package io.invok.core.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.invok.core.dto.McpContent;
import io.invok.core.dto.McpError;
import io.invok.core.dto.McpResponse;
import io.invok.core.dto.McpTool;
import io.invok.core.dto.McpToolCallRequest;
import io.invok.core.dto.McpToolCallResponse;
import io.invok.core.dto.McpToolsListResponse;
import io.invok.core.dto.ToolDefinition;
import io.invok.core.dto.ToolDiscoveryResponse;
import io.invok.core.dto.ToolExecuteRequest;
import io.invok.core.dto.ToolExecuteResponse;
import io.invok.core.dto.mcp.McpInitializeResult;
import io.invok.core.dto.mcp.McpServerInfo;
import io.invok.core.dto.mcp.RpcDispatchOutcome;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Shared MCP JSON-RPC handling for Streamable HTTP and legacy REST-shaped MCP routes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class McpRpcHandlerService {

    public static final String DEFAULT_NEGOTIATED_PROTOCOL_VERSION = "2025-03-26";

    private static final Set<String> SUPPORTED_PROTOCOL_VERSIONS = Set.of(
            "2025-11-25",
            "2025-06-18",
            "2025-03-26",
            "2024-11-05");

    private final ToolDiscoveryService toolDiscoveryService;
    private final ToolExecutionService toolExecutionService;
    private final ObjectMapper objectMapper;

    @Value("${invok.mcp.server-name:Invok OSS}")
    private String serverDisplayName;

    @Value("${invok.version:1.0.0}")
    private String applicationVersion;

    /**
     * Legacy GET /mcp/tools/list — sin {@code id} JSON-RPC (compat bridge).
     */
    public McpResponse<McpToolsListResponse> listToolsLegacy() {
        try {
                ToolDiscoveryResponse result = toolDiscoveryService.discoverTools();
                McpToolsListResponse mcpResult = convertToMcpToolsList(result);
                return McpResponse.<McpToolsListResponse>builder()
                                .jsonrpc("2.0")
                                .result(mcpResult)
                                .build();
        } catch (Exception ex) {
                log.warn("MCP legacy tools/list failed", ex);
                return McpResponse.<McpToolsListResponse>builder()
                                .jsonrpc("2.0")
                                .error(McpError.builder()
                                                .code(-32603)
                                                .message("Internal error discovering tools")
                                                .build())
                                .build();
        }
    }

    /**
     * Legacy POST /mcp/tools/call.
     */
    public McpResponse<McpToolCallResponse> callToolLegacy(McpToolCallRequest request) {
        if (request == null || request.params() == null) {
                return McpResponse.<McpToolCallResponse>builder()
                                .jsonrpc("2.0")
                                .error(McpError.builder()
                                                .code(-32602)
                                                .message("Invalid params: missing required parameters")
                                                .build())
                                .id(request != null ? request.id() : null)
                                .build();
        }
        try {
                ToolExecuteRequest toolRequest = new ToolExecuteRequest(
                                request.params().name(),
                                request.params().arguments(),
                                null);
                ToolExecuteResponse response = toolExecutionService.executeApiTool(toolRequest);
                McpToolCallResponse mcpResult = convertToMcpToolCall(response);
                return McpResponse.<McpToolCallResponse>builder()
                                .jsonrpc("2.0")
                                .result(mcpResult)
                                .id(request.id())
                                .build();
        } catch (Exception ex) {
                log.warn("MCP legacy tools/call failed", ex);
                return McpResponse.<McpToolCallResponse>builder()
                                .jsonrpc("2.0")
                                .error(McpError.builder()
                                                .code(getErrorCode(ex))
                                                .message(getErrorMessage(ex))
                                                .build())
                                .id(request.id())
                                .build();
        }
    }

    /**
     * Despacho JSON-RPC para Streamable HTTP (cuerpo = un solo mensaje).
     */
    public RpcDispatchOutcome dispatchJsonRpc(JsonNode root) {
        if (root == null || !root.isObject()) {
                return new RpcDispatchOutcome.JsonRpcEnvelope(
                                jsonRpcError(null, -32700, "Parse error"));
        }
        if (!root.path("jsonrpc").asText("").equals("2.0")) {
                return new RpcDispatchOutcome.JsonRpcEnvelope(
                                jsonRpcError(idNode(root), -32600, "Invalid Request"));
        }
        String method = root.path("method").asText(null);
        if (method == null || method.isBlank()) {
                return new RpcDispatchOutcome.JsonRpcEnvelope(
                                jsonRpcError(idNode(root), -32600, "Invalid Request"));
        }

        JsonNode id = idNode(root);
        boolean notification = isNotification(root);

        if (notification) {
                return new RpcDispatchOutcome.AcceptedNotification();
        }

        return switch (method) {
                case "initialize" -> new RpcDispatchOutcome.JsonRpcEnvelope(handleInitialize(root, id));
                case "tools/list" -> new RpcDispatchOutcome.JsonRpcEnvelope(handleToolsList(id));
                case "tools/call" -> new RpcDispatchOutcome.JsonRpcEnvelope(handleToolsCall(root, id));
                case "ping" -> new RpcDispatchOutcome.JsonRpcEnvelope(
                                success(objectMapper.valueToTree(Map.of()), id));
                default -> new RpcDispatchOutcome.JsonRpcEnvelope(
                                jsonRpcError(id, -32601, "Method not found: " + method));
        };
    }

    public static boolean isSupportedMcpProtocolVersion(String headerValue) {
        if (headerValue == null || headerValue.isBlank()) {
                return true;
        }
        return SUPPORTED_PROTOCOL_VERSIONS.contains(headerValue.trim());
    }

    public static String negotiateProtocolVersion(JsonNode initializeParams) {
        if (initializeParams == null || !initializeParams.has("protocolVersion")) {
                return DEFAULT_NEGOTIATED_PROTOCOL_VERSION;
        }
        String requested = initializeParams.get("protocolVersion").asText(null);
        if (requested == null || requested.isBlank()) {
                return DEFAULT_NEGOTIATED_PROTOCOL_VERSION;
        }
        if (SUPPORTED_PROTOCOL_VERSIONS.contains(requested)) {
                return requested;
        }
        return DEFAULT_NEGOTIATED_PROTOCOL_VERSION;
    }

    private JsonNode handleInitialize(JsonNode root, JsonNode id) {
        JsonNode params = root.get("params");
        String negotiated = negotiateProtocolVersion(params);
        Map<String, Object> capabilities = Map.of(
                        "tools", Map.of());
        McpInitializeResult result = McpInitializeResult.builder()
                        .protocolVersion(negotiated)
                        .capabilities(capabilities)
                        .serverInfo(McpServerInfo.builder()
                                        .name(serverDisplayName)
                                        .version(applicationVersion)
                                        .build())
                        .build();
        return success(objectMapper.valueToTree(result), id);
    }

    private JsonNode handleToolsList(JsonNode id) {
        try {
                ToolDiscoveryResponse result = toolDiscoveryService.discoverTools();
                McpToolsListResponse mcpResult = convertToMcpToolsList(result);
                return success(objectMapper.valueToTree(mcpResult), id);
        } catch (Exception ex) {
                log.warn("MCP tools/list failed", ex);
                return jsonRpcError(id, -32603, "Internal error discovering tools");
        }
    }

    private JsonNode handleToolsCall(JsonNode root, JsonNode id) {
        JsonNode params = root.get("params");
        if (params == null || !params.isObject()) {
                return jsonRpcError(id, -32602, "Invalid params: missing params object");
        }
        String name = params.path("name").asText(null);
        if (name == null || name.isBlank()) {
                return jsonRpcError(id, -32602, "Invalid params: missing tool name");
        }
        Map<String, Object> arguments = readArgumentsMap(params.get("arguments"));
        try {
                ToolExecuteRequest toolRequest = new ToolExecuteRequest(name, arguments, null);
                ToolExecuteResponse response = toolExecutionService.executeApiTool(toolRequest);
                McpToolCallResponse mcpResult = convertToMcpToolCall(response);
                return success(objectMapper.valueToTree(mcpResult), id);
        } catch (Exception ex) {
                log.warn("MCP tools/call failed", ex);
                return jsonRpcError(id, getErrorCode(ex), getErrorMessage(ex));
        }
    }

    private Map<String, Object> readArgumentsMap(JsonNode argumentsNode) {
        if (argumentsNode == null || argumentsNode.isNull()) {
                return Map.of();
        }
        if (!argumentsNode.isObject()) {
                throw new IllegalArgumentException("arguments must be a JSON object");
        }
        return objectMapper.convertValue(argumentsNode, new TypeReference<Map<String, Object>>() {
        });
    }

    private JsonNode success(JsonNode result, JsonNode id) {
        ObjectNode out = objectMapper.createObjectNode();
        out.put("jsonrpc", "2.0");
        out.set("result", result);
        out.set("id", id);
        return out;
    }

    private JsonNode jsonRpcError(JsonNode id, int code, String message) {
        ObjectNode out = objectMapper.createObjectNode();
        out.put("jsonrpc", "2.0");
        ObjectNode err = objectMapper.createObjectNode();
        err.put("code", code);
        err.put("message", message);
        out.set("error", err);
        if (id != null && !id.isNull() && !id.isMissingNode()) {
                out.set("id", id);
        } else {
                out.putNull("id");
        }
        return out;
    }

    private static JsonNode idNode(JsonNode root) {
        if (!root.has("id")) {
                return null;
        }
        return root.get("id");
    }

    private static boolean isNotification(JsonNode root) {
        if (!root.has("id")) {
                return true;
        }
        return root.get("id").isNull();
    }

    private McpToolsListResponse convertToMcpToolsList(ToolDiscoveryResponse response) {
        List<McpTool> mcpTools = response.tools().stream()
                        .map(this::convertToMcpTool)
                        .toList();
        return McpToolsListResponse.builder()
                        .tools(mcpTools)
                        .build();
    }

    private McpTool convertToMcpTool(ToolDefinition toolDef) {
        return McpTool.builder()
                        .name(toolDef.name())
                        .description(toolDef.description())
                        .inputSchema(toolDef.parameters())
                        .build();
    }

    private McpToolCallResponse convertToMcpToolCall(ToolExecuteResponse response) {
        String textContent = response.success()
                        ? (response.result() != null ? response.result().toString() : "")
                        : (response.errorMessage() != null ? response.errorMessage()
                                        : "Error ejecutando herramienta");
        McpContent content = McpContent.builder()
                        .type("text")
                        .text(textContent)
                        .build();
        return McpToolCallResponse.builder()
                        .content(List.of(content))
                        .build();
    }

    private int getErrorCode(Throwable ex) {
        if (ex instanceof IllegalArgumentException) {
                return -32602;
        }
        return -32603;
    }

    private String getErrorMessage(Throwable ex) {
        if (ex instanceof IllegalArgumentException) {
                return "Invalid params: " + ex.getMessage();
        }
        return "Internal error: " + ex.getMessage();
    }
}
