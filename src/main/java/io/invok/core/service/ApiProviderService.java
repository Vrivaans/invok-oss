package io.invok.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.invok.core.dto.ApiProviderResponse;
import io.invok.core.dto.CreateApiProviderRequest;
import io.invok.core.dto.UpdateApiProviderRequest;
import io.invok.core.exception.ResourceNotFoundException;
import io.invok.core.model.ApiProvider;
import io.invok.core.repository.ApiProviderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiProviderService {

    private final ApiProviderRepository providerRepository;
    private final EncryptionService encryptionService;
    private final ObjectMapper objectMapper;
    private final ToolCacheManager toolCacheManager;

    @Transactional(readOnly = true)
    public List<ApiProviderResponse> getAllProviders() {
        return providerRepository.findAll().stream()
                .map(ApiProviderResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ApiProviderResponse getProviderById(Long id) {
        return providerRepository.findById(id)
                .map(ApiProviderResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with id: " + id));
    }

    @Transactional
    public ApiProviderResponse createProvider(CreateApiProviderRequest request) {
        String code = (request.code() != null && !request.code().isBlank()) ? request.code()
                : java.util.UUID.randomUUID().toString();
        String customHeadersJson = null;
        if (request.customHeaders() != null && !request.customHeaders().isEmpty()) {
            try {
                customHeadersJson = objectMapper.writeValueAsString(request.customHeaders());
            } catch (Exception e) {
                log.error("Failed to serialize customHeaders", e);
            }
        }

        ApiProvider provider = ApiProvider.builder()
                .name(request.name())
                .code(code)
                .baseUrl(request.baseUrl())
                .authenticationType(request.authenticationType())
                .apiKeyLocation(request.apiKeyLocation())
                .apiKeyName(request.apiKeyName())
                .apiKeyValue(request.apiKeyValue() != null ? encryptionService.encrypt(request.apiKeyValue()) : null)
                .isDynamicAuth(request.isDynamicAuth() != null ? request.isDynamicAuth() : false)
                .dynamicAuthUrl(request.dynamicAuthUrl())
                .dynamicAuthMethod(request.dynamicAuthMethod())
                .dynamicAuthPayload(encryptMapJson(request.dynamicAuthPayload()))
                .dynamicAuthPayloadType(request.dynamicAuthPayloadType())
                .dynamicAuthPayloadLocation(request.dynamicAuthPayloadLocation())
                .dynamicAuthTokenExtractionPath(request.dynamicAuthTokenExtractionPath())
                .dynamicAuthInvalidationKeywords(request.dynamicAuthInvalidationKeywords())
                .isOauth2(request.isOauth2() != null ? request.isOauth2() : false)
                .oauth2ClientId(request.oauth2ClientId())
                .oauth2ClientSecret(
                        request.oauth2ClientSecret() != null ? encryptionService.encrypt(request.oauth2ClientSecret())
                                : null)
                .oauth2AuthorizationUrl(request.oauth2AuthorizationUrl())
                .oauth2TokenUrl(request.oauth2TokenUrl())
                .oauth2Scopes(request.oauth2Scopes())
                .oauth2RedirectUri(request.oauth2RedirectUri())
                .oauth2RedirectUri(request.oauth2RedirectUri())
                .customHeadersJson(encryptMapJson(customHeadersJson))
                .secondaryApiKeyName(request.secondaryApiKeyName())
                .secondaryApiKeyValue(request.secondaryApiKeyValue() != null ? encryptionService.encrypt(request.secondaryApiKeyValue()) : null)
                .secondaryApiKeyLocation(request.secondaryApiKeyLocation())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        ApiProviderResponse response = ApiProviderResponse.from(providerRepository.save(provider));
        toolCacheManager.refreshCache();
        return response;
    }

    @Transactional
    public ApiProviderResponse updateProvider(Long id, UpdateApiProviderRequest request) {
        ApiProvider existingProvider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with id: " + id));

        if (request.name() != null)
            existingProvider.setName(request.name());
        if (request.code() != null)
            existingProvider.setCode(request.code());
        if (request.baseUrl() != null)
            existingProvider.setBaseUrl(request.baseUrl());
        if (request.authenticationType() != null)
            existingProvider.setAuthenticationType(request.authenticationType());
        if (request.apiKeyLocation() != null)
            existingProvider.setApiKeyLocation(request.apiKeyLocation());
        if (request.apiKeyName() != null)
            existingProvider.setApiKeyName(request.apiKeyName());

        if (request.apiKeyValue() != null && !request.apiKeyValue().isEmpty()) {
            existingProvider.setApiKeyValue(encryptionService.encrypt(request.apiKeyValue()));
        }

        if (request.isDynamicAuth() != null)
            existingProvider.setDynamicAuth(request.isDynamicAuth());
        if (request.dynamicAuthUrl() != null)
            existingProvider.setDynamicAuthUrl(request.dynamicAuthUrl());
        if (request.dynamicAuthMethod() != null)
            existingProvider.setDynamicAuthMethod(request.dynamicAuthMethod());
        if (request.dynamicAuthPayload() != null)
            existingProvider.setDynamicAuthPayload(encryptMapJson(request.dynamicAuthPayload()));
        if (request.dynamicAuthPayloadType() != null)
            existingProvider.setDynamicAuthPayloadType(request.dynamicAuthPayloadType());
        if (request.dynamicAuthPayloadLocation() != null)
            existingProvider.setDynamicAuthPayloadLocation(request.dynamicAuthPayloadLocation());
        if (request.dynamicAuthTokenExtractionPath() != null)
            existingProvider.setDynamicAuthTokenExtractionPath(request.dynamicAuthTokenExtractionPath());
        if (request.dynamicAuthInvalidationKeywords() != null)
            existingProvider.setDynamicAuthInvalidationKeywords(request.dynamicAuthInvalidationKeywords());

        if (request.isOauth2() != null)
            existingProvider.setOauth2(request.isOauth2());
        if (request.oauth2ClientId() != null)
            existingProvider.setOauth2ClientId(request.oauth2ClientId());
        if (request.oauth2ClientSecret() != null && !request.oauth2ClientSecret().isEmpty())
            existingProvider.setOauth2ClientSecret(encryptionService.encrypt(request.oauth2ClientSecret()));
        if (request.oauth2AuthorizationUrl() != null)
            existingProvider.setOauth2AuthorizationUrl(request.oauth2AuthorizationUrl());
        if (request.oauth2TokenUrl() != null)
            existingProvider.setOauth2TokenUrl(request.oauth2TokenUrl());
        if (request.oauth2Scopes() != null)
            existingProvider.setOauth2Scopes(request.oauth2Scopes());
        if (request.oauth2RedirectUri() != null)
            existingProvider.setOauth2RedirectUri(request.oauth2RedirectUri());

        if (request.customHeaders() != null) {
            try {
                if (request.customHeaders().isEmpty()) {
                    existingProvider.setCustomHeadersJson(null);
                } else {
                    String rawJson = objectMapper.writeValueAsString(request.customHeaders());
                    existingProvider.setCustomHeadersJson(encryptMapJson(rawJson));
                }
            } catch (Exception e) {
                log.error("Failed to serialize customHeaders for provider update {}", id, e);
            }
        }

        if (request.secondaryApiKeyName() != null)
            existingProvider.setSecondaryApiKeyName(request.secondaryApiKeyName());
        if (request.secondaryApiKeyValue() != null && !request.secondaryApiKeyValue().isEmpty())
            existingProvider.setSecondaryApiKeyValue(encryptionService.encrypt(request.secondaryApiKeyValue()));
        if (request.secondaryApiKeyLocation() != null)
            existingProvider.setSecondaryApiKeyLocation(request.secondaryApiKeyLocation());

        existingProvider.setUpdatedAt(Instant.now());
        ApiProviderResponse response = ApiProviderResponse.from(providerRepository.save(existingProvider));
        toolCacheManager.refreshCache();
        return response;
    }

    @Transactional
    public void deleteProvider(Long id) {
        if (!providerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Provider not found with id: " + id);
        }
        providerRepository.deleteById(id);
        toolCacheManager.refreshCache();
    }

    private String encryptMapJson(String jsonString) {
        if (jsonString == null || jsonString.isBlank()) {
            return jsonString;
        }
        try {
            java.util.Map<String, String> map = objectMapper.readValue(jsonString,
                    new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<String, String>>() {
                    });
            java.util.Map<String, String> encryptedMap = new java.util.HashMap<>();
            for (java.util.Map.Entry<String, String> entry : map.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isBlank()) {
                    encryptedMap.put(entry.getKey(), encryptionService.encrypt(entry.getValue()));
                } else {
                    encryptedMap.put(entry.getKey(), entry.getValue());
                }
            }
            return objectMapper.writeValueAsString(encryptedMap);
        } catch (Exception e) {
            log.warn("Failed to parse and encrypt json map. Returning original.", e);
            return jsonString;
        }
    }
}
