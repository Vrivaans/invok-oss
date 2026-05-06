package io.invok.core.dto;

import io.invok.core.model.ApiKeyLocationEnum;
import io.invok.core.model.AuthenticationTypeEnum;
import io.invok.core.model.HttpMethodEnum;

import java.util.List;

public record UpdateApiToolRequest(
        String name,
        String code,
        String description,
        Long providerId,
        String endpointPath,
        HttpMethodEnum httpMethod,
        String bodyPayloadTemplate,
        boolean enabled,
                List<ToolParameterRequest> parameters) {
}
