package io.invok.core.dto;

import java.util.Map;

public record McpToolCallParams(
        String name,
        Map<String, Object> arguments) {
}
