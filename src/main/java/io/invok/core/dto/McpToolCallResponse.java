package io.invok.core.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record McpToolCallResponse(
        List<McpContent> content) {
}
