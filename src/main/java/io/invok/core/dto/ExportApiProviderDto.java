package io.invok.core.dto;

import io.invok.core.model.ApiKeyLocationEnum;
import io.invok.core.model.AuthenticationTypeEnum;
import java.util.List;
import java.util.Map;

public record ExportApiProviderDto(
                String name,
                String code,
                String baseUrl,
                AuthenticationTypeEnum authenticationType,
                ApiKeyLocationEnum apiKeyLocation,
                String apiKeyName,
                String apiKeyValue,
                String secondaryApiKeyName,
                String secondaryApiKeyValue,
                ApiKeyLocationEnum secondaryApiKeyLocation,
                Boolean isDynamicAuth,
                String dynamicAuthUrl,
                io.invok.core.model.DynamicAuthMethodEnum dynamicAuthMethod,
                io.invok.core.model.DynamicAuthPayloadTypeEnum dynamicAuthPayloadType,
                io.invok.core.model.DynamicAuthPayloadLocationEnum dynamicAuthPayloadLocation,
                String dynamicAuthPayload,
                String dynamicAuthTokenExtractionPath,
                String dynamicAuthInvalidationKeywords,
                Map<String, String> customHeaders,
                List<ExportApiToolDto> tools) {
}
