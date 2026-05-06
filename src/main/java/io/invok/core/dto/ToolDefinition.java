package io.invok.core.dto;

import io.invok.core.model.ApiTool;

import java.util.Map;

public record ToolDefinition(
        String name,
        String description,
        String type,
        Map<String, Object> parameters) {
    public static ToolDefinition from(ApiTool apiTool) {
        // Crear la estructura de parámetros compatible con OpenAI function calling
        java.util.Map<String, Object> properties = apiTool.getParameters().stream()
                .collect(java.util.stream.Collectors.toMap(
                        param -> param.getName(),
                        param -> {
                            java.util.Map<String, Object> propMap = new java.util.HashMap<>();
                            String type = param.getType() != null ? param.getType().toString().toLowerCase() : "string";

                            if (param.getType() == io.invok.core.model.ParameterType.ARRAY) {
                                propMap.put("type", "array");
                                propMap.put("items", java.util.Map.of("type", "string"));
                            } else {
                                propMap.put("type", type);
                            }

                            if (param.getDescription() != null) {
                                propMap.put("description", param.getDescription());
                            }
                            return propMap;
                        }));

        Map<String, Object> parametersSchema = Map.of(
                "type", "object",
                "properties", properties,
                "required", apiTool.getParameters().stream()
                        .filter(param -> param.getRequired() != null && param.getRequired())
                        .map(param -> param.getName())
                        .collect(java.util.stream.Collectors.toList()));

        return new ToolDefinition(
                apiTool.getCode(),
                apiTool.getDescription(),
                "api_tool",
                parametersSchema);
    }
}
