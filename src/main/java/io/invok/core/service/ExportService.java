package io.invok.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.invok.core.dto.ExportApiProviderDto;
import io.invok.core.dto.ExportApiToolDto;
import io.invok.core.dto.ExportToolParameterDto;
import io.invok.core.model.ApiProvider;
import io.invok.core.model.ApiTool;
import io.invok.core.repository.ApiProviderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExportService {

    private final ApiProviderRepository apiProviderRepository;
    private static final String MASKED_API_KEY = "<YOUR_API_KEY>";
    private static final String MASKED_SECONDARY_KEY = "<YOUR_SECONDARY_KEY>";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional(readOnly = true)
    public List<ExportApiProviderDto> exportProviders(List<Long> providerIds) {
        log.info("Exporting providers. Target IDs: {}", providerIds != null ? providerIds : "ALL");

        List<ApiProvider> providers;
        if (providerIds == null || providerIds.isEmpty()) {
            providers = apiProviderRepository.findAll();
        } else {
            providers = apiProviderRepository.findAllByIdIn(providerIds);
        }

        return providers.stream()
                .map(this::mapToExportProviderDto)
                .collect(Collectors.toList());
    }

    private ExportApiProviderDto mapToExportProviderDto(ApiProvider provider) {
        List<ExportApiToolDto> exportTools = provider.getTools().stream()
                .map(this::mapToExportToolDto)
                .collect(Collectors.toList());

        Map<String, String> customHeaders = null;
        if (provider.getCustomHeadersJson() != null && !provider.getCustomHeadersJson().isEmpty()) {
            try {
                customHeaders = objectMapper.readValue(provider.getCustomHeadersJson(),
                        new TypeReference<Map<String, String>>() {
                        });
            } catch (IOException e) {
                log.error("Failed to parse customHeadersJson for export on provider {}", provider.getId(), e);
            }
        }

        return new ExportApiProviderDto(
                provider.getName(),
                provider.getCode(),
                provider.getBaseUrl(),
                provider.getAuthenticationType(),
                provider.getApiKeyLocation(),
                provider.getApiKeyName(),
                provider.getApiKeyValue() != null ? MASKED_API_KEY : null,
                provider.getSecondaryApiKeyName(),
                provider.getSecondaryApiKeyValue() != null ? MASKED_SECONDARY_KEY : null,
                provider.getSecondaryApiKeyLocation(),
                provider.isDynamicAuth(),
                provider.getDynamicAuthUrl(),
                provider.getDynamicAuthMethod(),
                provider.getDynamicAuthPayloadType(),
                provider.getDynamicAuthPayloadLocation(),
                provider.getDynamicAuthPayload(),
                provider.getDynamicAuthTokenExtractionPath(),
                provider.getDynamicAuthInvalidationKeywords(),
                customHeaders,
                exportTools);
    }

    private ExportApiToolDto mapToExportToolDto(ApiTool tool) {
        List<ExportToolParameterDto> exportParams = tool.getParameters().stream()
                .map(param -> new ExportToolParameterDto(
                        param.getName(),
                        param.getType() != null ? param.getType().name() : null,
                        param.getDescription(),
                        param.getRequired(),
                        param.getDefaultValue()))
                .collect(Collectors.toList());

        return new ExportApiToolDto(
                tool.getName(),
                tool.getCode(),
                tool.getDescription(),
                tool.getEndpointPath(),
                tool.getBodyPayloadTemplate(),
                tool.getHttpMethod(),
                exportParams);
    }
}
