package io.invok.core.dto;

import lombok.Builder;

@Builder
public record McpContent(
        String type,
        String text) {
}
