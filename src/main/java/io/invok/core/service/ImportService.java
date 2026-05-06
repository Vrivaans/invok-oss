package io.invok.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.invok.core.dto.ImportApiProviderRequest;
import io.invok.core.dto.ImportApiToolRequest;
import io.invok.core.dto.ImportToolParameterRequest;
import io.invok.core.model.ApiProvider;
import io.invok.core.model.ApiTool;
import io.invok.core.model.ToolParameter;
import io.invok.core.repository.ApiProviderRepository;
import io.invok.core.repository.ApiToolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportService {

    private final ApiProviderRepository providerRepository;
    private final ApiToolRepository toolRepository;
    private final EncryptionService encryptionService;
    private final ToolCacheManager toolCacheManager;
    private final ObjectMapper objectMapper;

    private static final String MASKED_API_KEY = "<YOUR_API_KEY>";

    @Transactional
    public void importProviders(List<ImportApiProviderRequest> importRequests) {
        log.info("Starting import of {} providers.", importRequests.size());

        for (ImportApiProviderRequest providerReq : importRequests) {
            ApiProvider provider = processProvider(providerReq);

            if (providerReq.tools() != null) {
                for (ImportApiToolRequest toolReq : providerReq.tools()) {
                    processTool(toolReq, provider);
                }
            }
        }

        toolCacheManager.refreshCache();
        log.info("Import completed successfully.");
    }

    private ApiProvider processProvider(ImportApiProviderRequest req) {
        String code = (req.code() != null && !req.code().isBlank()) ? req.code() : UUID.randomUUID().toString();
        ApiProvider provider = providerRepository.findByCode(code).orElse(null);

        if (provider == null) {
            log.info("Creating imported provider: {}", req.name());
            provider = new ApiProvider();
            provider.setCode(code);
            provider.setCreatedAt(Instant.now());
        } else {
            log.info("Updating existing provider: {}", req.name());
            // Existing ones usually don't need code reassignment, but safe to do
            provider.setCode(code);
        }

        provider.setName(req.name());
        provider.setBaseUrl(req.baseUrl());
        provider.setAuthenticationType(req.authenticationType());
        provider.setApiKeyLocation(req.apiKeyLocation());
        provider.setApiKeyName(req.apiKeyName());
        provider.setDynamicAuth(req.isDynamicAuth() != null ? req.isDynamicAuth() : false);
        provider.setDynamicAuthUrl(req.dynamicAuthUrl());
        provider.setDynamicAuthMethod(req.dynamicAuthMethod() != null ? req.dynamicAuthMethod()
                : io.invok.core.model.DynamicAuthMethodEnum.POST);
        provider.setDynamicAuthPayloadType(req.dynamicAuthPayloadType() != null ? req.dynamicAuthPayloadType()
                : io.invok.core.model.DynamicAuthPayloadTypeEnum.JSON);
        provider.setDynamicAuthPayloadLocation(
                req.dynamicAuthPayloadLocation() != null ? req.dynamicAuthPayloadLocation()
                        : io.invok.core.model.DynamicAuthPayloadLocationEnum.BODY);
        provider.setDynamicAuthPayload(req.dynamicAuthPayload());
        provider.setDynamicAuthTokenExtractionPath(req.dynamicAuthTokenExtractionPath());
        provider.setDynamicAuthInvalidationKeywords(
                req.dynamicAuthInvalidationKeywords() != null && !req.dynamicAuthInvalidationKeywords().isBlank()
                        ? req.dynamicAuthInvalidationKeywords()
                        : "invalid_token,token_expired,unauthorized,expired_token");

        if (req.apiKeyValue() != null && !req.apiKeyValue().isBlank() && !req.apiKeyValue().equals(MASKED_API_KEY)) {
            provider.setApiKeyValue(encryptionService.encrypt(req.apiKeyValue()));
        }

        provider.setSecondaryApiKeyName(req.secondaryApiKeyName());
        provider.setSecondaryApiKeyLocation(req.secondaryApiKeyLocation());
        if (req.secondaryApiKeyValue() != null && !req.secondaryApiKeyValue().isBlank()
                && !req.secondaryApiKeyValue().equals(MASKED_API_KEY)) {
            provider.setSecondaryApiKeyValue(encryptionService.encrypt(req.secondaryApiKeyValue()));
        }

        if (req.customHeaders() != null) {
            try {
                if (req.customHeaders().isEmpty()) {
                    provider.setCustomHeadersJson(null);
                } else {
                    provider.setCustomHeadersJson(objectMapper.writeValueAsString(req.customHeaders()));
                }
            } catch (Exception e) {
                log.error("Failed to serialize customHeaders during import for provider: {}", req.name(), e);
            }
        }

        provider.setUpdatedAt(Instant.now());
        return providerRepository.save(provider);
    }

    private void processTool(ImportApiToolRequest req, ApiProvider provider) {
        String code = (req.code() != null && !req.code().isBlank()) ? req.code() : UUID.randomUUID().toString();
        ApiTool tool = toolRepository.findByCodeAndProvider(code, provider).orElse(null);

        if (tool == null) {
            log.info("Creating imported tool: {}", req.name());
            tool = new ApiTool();
            tool.setCode(code);
            tool.setCreatedAt(Instant.now());
            tool.setParameters(new LinkedHashSet<>());
        } else {
            log.info("Updating existing tool: {}", req.name());
            tool.getParameters().clear(); // To replace with fresh parameter list
        }

        tool.setProvider(provider);
        tool.setName(req.name());
        tool.setDescription(req.description());
        tool.setEndpointPath(req.endpointPath());
        tool.setBodyPayloadTemplate(req.bodyPayloadTemplate());
        tool.setHttpMethod(req.httpMethod());
        tool.setEnabled(true);
        tool.setHealthy(true);
        tool.setUpdatedAt(Instant.now());

        final ApiTool finalTool = tool;

        if (req.parameters() != null) {
            Set<ToolParameter> parameters = req.parameters().stream()
                    .map(pReq -> processParameter(pReq, finalTool))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            tool.getParameters().addAll(parameters);
        }

        toolRepository.save(tool);
    }

    private ToolParameter processParameter(ImportToolParameterRequest req, ApiTool tool) {
        return ToolParameter.builder()
                .apiTool(tool)
                .name(req.name())
                .code(UUID.randomUUID().toString()) // Re-roll parameter UUIDs on import to avoid db conflicts
                .type(req.type())
                .description(req.description())
                .required(req.required())
                .defaultValue(req.defaultValue())
                .enabled(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}
