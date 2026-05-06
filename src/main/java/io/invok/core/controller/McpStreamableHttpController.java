package io.invok.core.controller;

import io.invok.core.dto.mcp.RpcDispatchOutcome;
import io.invok.core.service.McpRpcHandlerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

import lombok.RequiredArgsConstructor;

/**
 * MCP Streamable HTTP: un solo path {@code /mcp} con POST (JSON-RPC) y sin SSE (solo JSON).
 * Convive con {@link MCPController} ({@code /mcp/tools/list}, {@code /mcp/tools/call}) para el bridge stdio.
 */
@RestController
@RequestMapping("/mcp")
@RequiredArgsConstructor
public class McpStreamableHttpController {

    private final McpRpcHandlerService mcpRpcHandlerService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postJsonRpc(
            @RequestBody JsonNode body,
            @RequestHeader(value = "MCP-Protocol-Version", required = false) String mcpProtocolVersion) {

        if (!McpRpcHandlerService.isSupportedMcpProtocolVersion(mcpProtocolVersion)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(Map.of(
                                                "error", "Unsupported MCP-Protocol-Version",
                                                "message", "Use one of: 2025-11-25, 2025-06-18, 2025-03-26, 2024-11-05"));
        }

        RpcDispatchOutcome outcome = mcpRpcHandlerService.dispatchJsonRpc(body);
        if (outcome instanceof RpcDispatchOutcome.AcceptedNotification) {
                return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }
        if (outcome instanceof RpcDispatchOutcome.JsonRpcEnvelope) {
                JsonNode rpcBody = ((RpcDispatchOutcome.JsonRpcEnvelope) outcome).body();
                return ResponseEntity.ok(rpcBody);
        }
        throw new IllegalStateException("Unhandled RpcDispatchOutcome");
    }

    /**
     * Sin stream SSE desde el servidor; los clientes pueden usar solo POST con respuestas JSON.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> getNotSupported() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                        .body(Map.of("error", "GET SSE not enabled; use POST with JSON-RPC"));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteSessionNotSupported() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
}
