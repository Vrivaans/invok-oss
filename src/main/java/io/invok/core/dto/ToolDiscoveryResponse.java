package io.invok.core.dto;

import java.time.Instant;
import java.util.List;

public record ToolDiscoveryResponse(
    List<ToolDefinition> tools,
    int totalCount,
    Instant lastUpdated
) {}
