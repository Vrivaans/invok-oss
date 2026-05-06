package io.invok.core.dto;

import io.invok.core.model.ApiKeyLocationEnum;
import io.invok.core.model.AuthenticationTypeEnum;
import io.invok.core.model.DynamicAuthMethodEnum;
import io.invok.core.model.DynamicAuthPayloadLocationEnum;
import io.invok.core.model.DynamicAuthPayloadTypeEnum;
import java.util.Map;

public record CreateApiProviderRequest(
                String name,
                String code,
                String baseUrl,
                AuthenticationTypeEnum authenticationType,
                ApiKeyLocationEnum apiKeyLocation,
                String apiKeyName,
                String apiKeyValue,
                Map<String, String> customHeaders,
                Boolean isDynamicAuth,
                String dynamicAuthUrl,
                DynamicAuthMethodEnum dynamicAuthMethod,
                String dynamicAuthPayload,
                DynamicAuthPayloadTypeEnum dynamicAuthPayloadType,
                DynamicAuthPayloadLocationEnum dynamicAuthPayloadLocation,
                String dynamicAuthTokenExtractionPath,
                String dynamicAuthInvalidationKeywords,
                Boolean isOauth2,
                String oauth2ClientId,
                String oauth2ClientSecret,
                String oauth2AuthorizationUrl,
                String oauth2TokenUrl,
                String oauth2Scopes,
                String oauth2RedirectUri,
                String secondaryApiKeyName,
                String secondaryApiKeyValue,
                ApiKeyLocationEnum secondaryApiKeyLocation) {
}
