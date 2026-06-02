package io.invok.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.invok.core.dto.ToolExecuteRequest;
import io.invok.core.dto.ToolExecuteResponse;
import io.invok.core.exception.ResourceNotFoundException;
import io.invok.core.exception.ToolExecutionException;
import io.invok.core.model.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
@RequiredArgsConstructor
public class ToolExecutionService {

    private final ApiToolService apiToolService;
    private final ToolCacheManager toolCacheManager;
    private final RestClient.Builder restClientBuilder;
    private final ObjectMapper objectMapper;
    private final EncryptionService encryptionService;
    private final io.invok.core.util.LogObfuscator logObfuscator;
    private final DynamicTokenManager dynamicTokenManager;
    private final io.invok.core.util.SecuritySanitizer securitySanitizer;
    private final io.invok.core.util.DataEgressScrubber dataEgressScrubber;

    public ToolExecuteResponse executeApiTool(ToolExecuteRequest request) {
        return executeApiTool(request, false);
    }

    public ToolExecuteResponse executeApiTool(ToolExecuteRequest request, boolean raw) {
        log.info("Executing tool: {} (raw={})", request.toolName(), raw);
        Instant startTime = Instant.now();

        Map<String, Object> safeParameters = dataEgressScrubber.scrubParameters(request.parameters());
        ToolExecuteRequest safeRequest = new ToolExecuteRequest(request.toolName(), safeParameters,
                request.sessionId());

        if ("invok_guide".equals(safeRequest.toolName())) {
            return handleInvokGuide();
        }

        try {
            ApiTool apiTool = toolCacheManager.getCachedTool(safeRequest.toolName())
                    .orElseGet(() -> {
                        try {
                            return apiToolService.getApiToolByCode(safeRequest.toolName());
                        } catch (Exception e) {
                            throw new ResourceNotFoundException("Tool not found: " + safeRequest.toolName());
                        }
                    });

            if (!apiTool.isEnabled() || !apiTool.isHealthy()) {
                throw new ToolExecutionException("Tool is disabled or unhealthy: " + safeRequest.toolName());
            }

            if (apiTool.getProvider().getAuthenticationType() == AuthenticationTypeEnum.OAUTH2_AUTHORIZATION_CODE) {
                String token = apiTool.getProvider().getApiKeyValue();
                if (token == null || token.isEmpty()) {
                    log.warn("Provider {} requires OAuth2 authorization; no access token stored yet.",
                            apiTool.getProvider().getName());
                    throw new ToolExecutionException("OAuth2 authorization is required for "
                            + apiTool.getProvider().getName()
                            + ". Complete login in the Invok UI, then retry this tool.");
                }
            }

            String dynamicToken = null;
            if (apiTool.getProvider().isDynamicAuth()) {
                dynamicToken = dynamicTokenManager.getToken(apiTool.getProvider());
            }

            Object result = null;
            try {
                result = executeApiCall(apiTool, safeRequest.parameters(), dynamicToken);
                if (apiTool.getProvider().isDynamicAuth() && isResultInvalid(result, apiTool.getProvider())) {
                    throw new HttpClientErrorException(org.springframework.http.HttpStatus.UNAUTHORIZED,
                            "Invalidated by keyword");
                }
            } catch (Exception e) {
                boolean isUnauthorized = (e instanceof HttpClientErrorException
                        && ((HttpClientErrorException) e).getStatusCode().value() == 401);
                boolean isKeywordInvalid = apiTool.getProvider().isDynamicAuth()
                        && isExceptionInvalid(e, apiTool.getProvider());

                if (apiTool.getProvider().isDynamicAuth() && (isUnauthorized || isKeywordInvalid)) {
                    log.warn("Dynamic token expired or invalid for provider {}, fetching new token and retrying",
                            apiTool.getProvider().getId());
                    dynamicTokenManager.invalidateToken(apiTool.getProvider().getId());
                    dynamicToken = dynamicTokenManager.getToken(apiTool.getProvider());
                    result = executeApiCall(apiTool, safeRequest.parameters(), dynamicToken);
                    if (isResultInvalid(result, apiTool.getProvider())) {
                        throw new ToolExecutionException(
                                "Tool execution failed even after token refresh due to invalidation keywords.");
                    }
                } else {
                    throw e;
                }
            }

            long executionTime = Duration.between(startTime, Instant.now()).toMillis();
            log.info("Tool execution successful: {} in {}ms", safeRequest.toolName(), executionTime);

            Object finalOutput = raw ? result : securitySanitizer.sanitizeToolResponse(result);

            return new ToolExecuteResponse(
                    true,
                    finalOutput,
                    executionTime,
                    "api_tool",
                    null);

        } catch (Exception e) {
            log.error("Error executing tool {}: {}", safeRequest.toolName(), e.getMessage());

            return new ToolExecuteResponse(
                    false,
                    null,
                    Duration.between(startTime, Instant.now()).toMillis(),
                    "api_tool",
                    e.getMessage());
        }
    }

