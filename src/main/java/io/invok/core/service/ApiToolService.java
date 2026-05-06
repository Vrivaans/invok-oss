package io.invok.core.service;

import io.invok.core.dto.ApiToolResponse;
import io.invok.core.dto.CreateApiToolRequest;
import io.invok.core.dto.UpdateApiToolRequest;
import io.invok.core.model.ApiTool;

import java.util.List;

public interface ApiToolService {

    ApiToolResponse createApiTool(CreateApiToolRequest request);

    List<ApiToolResponse> createApiToolsBatch(List<CreateApiToolRequest> requests);

    ApiToolResponse updateApiTool(Long id, UpdateApiToolRequest request);

    ApiToolResponse getApiTool(Long id);

    List<ApiToolResponse> getAllApiTools();

    void deleteApiTool(Long id);

    ApiToolResponse validateApiToolHealth(Long id);

    ApiTool getApiToolByCode(String code);
}
