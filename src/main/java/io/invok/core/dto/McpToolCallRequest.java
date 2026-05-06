package io.invok.core.dto;

public record McpToolCallRequest(
        String jsonrpc,
        String method,
        McpToolCallParams params,
        String id) {
}
