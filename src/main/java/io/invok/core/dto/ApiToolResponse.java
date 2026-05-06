package io.invok.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.invok.core.model.ApiKeyLocationEnum;
import io.invok.core.model.ApiTool;
import io.invok.core.model.AuthenticationTypeEnum;
import io.invok.core.model.HttpMethodEnum;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the data of an API tool that is safe to expose to clients.
 * It purposefully excludes sensitive information like the stored API key value.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiToolResponse(
        Long id,
        String code,
        String name,
        String description,
        Long providerId,
        String providerName,
        String endpointPath,
        HttpMethodEnum httpMethod,
        boolean enabled,
        boolean healthy,
        Instant lastHealthCheck,
        String bodyPayloadTemplate,
        List<ToolParameterResponse> parameters) {
    public static ApiToolResponse from(ApiTool apiTool) {
        return new ApiToolResponse(
                apiTool.getId(),
                apiTool.getCode(),
                apiTool.getName(),
                apiTool.getDescription(),
                apiTool.getProvider().getId(),
                apiTool.getProvider().getName(),
                apiTool.getEndpointPath(),
                apiTool.getHttpMethod(),
                apiTool.isEnabled(),
                apiTool.isHealthy(),
                apiTool.getLastHealthCheck(),
                apiTool.getBodyPayloadTemplate(),
                apiTool.getParameters().stream()
                        .map(ToolParameterResponse::from)
                        .collect(Collectors.toList()));
    }
}
