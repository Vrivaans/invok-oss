package io.invok.core.dto;

import io.invok.core.model.HttpMethodEnum;

import java.util.List;

public record ImportApiToolRequest(
        String name,
        String code,
        String description,
        String endpointPath,
        String bodyPayloadTemplate,
        HttpMethodEnum httpMethod,
        List<ImportToolParameterRequest> parameters) {
}
