package io.invok.core.dto.mcp;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;

/**
 * JSON-RPC result for {@code initialize} (MCP).
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record McpInitializeResult(
        String protocolVersion,
        Map<String, Object> capabilities,
        McpServerInfo serverInfo) {
}
