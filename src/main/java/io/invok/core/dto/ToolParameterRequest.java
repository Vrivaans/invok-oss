package io.invok.core.dto;

import io.invok.core.model.ParameterType;

public record ToolParameterRequest(
    String name,
    ParameterType type,
    String description,
    Boolean required,
    String defaultValue
) {}
