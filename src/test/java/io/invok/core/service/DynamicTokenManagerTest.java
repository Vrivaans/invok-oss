package io.invok.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.invok.core.model.ApiProvider;
import io.invok.core.model.DynamicAuthMethodEnum;
import io.invok.core.model.DynamicAuthPayloadLocationEnum;
import io.invok.core.model.DynamicAuthPayloadTypeEnum;
import io.invok.core.repository.ApiProviderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.Matchers.startsWith;

@ExtendWith(MockitoExtension.class)
public class DynamicTokenManagerTest {

    private MockRestServiceServer mockServer;

    @Mock
    private EncryptionService encryptionService;

    @Mock
    private RestClient.Builder builderMock;

    @Mock
    private ApiProviderRepository apiProviderRepository;

    private DynamicTokenManager dynamicTokenManager;

    private ApiProvider provider;

    @BeforeEach
    void setUp() {
        // Use RestTemplate for MockRestServiceServer, then adapt to RestClient
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);

        // Pass a mock builder that just returns our bridged RestClient
        RestClient restClient = RestClient.create(restTemplate);

        org.mockito.Mockito.when(builderMock.baseUrl(org.mockito.ArgumentMatchers.anyString())).thenReturn(builderMock);
        org.mockito.Mockito.when(builderMock.build()).thenReturn(restClient);

        dynamicTokenManager = new DynamicTokenManager(builderMock, new ObjectMapper(), encryptionService,
                apiProviderRepository);

        provider = new ApiProvider();
        provider.setId(1L);
        provider.setDynamicAuth(true); // <--- THIS WAS MISSING
        provider.setDynamicAuthUrl("https://api.example.com/oauth/token");
        provider.setDynamicAuthMethod(DynamicAuthMethodEnum.POST);
        provider.setDynamicAuthPayloadLocation(DynamicAuthPayloadLocationEnum.BODY);
        provider.setDynamicAuthPayloadType(DynamicAuthPayloadTypeEnum.JSON);
        provider.setDynamicAuthPayload("{\"client_id\":\"123\", \"client_secret\":\"abc\"}");
        provider.setDynamicAuthTokenExtractionPath("access_token");
    }

    @Test
    void testGetToken_WithJsonResponse_ExtractsToken() {
        String mockResponseJson = "{\"access_token\":\"mocked-jwt-token\", \"expires_in\": 3600}";

        mockServer.expect(MockRestRequestMatchers.requestTo(startsWith("https://api.example.com/oauth/token")))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withSuccess(mockResponseJson, MediaType.APPLICATION_JSON));

        // First call fetches
        String token = dynamicTokenManager.getToken(provider);
        assertEquals("mocked-jwt-token", token);
        mockServer.verify();

        // Second call should hit the cache and not trigger expected request
        String cachedToken = dynamicTokenManager.getToken(provider);
        assertEquals("mocked-jwt-token", cachedToken);
    }

    @Test
    void testGetToken_WithFormDataPayload_ExtractsToken() {
        provider.setDynamicAuthPayloadType(DynamicAuthPayloadTypeEnum.FORM_DATA);

        String mockResponseJson = "{\"access_token\":\"form-data-token\"}";

        mockServer.expect(MockRestRequestMatchers.requestTo(startsWith("https://api.example.com/oauth/token")))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andExpect(MockRestRequestMatchers.content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED))
                .andRespond(MockRestResponseCreators.withSuccess(mockResponseJson, MediaType.APPLICATION_JSON));

        String token = dynamicTokenManager.getToken(provider);

        assertEquals("form-data-token", token);
        mockServer.verify();
    }

    @Test
    void testGetToken_EmptyExtractionPath_ReturnsRawResponse() {
        provider.setDynamicAuthTokenExtractionPath("");
        String mockRawResponse = "raw-text-token-12345";

        mockServer.expect(MockRestRequestMatchers.requestTo(startsWith("https://api.example.com/oauth/token")))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withSuccess(mockRawResponse, MediaType.TEXT_PLAIN));

        String token = dynamicTokenManager.getToken(provider);

        assertEquals("raw-text-token-12345", token);
        mockServer.verify();
    }

    @Test
    void testInvalidateToken_ClearsCache() {
        String mockResponseJson = "{\"access_token\":\"token-to-invalidate\"}";

        // Expectation 1
        mockServer.expect(MockRestRequestMatchers.requestTo(startsWith("https://api.example.com/oauth/token")))
                .andRespond(MockRestResponseCreators.withSuccess(mockResponseJson, MediaType.APPLICATION_JSON));

        // Fetch once to cache
        dynamicTokenManager.getToken(provider);
        mockServer.verify(); // verify request

        // Invalidate
        dynamicTokenManager.invalidateToken(provider.getId());

        // Expectation 2 (reset mock server for next expect)
        mockServer.reset();
        mockServer.expect(MockRestRequestMatchers.requestTo(startsWith("https://api.example.com/oauth/token")))
                .andRespond(MockRestResponseCreators.withSuccess(mockResponseJson, MediaType.APPLICATION_JSON));

        // Fetch again, should trigger request 2
        dynamicTokenManager.getToken(provider);
        mockServer.verify();
    }

    @Test
    void testGetToken_MixedPayload_HandlesPlaintext() {
        provider.setDynamicAuthPayload("{\"encrypted_key\":\"ENC(encrypted_val)\", \"plain_key\":\"plain_val\"}");
        
        // Simular que el encryptionService falla con el valor en texto plano
        org.mockito.Mockito.when(encryptionService.decrypt("ENC(encrypted_val)")).thenReturn("decrypted_val");
        org.mockito.Mockito.when(encryptionService.decrypt("plain_val")).thenThrow(new RuntimeException("Decryption failed"));

        String mockResponseJson = "{\"access_token\":\"mixed-payload-token\"}";

        // Verificamos que se envía el body correcto (uno desencriptado, el otro sin tocar)
        mockServer.expect(MockRestRequestMatchers.requestTo(startsWith("https://api.example.com/oauth/token")))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andExpect(MockRestRequestMatchers.content().json("{\"encrypted_key\":\"decrypted_val\", \"plain_key\":\"plain_val\"}"))
                .andRespond(MockRestResponseCreators.withSuccess(mockResponseJson, MediaType.APPLICATION_JSON));

        String token = dynamicTokenManager.getToken(provider);

        assertEquals("mixed-payload-token", token);
        mockServer.verify();
    }
}
