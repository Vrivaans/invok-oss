package io.invok.core.dto;

import io.invok.core.model.ParameterType;

public record ImportToolParameterRequest(
                String name,
                ParameterType type,
                String description,
                Boolean required,
                String defaultValue) {
}
