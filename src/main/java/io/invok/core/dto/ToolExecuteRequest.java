package io.invok.core.dto;

import java.util.Map;

public record ToolExecuteRequest(
        String toolName,
        Map<String, Object> parameters,
        String sessionId) {
}
