package io.invok.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.invok.core.model.ApiTool;
import io.invok.core.model.ParameterType;
import io.invok.core.model.ToolParameter;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenApiService {

    public Map<String, Object> generateOpenApiSpec(String serverUrl, List<ApiTool> tools) {
        log.info("Compiling dynamic OpenAPI spec for {} tools. Base URL: {}", tools.size(), serverUrl);

        Map<String, Object> spec = new LinkedHashMap<>();
        spec.put("openapi", "3.0.1");

        // Info block
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("title", "Invok Dynamic APIs");
        info.put("description", "Dynamically compiled OpenAPI specification for automation platforms (n8n, Make, Zapier)");
        info.put("version", "1.0.0");
        spec.put("info", info);

        // Servers block
        Map<String, Object> server = new LinkedHashMap<>();
        server.put("url", serverUrl);
        spec.put("servers", List.of(server));

        // Paths block
        Map<String, Object> paths = new LinkedHashMap<>();
        for (ApiTool tool : tools) {
            String pathKey = "/api/v1/execute/" + tool.getCode();
            Map<String, Object> pathItem = new LinkedHashMap<>();
            Map<String, Object> postOperation = new LinkedHashMap<>();

            postOperation.put("summary", tool.getName() != null ? tool.getName() : tool.getCode());
            postOperation.put("description", tool.getDescription() != null ? tool.getDescription() : "");
            postOperation.put("operationId", tool.getCode());

            // Build requestBody schema matching parameters
            if (tool.getParameters() != null && !tool.getParameters().isEmpty()) {
                Map<String, Object> requestBody = new LinkedHashMap<>();
                Map<String, Object> content = new LinkedHashMap<>();
                Map<String, Object> jsonMediaType = new LinkedHashMap<>();
                Map<String, Object> schema = new LinkedHashMap<>();

                schema.put("type", "object");
                Map<String, Object> properties = new LinkedHashMap<>();
                List<String> requiredParams = new ArrayList<>();

                for (ToolParameter param : tool.getParameters()) {
                    Map<String, Object> paramSchema = new LinkedHashMap<>();
                    paramSchema.put("type", mapParameterType(param.getType()));
                    if (param.getDescription() != null) {
                        paramSchema.put("description", param.getDescription());
                    }
                    if (param.getDefaultValue() != null && !param.getDefaultValue().isBlank()) {
                        paramSchema.put("default", param.getDefaultValue());
                    }
                    properties.put(param.getName(), paramSchema);

                    if (param.getRequired() != null && param.getRequired()) {
                        requiredParams.add(param.getName());
                    }
                }

                schema.put("properties", properties);
                if (!requiredParams.isEmpty()) {
                    schema.put("required", requiredParams);
                }

                jsonMediaType.put("schema", schema);
                content.put("application/json", jsonMediaType);
                requestBody.put("content", content);
                requestBody.put("required", true);
                postOperation.put("requestBody", requestBody);
            }

            // Responses block
            Map<String, Object> responses = new LinkedHashMap<>();
            Map<String, Object> okResponse = new LinkedHashMap<>();
            okResponse.put("description", "Successful execution");
            Map<String, Object> responseContent = new LinkedHashMap<>();
            Map<String, Object> responseJsonMediaType = new LinkedHashMap<>();
            Map<String, Object> responseSchema = new LinkedHashMap<>();
            responseSchema.put("type", "object");
            responseJsonMediaType.put("schema", responseSchema);
            responseContent.put("application/json", responseJsonMediaType);
            okResponse.put("content", responseContent);
            responses.put("200", okResponse);

            postOperation.put("responses", responses);
            pathItem.put("post", postOperation);
            paths.put(pathKey, pathItem);
        }
        spec.put("paths", paths);

        return spec;
    }

    private String mapParameterType(ParameterType type) {
        if (type == null) return "string";
        return switch (type) {
            case NUMBER -> "number";
            case BOOLEAN -> "boolean";
            case OBJECT -> "object";
            case ARRAY -> "array";
            default -> "string"; // STRING, FILE
        };
    }
}
