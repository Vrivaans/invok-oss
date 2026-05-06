package io.invok.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.invok.core.dto.ImportApiProviderRequest;
import io.invok.core.dto.ImportApiToolRequest;
import io.invok.core.dto.ImportToolParameterRequest;
import io.invok.core.model.HttpMethodEnum;
import io.invok.core.model.ParameterType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenApiImportService {

    private final ImportService importService;
    private final ObjectMapper objectMapper;

    public Map<String, Object> importOpenApi(JsonNode openApiSpec) {
        List<ImportApiProviderRequest> providers = parseOpenApi(openApiSpec);
        importService.importProviders(providers);
        return Map.of(
                "message", "OpenAPI import successful",
                "providers", providers.size(),
                "tools", providers.stream().mapToInt(p -> p.tools() != null ? p.tools().size() : 0).sum());
    }

    private List<ImportApiProviderRequest> parseOpenApi(JsonNode spec) {
        JsonNode info = spec.path("info");
        String providerName = info.path("title").asText("Imported API");

        String baseUrl = extractBaseUrl(spec);

        JsonNode paths = spec.path("paths");
        if (paths.isMissingNode() || paths.isEmpty()) {
            log.warn("No paths found in OpenAPI spec");
            return List.of();
        }

        List<ImportApiToolRequest> tools = new ArrayList<>();
        Iterator<Map.Entry<String, JsonNode>> pathIterator = paths.fields();
        while (pathIterator.hasNext()) {
            Map.Entry<String, JsonNode> pathEntry = pathIterator.next();
            String path = pathEntry.getKey();
            JsonNode methods = pathEntry.getValue();

            if (methods.isObject()) {
                Iterator<Map.Entry<String, JsonNode>> methodIterator = methods.fields();
                while (methodIterator.hasNext()) {
                    Map.Entry<String, JsonNode> methodEntry = methodIterator.next();
                    String httpMethodName = methodEntry.getKey().toUpperCase();
                    JsonNode operation = methodEntry.getValue();

                    HttpMethodEnum httpMethod = switch (httpMethodName) {
                        case "GET" -> HttpMethodEnum.GET;
                        case "POST" -> HttpMethodEnum.POST;
                        case "PUT" -> HttpMethodEnum.PUT;
                        case "DELETE" -> HttpMethodEnum.DELETE;
                        case "PATCH" -> HttpMethodEnum.PATCH;
                        default -> null;
                    };

                    if (httpMethod == null) continue;

                    String operationId = operation.path("operationId").asText(null);
                    String summary = operation.path("summary").asText(null);
                    String description = operation.path("description").asText(null);
                    String toolName = summary != null ? summary : (operationId != null ? operationId : httpMethodName + " " + path);
                    String toolCode = operationId != null ? operationId : httpMethodName.toLowerCase() + "_" + sanitizeCode(path);

                    List<ImportToolParameterRequest> parameters = extractParameters(operation, path);

                    tools.add(new ImportApiToolRequest(
                            toolName,
                            toolCode,
                            description != null ? description : toolName,
                            path,
                            null,
                            httpMethod,
                            parameters));
                }
            }
        }

        String providerCode = sanitizeCode(providerName);

        ImportApiProviderRequest provider = new ImportApiProviderRequest(
                providerName,
                providerCode,
                baseUrl,
                null,
                null,
                null,
                null,
                false,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                tools,
                null,
                null,
                null);

        return List.of(provider);
    }

    private String extractBaseUrl(JsonNode spec) {
        JsonNode servers = spec.path("servers");
        if (servers.isArray() && !servers.isEmpty()) {
            return servers.get(0).path("url").asText("");
        }

        String host = spec.path("host").asText(null);
        if (host != null) {
            String basePath = spec.path("basePath").asText("");
            String scheme = spec.path("schemes").isArray() && !spec.path("schemes").isEmpty()
                    ? spec.path("schemes").get(0).asText("https")
                    : "https";
            return scheme + "://" + host + basePath;
        }

        return "http://localhost:8080";
    }

    private List<ImportToolParameterRequest> extractParameters(JsonNode operation, String path) {
        List<ImportToolParameterRequest> params = new ArrayList<>();

        // Path parameters from operation
        JsonNode parametersNode = operation.path("parameters");
        if (parametersNode.isArray()) {
            for (JsonNode param : parametersNode) {
                String in = param.path("in").asText("");
                params.add(new ImportToolParameterRequest(
                        param.path("name").asText(""),
                        mapParameterType(param.path("type").asText("string"), param.path("schema")),
                        param.path("description").asText(""),
                        "query".equals(in) ? false : param.path("required").asBoolean(false),
                        param.path("default").asText(null)));
            }
        }

        // Request body parameter
        if (operation.has("requestBody")) {
            JsonNode requestBody = operation.path("requestBody");
            String bodyDescription = requestBody.path("description").asText("Request body");
            params.add(new ImportToolParameterRequest(
                    "body",
                    ParameterType.STRING,
                    bodyDescription,
                    false,
                    null));
        }

        return params;
    }

    private ParameterType mapParameterType(String type, JsonNode schema) {
        if (schema != null && !schema.isMissingNode()) {
            String schemaType = schema.path("type").asText(type);
            return switch (schemaType) {
                case "integer", "number" -> ParameterType.NUMBER;
                case "boolean" -> ParameterType.BOOLEAN;
                case "array" -> ParameterType.ARRAY;
                default -> ParameterType.STRING;
            };
        }
        return switch (type) {
            case "integer", "number" -> ParameterType.NUMBER;
            case "boolean" -> ParameterType.BOOLEAN;
            case "array" -> ParameterType.ARRAY;
            default -> ParameterType.STRING;
        };
    }

    private String sanitizeCode(String input) {
        return input.toLowerCase()
            .replaceAll("[^a-z0-9]+", "_")
            .replaceAll("^_|_$", "");
    }
}