    private Object executeApiCall(ApiTool apiTool, Map<String, Object> parameters, String dynamicToken) {
        RestClient client = restClientBuilder.baseUrl(apiTool.getProvider().getBaseUrl()).build();

        Map<String, Object> finalParameters = prepareParametersWithAuth(apiTool, parameters, dynamicToken);
        String uriPath = buildUriWithQueryParams(apiTool, finalParameters);

        if (uriPath.startsWith("/")) {
            uriPath = uriPath.substring(1);
        }

        String baseUrl = apiTool.getProvider().getBaseUrl();
        String fullUrl = baseUrl.endsWith("/") ? baseUrl + uriPath : baseUrl + "/" + uriPath;
        log.info("Executing API call to full URI: {}", fullUrl);

        HttpMethod httpMethod = convertHttpMethod(apiTool.getHttpMethod());

        // URI.create() es estricto: falla con caracteres ilegales (espacios, [], etc.) en query params.
        // UriComponentsBuilder con encoded=true parsea la URL ya-codificada de forma robusta.
        java.net.URI resolvedUri;
        try {
            resolvedUri = UriComponentsBuilder.fromUriString(fullUrl).build(true).toUri();
        } catch (Exception uriEx) {
            log.warn("URI build failed for '{}', falling back to URI.create: {}", fullUrl, uriEx.getMessage());
            resolvedUri = java.net.URI.create(fullUrl);
        }
        RestClient.RequestBodySpec requestSpec = client.method(httpMethod).uri(resolvedUri);

        configureAuthentication(requestSpec, apiTool, dynamicToken);

        // Los valores de customHeaders pueden estar encriptados (API secrets) o en texto plano
        // (Content-Type, Accept, etc.). Solo intentamos desencriptar; si falla, usamos el valor raw.
        if (apiTool.getProvider().getCustomHeadersJson() != null
                && !apiTool.getProvider().getCustomHeadersJson().isEmpty()) {
            try {
                Map<String, String> customHeaders = objectMapper.readValue(apiTool.getProvider().getCustomHeadersJson(),
                        new TypeReference<Map<String, String>>() {
                        });
                customHeaders.forEach((k, v) -> {
                    if (v == null || v.isBlank()) {
                        requestSpec.header(k, v);
                        return;
                    }
                    String headerValue;
                    try {
                        headerValue = encryptionService.decrypt(v);
                    } catch (Exception decryptEx) {
                        // El valor no está encriptado (ej: "application/json") — usarlo directamente.
                        log.debug("Custom header '{}' value is not encrypted, using raw value.", k);
                        headerValue = v;
                    }
                    requestSpec.header(k, headerValue);
                });
            } catch (Exception e) {
                log.warn("Failed to parse customHeadersJson for tool execution: {}",
                        apiTool.getProvider().getCustomHeadersJson(), e);
            }
        }

        if (httpMethod == HttpMethod.GET || httpMethod == HttpMethod.DELETE) {
            return requestSpec
                    .retrieve()
                    .body(Object.class);
        } else {
            byte[] binaryBody = extractBinaryBody(apiTool, finalParameters);
            if (binaryBody != null) {
                log.debug("Tool {} has a FILE parameter, sending binary body", apiTool.getCode());
                return requestSpec
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(binaryBody)
                        .retrieve()
                        .body(Object.class);
            }

            Map<String, Object> bodyParameters = prepareBodyParameters(apiTool, finalParameters, dynamicToken);
            String customTemplate = apiTool.getBodyPayloadTemplate();

            if (customTemplate != null && !customTemplate.isBlank()) {
                String interpolatedBody = interpolateTemplate(customTemplate, bodyParameters);
                log.debug("Using interpolated body payload template: {}", interpolatedBody);
                return requestSpec
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(interpolatedBody)
                        .retrieve()
                        .body(Object.class);
            } else {
                return requestSpec
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(bodyParameters)
                        .retrieve()
                        .body(Object.class);
            }
        }
    }

