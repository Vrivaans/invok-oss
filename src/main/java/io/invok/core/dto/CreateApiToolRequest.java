package io.invok.core.dto;

import io.invok.core.model.ApiKeyLocationEnum;
import io.invok.core.model.AuthenticationTypeEnum;
import io.invok.core.model.HttpMethodEnum;

import java.util.List;

/**
 * DTO for creating a new API tool.
 * The API key value is optional and can be provided at runtime during execution
 * if not set here.
 */
public record CreateApiToolRequest(
                String name,
                String code,
                Boolean enabled,
                String description,
                Long providerId,
                String endpointPath,
                HttpMethodEnum httpMethod,
                String bodyPayloadTemplate,
                List<ToolParameterRequest> parameters) {
}
