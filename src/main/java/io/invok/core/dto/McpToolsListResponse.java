package io.invok.core.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record McpToolsListResponse(
        List<McpTool> tools) {
}
