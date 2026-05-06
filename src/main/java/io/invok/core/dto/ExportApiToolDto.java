package io.invok.core.dto;

import io.invok.core.model.HttpMethodEnum;
import java.util.List;

public record ExportApiToolDto(
                String name,
                String code,
                String description,
                String endpointPath,
                String bodyPayloadTemplate,
                HttpMethodEnum httpMethod,
                List<ExportToolParameterDto> parameters) {
}