    private byte[] extractBinaryBody(ApiTool apiTool, Map<String, Object> parameters) {
        if (apiTool.getParameters() == null || apiTool.getBodyPayloadTemplate() != null) {
            return null;
        }

        List<ToolParameter> fileParams = apiTool.getParameters().stream()
                .filter(p -> p.getType() == ParameterType.FILE)
                .toList();

        if (fileParams.size() == 1) {
            Object value = parameters.get(fileParams.get(0).getName());
            if (value instanceof String strValue) {
                try {
                    if (strValue.contains(",") && strValue.startsWith("data:")) {
                        return java.util.Base64.getDecoder().decode(strValue.substring(strValue.indexOf(",") + 1));
                    }
                    return java.util.Base64.getDecoder().decode(strValue);
                } catch (Exception e) {
                    return strValue.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                }
            } else if (value instanceof byte[] bytes) {
                return bytes;
            }
        }
        return null;
    }

    private String interpolateTemplate(String template, Map<String, Object> params) {
        String result = template;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            if (result.contains(placeholder)) {
                String replacement = "";
                if (entry.getValue() != null) {
                    try {
                        if (entry.getValue() instanceof String) {
                            replacement = String.valueOf(entry.getValue());
                            replacement = replacement.replace("\\", "\\\\").replace("\"", "\\\"");
                        } else {
                            replacement = objectMapper.writeValueAsString(entry.getValue());
                        }
                    } catch (Exception e) {
                        log.warn("Failed to serialize parameter {} for interpolation", entry.getKey(), e);
                        replacement = String.valueOf(entry.getValue());
                    }
                }
                result = result.replace(placeholder, replacement);
            }
        }

        if (result.contains("{{") && result.contains("}}")) {
            try {
                String parseableResult = result.replaceAll("(?<!\")\\{\\{([^}]+)\\}\\}(?!\")", "\"{{$1}}\"");

                com.fasterxml.jackson.databind.JsonNode rootNode = objectMapper.readTree(parseableResult);
                cleanUnresolvedPlaceholders(rootNode);
                result = objectMapper.writeValueAsString(rootNode);
            } catch (Exception e) {
                log.debug("Template is not valid JSON or parsing failed. Falling back to regex replacement for unresolved placeholders.", e);
                result = result.replaceAll("\"\\{\\{.*?\\}\\}\"", "null");
                result = result.replaceAll("\\{\\{.*?\\}\\}", "null");
            }
        }

