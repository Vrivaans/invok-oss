package io.invok.core.dto.mcp;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record McpServerInfo(
        String name,
        String version) {
}
