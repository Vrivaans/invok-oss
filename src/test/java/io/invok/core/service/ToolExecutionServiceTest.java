package io.invok.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.invok.core.dto.ToolExecuteRequest;
import io.invok.core.dto.ToolExecuteResponse;
import io.invok.core.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ToolExecutionServiceTest {

    private MockRestServiceServer mockServer;

    @Mock
    private ApiToolService apiToolService;
    @Mock
    private ToolCacheManager toolCacheManager;
    @Mock
    private RestClient.Builder restClientBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private EncryptionService encryptionService;
    @Mock
    private io.invok.core.util.LogObfuscator logObfuscator;
    @Mock
    private DynamicTokenManager dynamicTokenManager;
    @Mock
    private io.invok.core.util.SecuritySanitizer securitySanitizer;
    @Mock
    private io.invok.core.util.DataEgressScrubber dataEgressScrubber;

    private ToolExecutionService service;
    private ApiTool tool;
    private ApiProvider provider;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);

        RestClient restClient = RestClient.create(restTemplate);
        when(restClientBuilder.baseUrl(anyString())).thenReturn(restClientBuilder);
        when(restClientBuilder.build()).thenReturn(restClient);

        service = new ToolExecutionService(
                apiToolService,
                toolCacheManager,
                restClientBuilder,
                objectMapper,
                encryptionService,
                logObfuscator,
                dynamicTokenManager,
                securitySanitizer,
                dataEgressScrubber);

        provider = new ApiProvider();
        provider.setId(10L);
        provider.setBaseUrl("https://api.test.com");
        provider.setAuthenticationType(AuthenticationTypeEnum.API_KEY);
        provider.setDynamicAuth(true);
        provider.setApiKeyLocation(ApiKeyLocationEnum.HEADER);
        provider.setApiKeyName("Authorization");

        tool = new ApiTool();
        tool.setId(100L);
        tool.setCode("TEST-TOOL");
        tool.setEnabled(true);
        tool.setHealthy(true);
        tool.setProvider(provider);
        tool.setEndpointPath("/data");
        tool.setHttpMethod(HttpMethodEnum.GET);
    }

    @Test
    void testExecuteApiTool_WithDynamicAuth_401TriggerRetrySuccess() {
        when(toolCacheManager.getCachedTool("TEST-TOOL")).thenReturn(Optional.of(tool));
        when(dynamicTokenManager.getToken(provider)).thenReturn("first-stale-token", "second-fresh-token");

        mockServer.expect(MockRestRequestMatchers.requestTo("https://api.test.com/data"))
                .andExpect(MockRestRequestMatchers.header("Authorization", "first-stale-token"))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.UNAUTHORIZED));

        mockServer.expect(MockRestRequestMatchers.requestTo("https://api.test.com/data"))
                .andExpect(MockRestRequestMatchers.header("Authorization", "second-fresh-token"))
                .andRespond(MockRestResponseCreators.withSuccess("{\"result\":\"ok\"}", MediaType.APPLICATION_JSON));

        when(securitySanitizer.sanitizeToolResponse(any()))
                .thenReturn("<UntrustedExternalContent>\n{\"result\":\"ok\"}\n</UntrustedExternalContent>");

        ToolExecuteRequest request = new ToolExecuteRequest("TEST-TOOL", new HashMap<>(), "my-session-id");
        ToolExecuteResponse response = service.executeApiTool(request);

        assertNotNull(response);
        if (response.errorMessage() != null) {
            System.err.println("Unexpected error message: " + response.errorMessage());
        }
        assertTrue(response.success());
        verify(dynamicTokenManager, times(1)).invalidateToken(10L);
        mockServer.verify();
    }

    @Test
    void testExecuteApiTool_WithDynamicAuth_401TriggerRetryFailsAgain() {
        when(toolCacheManager.getCachedTool("TEST-TOOL")).thenReturn(Optional.of(tool));
        when(dynamicTokenManager.getToken(provider)).thenReturn("first-token", "second-token");

        mockServer.expect(MockRestRequestMatchers.requestTo("https://api.test.com/data"))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.UNAUTHORIZED));

        mockServer.expect(MockRestRequestMatchers.requestTo("https://api.test.com/data"))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.UNAUTHORIZED));

        when(dataEgressScrubber.scrubParameters(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ToolExecuteRequest request = new ToolExecuteRequest("TEST-TOOL", new HashMap<>(), "my-session-id");
        ToolExecuteResponse response = service.executeApiTool(request);

        assertFalse(response.success());
        assertNotNull(response.errorMessage());
        assertTrue(response.errorMessage().contains("401"));

        verify(dynamicTokenManager, times(1)).invalidateToken(10L);
        mockServer.verify();
    }

    @Test
    void testExecuteApiTool_WithSpecialCharactersInUri_EscapesCorrectly() {
        tool.setEndpointPath("/search");
        when(toolCacheManager.getCachedTool("TEST-TOOL")).thenReturn(Optional.of(tool));
        when(dynamicTokenManager.getToken(provider)).thenReturn("token");

        java.util.Map<String, Object> params = new HashMap<>();
        params.put("query", "hello world {special}");

        mockServer.expect(MockRestRequestMatchers.requestTo(org.hamcrest.Matchers.containsString("query=hello+world+%7Bspecial%7D")))
                .andRespond(MockRestResponseCreators.withSuccess("{\"status\":\"ok\"}", MediaType.APPLICATION_JSON));

        when(securitySanitizer.sanitizeToolResponse(any())).thenReturn("ok");
        when(dataEgressScrubber.scrubParameters(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ToolExecuteRequest request = new ToolExecuteRequest("TEST-TOOL", params, "sess");
        ToolExecuteResponse response = service.executeApiTool(request);

        assertTrue(response.success());
        mockServer.verify();
    }

    @Test
    void testExecuteApiTool_WithUnquotedPlaceholdersInTemplate_CleansUpCorrectly() {
        tool.setHttpMethod(HttpMethodEnum.POST);
        tool.setEndpointPath("/create");
        tool.setBodyPayloadTemplate("{\"name\": \"{{name}}\", \"limit\": {{limit}}}");
        
        when(toolCacheManager.getCachedTool("TEST-TOOL")).thenReturn(Optional.of(tool));
        when(dynamicTokenManager.getToken(provider)).thenReturn("token");

        java.util.Map<String, Object> params = new HashMap<>();
        params.put("name", "test-item");

        mockServer.expect(MockRestRequestMatchers.requestTo("https://api.test.com/create"))
                .andExpect(MockRestRequestMatchers.content().string(org.hamcrest.Matchers.containsString("\"name\":\"test-item\"")))
                .andExpect(MockRestRequestMatchers.content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("\"limit\""))))
                .andRespond(MockRestResponseCreators.withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        when(securitySanitizer.sanitizeToolResponse(any())).thenReturn("ok");
        when(dataEgressScrubber.scrubParameters(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ToolExecuteRequest request = new ToolExecuteRequest("TEST-TOOL", params, "sess");
        ToolExecuteResponse response = service.executeApiTool(request);

        assertTrue(response.success());
        mockServer.verify();
    }

    @Test
    void testExecuteApiTool_WithBaseUrlPathAndLeadingSlash_ResolvesCorrectly() {
        provider.setBaseUrl("https://bsky.social/xrpc");
        tool.setEndpointPath("/com.atproto.repo.createRecord");
        
        when(toolCacheManager.getCachedTool("TEST-TOOL")).thenReturn(Optional.of(tool));
        when(dynamicTokenManager.getToken(provider)).thenReturn("token");

        mockServer.expect(MockRestRequestMatchers.requestTo("https://bsky.social/xrpc/com.atproto.repo.createRecord"))
                .andRespond(MockRestResponseCreators.withSuccess("{}", MediaType.APPLICATION_JSON));

        when(securitySanitizer.sanitizeToolResponse(any())).thenReturn("ok");
        when(dataEgressScrubber.scrubParameters(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ToolExecuteRequest request = new ToolExecuteRequest("TEST-TOOL", new HashMap<>(), "sess");
        service.executeApiTool(request);

        mockServer.verify();
    }

    @Test
    void testExecuteApiTool_WithStructuredObjectParameter_ExtractsValue() {
        tool.setEndpointPath("/data");
        when(toolCacheManager.getCachedTool("TEST-TOOL")).thenReturn(Optional.of(tool));
        when(dynamicTokenManager.getToken(provider)).thenReturn("token");

        java.util.Map<String, Object> structuredParam = new HashMap<>();
        structuredParam.put("type", "string");
        structuredParam.put("value", "real-value");

        java.util.Map<String, Object> params = new HashMap<>();
        params.put("board_id", structuredParam);

        mockServer.expect(MockRestRequestMatchers.requestTo(org.hamcrest.Matchers.containsString("board_id=real-value")))
                .andRespond(MockRestResponseCreators.withSuccess("{}", MediaType.APPLICATION_JSON));

        when(securitySanitizer.sanitizeToolResponse(any())).thenReturn("ok");
        when(dataEgressScrubber.scrubParameters(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ToolExecuteRequest request = new ToolExecuteRequest("TEST-TOOL", params, "sess");
        service.executeApiTool(request);

        mockServer.verify();
    }
}