        return result;
    }

    private void cleanUnresolvedPlaceholders(com.fasterxml.jackson.databind.JsonNode node) {
        if (node.isObject()) {
            com.fasterxml.jackson.databind.node.ObjectNode objNode = (com.fasterxml.jackson.databind.node.ObjectNode) node;
            java.util.List<String> keysToRemove = new java.util.ArrayList<>();
            for (Map.Entry<String, com.fasterxml.jackson.databind.JsonNode> field : objNode.properties()) {
                if (field.getValue().isTextual() && field.getValue().asText().matches("^\\{\\{.*\\}\\}$")) {
                    keysToRemove.add(field.getKey());
                } else {
                    cleanUnresolvedPlaceholders(field.getValue());
                }
            }
            for (String key : keysToRemove) {
                objNode.remove(key);
            }
        } else if (node.isArray()) {
            com.fasterxml.jackson.databind.node.ArrayNode arrayNode = (com.fasterxml.jackson.databind.node.ArrayNode) node;
            for (int i = arrayNode.size() - 1; i >= 0; i--) {
                com.fasterxml.jackson.databind.JsonNode element = arrayNode.get(i);
                if (element.isTextual() && element.asText().matches("^\\{\\{.*\\}\\}$")) {
                    arrayNode.remove(i);
                } else {
                    cleanUnresolvedPlaceholders(element);
                }
            }
        }
    }

    private HttpMethod convertHttpMethod(HttpMethodEnum methodEnum) {
        switch (methodEnum) {
            case GET:
                return HttpMethod.GET;
            case POST:
                return HttpMethod.POST;
            case PUT:
                return HttpMethod.PUT;
            case DELETE:
                return HttpMethod.DELETE;
            case PATCH:
                return HttpMethod.PATCH;
            default:
                throw new ToolExecutionException("Unsupported HTTP method: " + methodEnum);
        }
    }

    private Map<String, Object> prepareParametersWithAuth(ApiTool apiTool, Map<String, Object> originalParameters,
            String dynamicToken) {
        Map<String, Object> parameters = new java.util.HashMap<>(originalParameters);

        if (apiTool.getProvider().getAuthenticationType() == AuthenticationTypeEnum.API_KEY &&
                apiTool.getProvider().getApiKeyLocation() == ApiKeyLocationEnum.QUERY_PARAMETER &&
                apiTool.getProvider().getApiKeyName() != null) {

            String token = getEffectiveToken(apiTool.getProvider(), dynamicToken);
            if (token != null) {
                parameters.put(apiTool.getProvider().getApiKeyName(), token);
            }
        }

        if (apiTool.getProvider().getSecondaryApiKeyName() != null &&
                apiTool.getProvider().getSecondaryApiKeyLocation() == ApiKeyLocationEnum.QUERY_PARAMETER) {

            String secondaryToken = getSecondaryEffectiveToken(apiTool.getProvider());
            if (secondaryToken != null) {
                parameters.put(apiTool.getProvider().getSecondaryApiKeyName(), secondaryToken);
            }
        }

        parameters.entrySet().forEach(entry -> {
            if (entry.getValue() instanceof Map<?, ?> map) {
                if (map.containsKey("value")) {
                    entry.setValue(map.get("value"));
                }
            }
        });

        return parameters;
    }

    private String buildUriWithQueryParams(ApiTool apiTool, Map<String, Object> parameters) {
        String basePath = apiTool.getEndpointPath();

        Map<String, Object> remainingParams = new java.util.HashMap<>(parameters);
        java.util.regex.Matcher matcher = java.util.regex.Pattern
                .compile("\\{([^}]+)}")
                .matcher(basePath);
        StringBuffer resolvedPath = new StringBuffer();
        while (matcher.find()) {
            String paramName = matcher.group(1);
            Object value = remainingParams.remove(paramName);
            matcher.appendReplacement(resolvedPath, value != null ? value.toString() : matcher.group(0));
        }
        matcher.appendTail(resolvedPath);
        basePath = resolvedPath.toString();

        boolean shouldAppendParams = !remainingParams.isEmpty() &&
                (apiTool.getHttpMethod() == HttpMethodEnum.GET ||
                        apiTool.getHttpMethod() == HttpMethodEnum.DELETE ||
                        hasQueryParametersForNonGet(apiTool, remainingParams));

        if (shouldAppendParams) {
            StringBuilder uriBuilder = new StringBuilder(basePath);
            if (basePath.contains("?")) {
                if (!basePath.endsWith("?") && !basePath.endsWith("&")) {
                    uriBuilder.append("&");
                }
            } else {
                uriBuilder.append("?");
            }

            remainingParams.entrySet().forEach(entry -> {
                String encodedValue = "";
                try {
                    encodedValue = java.net.URLEncoder.encode(String.valueOf(entry.getValue()), java.nio.charset.StandardCharsets.UTF_8);
                } catch (Exception e) {
                    encodedValue = String.valueOf(entry.getValue());
                }
                uriBuilder.append(entry.getKey())
                        .append("=")
                        .append(encodedValue)
                        .append("&");
            });

            if (uriBuilder.charAt(uriBuilder.length() - 1) == '&') {
                uriBuilder.setLength(uriBuilder.length() - 1);
            }

            return uriBuilder.toString();
        }

        return basePath;
    }

    private boolean hasQueryParametersForNonGet(ApiTool apiTool, Map<String, Object> parameters) {
        return apiTool.getProvider().getAuthenticationType() == AuthenticationTypeEnum.API_KEY &&
                apiTool.getProvider().getApiKeyLocation() == ApiKeyLocationEnum.QUERY_PARAMETER &&
                apiTool.getProvider().getApiKeyName() != null &&
                parameters.containsKey(apiTool.getProvider().getApiKeyName());
    }

    private void configureAuthentication(RestClient.RequestBodySpec requestSpec, ApiTool apiTool, String dynamicToken) {
        if (apiTool.getProvider().getAuthenticationType() == null) {
            return;
        }

        String token = getEffectiveToken(apiTool.getProvider(), dynamicToken);
        if (token == null)
            return;

        switch (apiTool.getProvider().getAuthenticationType()) {
            case API_KEY:
                if (apiTool.getProvider().getApiKeyLocation() == ApiKeyLocationEnum.HEADER) {
                    String headerName = apiTool.getProvider().getApiKeyName() != null
                            ? apiTool.getProvider().getApiKeyName()
                            : "X-API-Key";
                    requestSpec.header(headerName, token);
                }
                break;

            case BEARER_TOKEN:
                requestSpec.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                break;

            case BASIC_AUTH:
                requestSpec.header("Authorization", "Basic " + token);
                break;

            default:
                log.warn("Unsupported authentication type: {}", apiTool.getProvider().getAuthenticationType());
                break;
        }

        if (apiTool.getProvider().getSecondaryApiKeyName() != null &&
                apiTool.getProvider().getSecondaryApiKeyLocation() == ApiKeyLocationEnum.HEADER) {
            String secondaryToken = getSecondaryEffectiveToken(apiTool.getProvider());
            if (secondaryToken != null) {
                requestSpec.header(apiTool.getProvider().getSecondaryApiKeyName(), secondaryToken);
            }
        }
    }

    private Map<String, Object> prepareBodyParameters(ApiTool apiTool, Map<String, Object> parameters,
            String dynamicToken) {
        Map<String, Object> bodyParams = new java.util.HashMap<>(parameters);

        java.util.regex.Matcher pathMatcher = java.util.regex.Pattern
                .compile("\\{([^}]+)}")
                .matcher(apiTool.getEndpointPath());
        while (pathMatcher.find()) {
            bodyParams.remove(pathMatcher.group(1));
        }

        if (apiTool.getProvider().getAuthenticationType() == AuthenticationTypeEnum.API_KEY &&
                apiTool.getProvider().getApiKeyLocation() == ApiKeyLocationEnum.QUERY_PARAMETER &&
                apiTool.getProvider().getApiKeyName() != null) {
            bodyParams.remove(apiTool.getProvider().getApiKeyName());
        }

        if (apiTool.getProvider().getAuthenticationType() == AuthenticationTypeEnum.API_KEY &&
                apiTool.getProvider().getApiKeyLocation() == ApiKeyLocationEnum.IN_BODY &&
                apiTool.getProvider().getApiKeyName() != null) {

            String token = getEffectiveToken(apiTool.getProvider(), dynamicToken);
            if (token != null) {
                bodyParams.put(apiTool.getProvider().getApiKeyName(), token);
            }
        }

        if (apiTool.getProvider().getSecondaryApiKeyName() != null &&
                apiTool.getProvider().getSecondaryApiKeyLocation() == ApiKeyLocationEnum.IN_BODY) {

            String secondaryToken = getSecondaryEffectiveToken(apiTool.getProvider());
            if (secondaryToken != null) {
                bodyParams.put(apiTool.getProvider().getSecondaryApiKeyName(), secondaryToken);
            }
        }

        resolveArrayParams(apiTool, bodyParams);

        return bodyParams;
    }

    private void resolveArrayParams(ApiTool apiTool, Map<String, Object> bodyParams) {
        if (apiTool.getParameters() == null)
            return;
        for (ToolParameter param : apiTool.getParameters()) {
            if (param.getType() != io.invok.core.model.ParameterType.ARRAY)
                continue;
            Object value = bodyParams.get(param.getName());
            if (value == null)
                continue;
            if (value instanceof List)
                continue;
            if (value instanceof String) {
                String strVal = ((String) value).trim();
                if (strVal.startsWith("[")) {
                    try {
                        List<Object> parsed = objectMapper.readValue(strVal,
                                new TypeReference<List<Object>>() {
                                });
                        bodyParams.put(param.getName(), parsed);
                        log.debug("Deserialized ARRAY param '{}' from String to List", param.getName());
                    } catch (Exception e) {
                        log.warn("Could not deserialize ARRAY param '{}' value '{}': {}",
                                param.getName(), strVal, e.getMessage());
                    }
                }
            }
        }
    }

    private boolean isResultInvalid(Object result, ApiProvider provider) {
        if (result == null || provider.getDynamicAuthInvalidationKeywords() == null
                || provider.getDynamicAuthInvalidationKeywords().isBlank())
            return false;
        String responseStr = result.toString().toLowerCase();
        String[] keywords = provider.getDynamicAuthInvalidationKeywords().toLowerCase().split(",");
        for (String keyword : keywords) {
            if (!keyword.trim().isEmpty() && responseStr.contains(keyword.trim()))
                return true;
        }
        return false;
    }

    private boolean isExceptionInvalid(Exception e, ApiProvider provider) {
        if (e.getMessage() == null || provider.getDynamicAuthInvalidationKeywords() == null
                || provider.getDynamicAuthInvalidationKeywords().isBlank())
            return false;
        String errorStr = e.getMessage().toLowerCase();
        if (e instanceof HttpClientErrorException) {
            errorStr += " " + ((HttpClientErrorException) e).getResponseBodyAsString().toLowerCase();
        }
        String[] keywords = provider.getDynamicAuthInvalidationKeywords().toLowerCase().split(",");
        for (String keyword : keywords) {
            if (!keyword.trim().isEmpty() && errorStr.contains(keyword.trim()))
                return true;
        }
        return false;
    }

    private String getEffectiveToken(ApiProvider provider, String dynamicToken) {
        if (dynamicToken != null)
            return dynamicToken;
        if (provider.getApiKeyValue() != null)
            return encryptionService.decrypt(provider.getApiKeyValue());
        return null;
    }

    private String getSecondaryEffectiveToken(ApiProvider provider) {
        if (provider.getSecondaryApiKeyValue() != null)
            return encryptionService.decrypt(provider.getSecondaryApiKeyValue());
        return null;
    }

    private ToolExecuteResponse handleInvokGuide() {
        Map<String, Object> guide = Map.of(
                "purpose", "Invok is a universal bridge between MCP clients (LLMs) and external REST APIs. Define API providers and tools once, and any MCP-compatible agent can discover and call them dynamically without custom integration code.",
                "how_to_create_a_provider", Map.of(
                        "endpoint", "POST /api/providers",
                        "required_fields", List.of("name", "baseUrl", "authenticationType"),
                        "json_template", Map.of(
                                "name", "My API",
                                "code", "my_api (optional, auto-generated UUID if omitted)",
                                "baseUrl", "https://api.example.com",
                                "authenticationType", "API_KEY | BEARER_TOKEN | BASIC_AUTH | OAUTH2_AUTHORIZATION_CODE | NONE",
                                "apiKeyLocation", "HEADER | QUERY_PARAMETER | IN_BODY",
                                "apiKeyName", "X-API-Key (the exact header/param name the API expects)",
                                "apiKeyValue", "your-secret-key (will be encrypted)"
                        )
                ),
                "how_to_create_a_tool", Map.of(
                        "endpoint", "POST /api/tools",
                        "required_fields", List.of("name", "providerId", "endpointPath", "httpMethod"),
                        "json_template", Map.of(
                                "name", "Get Users",
                                "code", "get_users (optional)",
                                "description", "Fetches users from the API",
                                "providerId", 1,
                                "endpointPath", "/users",
                                "httpMethod", "GET",
                                "parameters", List.of(
                                        Map.of("name", "page", "type", "NUMBER", "required", false, "description", "Page number")
                                )
                        )
                ),
                "authentication_types", Map.of(
                        "API_KEY", "Sends the key as header or query param. Set apiKeyLocation and apiKeyName.",
                        "BEARER_TOKEN", "Sends Authorization: Bearer <token>. The apiKeyValue is used as the token.",
                        "BASIC_AUTH", "Sends Authorization: Basic <base64>. Store 'username:password' as apiKeyValue.",
                        "OAUTH2_AUTHORIZATION_CODE", "Full OAuth2 flow. Requires client_id, client_secret, auth/token URLs.",
                        "NONE", "No authentication (public APIs)."
                ),
                "dynamic_auth_explanation", List.of(
                        "For APIs with expiring tokens: set isDynamicAuth = true.",
                        "dynamicAuthUrl: the login endpoint (e.g., POST /auth/login).",
                        "dynamicAuthPayload: JSON with credentials (encrypted).",
                        "dynamicAuthTokenExtractionPath: JSON path to the token (e.g., 'access_token').",
                        "dynamicAuthInvalidationKeywords: comma-separated words that indicate an expired token."
                ),
                "body_payload_template_explanation", List.of(
                        "For POST/PUT/PATCH, use {{parameter_name}} placeholders for complex JSON structures.",
                        "Example: {\"query\": \"{{search_term}}\", \"limit\": {{max_results}}}.",
                        "Unresolved optional placeholders are automatically removed."
                ),
                "parameters_schema", Map.of(
                        "STRING", "Text value",
                        "NUMBER", "Integer or decimal",
                        "BOOLEAN", "true or false",
                        "ARRAY", "JSON array (LLMs send as string, auto-parsed)",
                        "FILE", "Binary upload (base64)"
                ),
                "checklist", List.of(
                        "baseUrl does NOT include the endpoint path.",
                        "endpointPath starts with '/' and matches the API docs.",
                        "authenticationType matches what the API expects.",
                        "apiKeyName matches exactly what the API docs specify.",
                        "Parameters match the API's expected query/path/body parameters.",
                        "For Dynamic Auth: token extraction path is correct.",
                        "Tool is enabled and healthy before connecting an agent."
                ),
                "export_format", "Use GET /api/export to download all providers+tools as JSON. Use POST /api/import to restore them."
        );

        try {
            String result = objectMapper.writeValueAsString(guide);
            return new ToolExecuteResponse(true, result, 0L, "system_tool", null);
        } catch (Exception e) {
            return new ToolExecuteResponse(false, null, 0L, "system_tool", e.getMessage());
        }
    }
}
