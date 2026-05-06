package io.invok.core.dto;

import io.invok.core.model.ApiKeyLocationEnum;
import io.invok.core.model.ApiProvider;
import io.invok.core.model.AuthenticationTypeEnum;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.invok.core.model.DynamicAuthMethodEnum;
import io.invok.core.model.DynamicAuthPayloadLocationEnum;
import io.invok.core.model.DynamicAuthPayloadTypeEnum;
import java.util.Map;
import java.util.HashMap;

public record ApiProviderResponse(
        Long id,
        String name,
        String code,
        String baseUrl,
        AuthenticationTypeEnum authenticationType,
        ApiKeyLocationEnum apiKeyLocation,
        String apiKeyName,
        Map<String, String> customHeaders,
        boolean isDynamicAuth,
        String dynamicAuthUrl,
        DynamicAuthMethodEnum dynamicAuthMethod,
        String dynamicAuthPayload,
        DynamicAuthPayloadTypeEnum dynamicAuthPayloadType,
        DynamicAuthPayloadLocationEnum dynamicAuthPayloadLocation,
        String dynamicAuthTokenExtractionPath,
        String dynamicAuthInvalidationKeywords,
        boolean isOauth2,
        String oauth2ClientId,
        String oauth2AuthorizationUrl,
        String oauth2TokenUrl,
        String oauth2Scopes,
        String oauth2RedirectUri,
        String secondaryApiKeyName,
        ApiKeyLocationEnum secondaryApiKeyLocation) {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ApiProviderResponse from(ApiProvider provider) {
        Map<String, String> headers = new HashMap<>();
        if (provider.getCustomHeadersJson() != null && !provider.getCustomHeadersJson().isEmpty()) {
            try {
                headers = objectMapper.readValue(provider.getCustomHeadersJson(),
                        new TypeReference<Map<String, String>>() {
                        });
                headers.replaceAll((k, v) -> (v != null && !v.isBlank()) ? "generic_text" : v);
            } catch (Exception e) {
                // Return empty map on parse error
            }
        }

        String obscuredDynamicAuthPayload = provider.getDynamicAuthPayload();
        if (obscuredDynamicAuthPayload != null && !obscuredDynamicAuthPayload.isBlank()) {
            try {
                Map<String, String> payloadMap = objectMapper.readValue(obscuredDynamicAuthPayload,
                        new TypeReference<Map<String, String>>() {
                        });
                payloadMap.replaceAll((k, v) -> (v != null && !v.isBlank()) ? "generic_text" : v);
                obscuredDynamicAuthPayload = objectMapper.writeValueAsString(payloadMap);
            } catch (Exception e) {
                // Ignore parse errors here, let the raw string be returned if not valid JSON
            }
        }

        return new ApiProviderResponse(
                provider.getId(),
                provider.getName(),
                provider.getCode(),
                provider.getBaseUrl(),
                provider.getAuthenticationType(),
                provider.getApiKeyLocation(),
                provider.getApiKeyName(),
                headers,
                provider.isDynamicAuth(),
                provider.getDynamicAuthUrl(),
                provider.getDynamicAuthMethod(),
                obscuredDynamicAuthPayload,
                provider.getDynamicAuthPayloadType(),
                provider.getDynamicAuthPayloadLocation(),
                provider.getDynamicAuthTokenExtractionPath(),
                provider.getDynamicAuthInvalidationKeywords(),
                provider.isOauth2(),
                provider.getOauth2ClientId(),
                provider.getOauth2AuthorizationUrl(),
                provider.getOauth2TokenUrl(),
                provider.getOauth2Scopes(),
                provider.getOauth2RedirectUri(),
                provider.getSecondaryApiKeyName(),
                provider.getSecondaryApiKeyLocation());
    }
}
