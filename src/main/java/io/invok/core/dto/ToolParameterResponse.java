package io.invok.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.invok.core.model.ParameterType;
import io.invok.core.model.ToolParameter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ToolParameterResponse(
        Long id,
        String code,
        String name,
        ParameterType type,
        String description,
        Boolean required,
        String defaultValue
) {
    public static ToolParameterResponse from(ToolParameter parameter) {
        return new ToolParameterResponse(
                parameter.getId(),
                parameter.getCode(),
                parameter.getName(),
                parameter.getType(),
                parameter.getDescription(),
                parameter.getRequired(),
                parameter.getDefaultValue()
        );
    }
}
