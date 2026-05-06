package io.invok.core.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import io.invok.core.dto.ToolDefinition;
import io.invok.core.dto.ToolDiscoveryResponse;
import io.invok.core.dto.ToolExecuteRequest;
import io.invok.core.dto.ToolExecuteResponse;
import io.invok.core.dto.mcp.RpcDispatchOutcome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@ExtendWith(MockitoExtension.class)
class McpRpcHandlerServiceTest {

    @Mock
    private ToolDiscoveryService toolDiscoveryService;

    @Mock
    private ToolExecutionService toolExecutionService;

    private McpRpcHandlerService mcpRpcHandlerService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setVersionFields() {
        mcpRpcHandlerService = new McpRpcHandlerService(toolDiscoveryService, toolExecutionService, objectMapper);
        ReflectionTestUtils.setField(mcpRpcHandlerService, "serverDisplayName", "Invok");
        ReflectionTestUtils.setField(mcpRpcHandlerService, "applicationVersion", "test");
    }

    @Test
    void dispatchJsonRpc_initialize_returnsProtocolAndServerInfo() throws Exception {
        ObjectNode req = objectMapper.createObjectNode();
        req.put("jsonrpc", "2.0");
        req.put("method", "initialize");
        req.set("params", objectMapper.createObjectNode().put("protocolVersion", "2025-11-25"));
        req.put("id", 1);

        RpcDispatchOutcome outcome = mcpRpcHandlerService.dispatchJsonRpc(req);
        assertInstanceOf(RpcDispatchOutcome.JsonRpcEnvelope.class, outcome);
        JsonNode body = ((RpcDispatchOutcome.JsonRpcEnvelope) outcome).body();
        assertEquals("2.0", body.get("jsonrpc").asText());
        assertEquals(1, body.get("id").asInt());
        assertEquals("2025-11-25", body.get("result").get("protocolVersion").asText());
        assertEquals("Invok", body.get("result").get("serverInfo").get("name").asText());
    }

    @Test
    void dispatchJsonRpc_toolsList_delegatesToDiscovery() throws Exception {
        when(toolDiscoveryService.discoverTools()).thenReturn(new ToolDiscoveryResponse(List.of(
                        new ToolDefinition("t1", "d1", "api_tool", Map.of("type", "object", "properties", Map.of()))),
                        1,
                        Instant.now()));

        ObjectNode req = objectMapper.createObjectNode();
        req.put("jsonrpc", "2.0");
        req.put("method", "tools/list");
        req.put("id", "a");

        RpcDispatchOutcome outcome = mcpRpcHandlerService.dispatchJsonRpc(req);
        JsonNode body = ((RpcDispatchOutcome.JsonRpcEnvelope) outcome).body();
        assertTrue(body.get("result").get("tools").isArray());
        assertEquals("t1", body.get("result").get("tools").get(0).get("name").asText());
    }

    @Test
    void dispatchJsonRpc_toolsCall_delegatesToExecution() throws Exception {
        when(toolExecutionService.executeApiTool(any(ToolExecuteRequest.class)))
                        .thenReturn(new ToolExecuteResponse(true, "ok", 1L, "api_tool", null));

        ObjectNode req = objectMapper.createObjectNode();
        req.put("jsonrpc", "2.0");
        req.put("method", "tools/call");
        req.put("id", 2);
        ObjectNode params = objectMapper.createObjectNode();
        params.put("name", "mytool");
        params.set("arguments", objectMapper.createObjectNode().put("x", "y"));
        req.set("params", params);

        RpcDispatchOutcome outcome = mcpRpcHandlerService.dispatchJsonRpc(req);
        JsonNode body = ((RpcDispatchOutcome.JsonRpcEnvelope) outcome).body();
        assertEquals("text", body.get("result").get("content").get(0).get("type").asText());
        assertTrue(body.get("result").get("content").get(0).get("text").asText().contains("ok"));
    }

    @Test
    void dispatchJsonRpc_notification_returnsAccepted() {
        ObjectNode req = objectMapper.createObjectNode();
        req.put("jsonrpc", "2.0");
        req.put("method", "notifications/initialized");

        RpcDispatchOutcome outcome = mcpRpcHandlerService.dispatchJsonRpc(req);
        assertInstanceOf(RpcDispatchOutcome.AcceptedNotification.class, outcome);
    }

    @Test
    void dispatchJsonRpc_unknownMethod_returnsError() {
        ObjectNode req = objectMapper.createObjectNode();
        req.put("jsonrpc", "2.0");
        req.put("method", "unknown/method");
        req.put("id", 0);

        RpcDispatchOutcome outcome = mcpRpcHandlerService.dispatchJsonRpc(req);
        JsonNode body = ((RpcDispatchOutcome.JsonRpcEnvelope) outcome).body();
        assertEquals(-32601, body.get("error").get("code").asInt());
    }

    @Test
    void supportedProtocolVersion_acceptsNullAndKnown() {
        assertTrue(McpRpcHandlerService.isSupportedMcpProtocolVersion(null));
        assertTrue(McpRpcHandlerService.isSupportedMcpProtocolVersion("2025-11-25"));
        assertFalse(McpRpcHandlerService.isSupportedMcpProtocolVersion("2099-01-01"));
    }
}
