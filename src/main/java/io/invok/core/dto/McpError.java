package io.invok.core.dto;

import lombok.Builder;

@Builder
public record McpError(
        int code,
        String message) {
}
