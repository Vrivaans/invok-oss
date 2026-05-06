package io.invok.core.dto;

public record ToolExecuteResponse(
        boolean success,
        Object result,
        Long executionTimeMs,
        String toolType,
        String errorMessage) {
}
