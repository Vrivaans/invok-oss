package io.invok.core.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.invok.core.exception.ToolExecutionException;
import io.invok.core.model.ApiProvider;
import io.invok.core.model.DynamicAuthMethodEnum;
import io.invok.core.model.DynamicAuthPayloadLocationEnum;
import io.invok.core.model.DynamicAuthPayloadTypeEnum;
import io.invok.core.model.AuthenticationTypeEnum;
import io.invok.core.repository.ApiProviderRepository;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class DynamicTokenManager {

    private final RestClient.Builder restClientBuilder;
    private final ObjectMapper objectMapper;
    private final EncryptionService encryptionService;
    private final ApiProviderRepository providerRepository;

    // Cache: Provider ID -> CachedToken
    private final Map<Long, CachedToken> tokenCache = new ConcurrentHashMap<>();
    private static final long TOKEN_TTL_SECONDS = 18000; // 5 hours

    public String getToken(ApiProvider provider) {
        if (!provider.isDynamicAuth()
                && provider.getAuthenticationType() != AuthenticationTypeEnum.OAUTH2_AUTHORIZATION_CODE) {
            return null;
        }

        CachedToken cachedToken = tokenCache.get(provider.getId());
        if (cachedToken != null && cachedToken.expiresAt().isAfter(Instant.now())) {
            log.debug("Returning cached dynamic token for provider {}", provider.getId());
            return cachedToken.token();
        }

        log.info("Fetching new dynamic or refresh token for provider {}", provider.getId());
        String newToken = fetchNewToken(provider);

        tokenCache.put(provider.getId(), new CachedToken(newToken, Instant.now().plusSeconds(TOKEN_TTL_SECONDS)));
        return newToken;
    }

    public void invalidateToken(Long providerId) {
        log.info("Invalidating dynamic token cache for provider {}", providerId);
        tokenCache.remove(providerId);
    }

    private String fetchNewToken(ApiProvider provider) {
        try {
            if (provider.getAuthenticationType() == AuthenticationTypeEnum.OAUTH2_AUTHORIZATION_CODE) {
                return fetchOAuth2RefreshToken(provider);
            }

            RestClient client = restClientBuilder.baseUrl(provider.getDynamicAuthUrl()).build();
            HttpMethod method = provider.getDynamicAuthMethod() == DynamicAuthMethodEnum.GET ? HttpMethod.GET
                    : HttpMethod.POST;

            // Parse Payload
            Map<String, Object> payloadMap = null;
            if (provider.getDynamicAuthPayload() != null && !provider.getDynamicAuthPayload().isBlank()) {
                payloadMap = objectMapper.readValue(provider.getDynamicAuthPayload(),
                        new TypeReference<Map<String, Object>>() {
                        });

                // Decrypt payload values safely
                for (Map.Entry<String, Object> entry : payloadMap.entrySet()) {
                    if (entry.getValue() instanceof String strVal && !strVal.isBlank()) {
                        try {
                            entry.setValue(encryptionService.decrypt(strVal));
                        } catch (Exception decryptEx) {
                            // Si falla la desencriptación (porque es texto plano o de otro tenant), se
                            // ignora y se usa el valor original
                            log.debug(
                                    "Safe-decryption fallback: Failed to decrypt payload value for key '{}'. Using original value.",
                                    entry.getKey());
                        }
                    }
                }
            }

            String finalUri = provider.getDynamicAuthUrl();
            DynamicAuthPayloadLocationEnum location = provider.getDynamicAuthPayloadLocation();
            if (location == null)
                location = DynamicAuthPayloadLocationEnum.BODY; // default

            if (payloadMap != null && !payloadMap.isEmpty()
                    && location == DynamicAuthPayloadLocationEnum.QUERY_PARAMETERS) {
                StringBuilder query = new StringBuilder("?");
                payloadMap.forEach((k, v) -> query.append(k).append("=").append(v).append("&"));
                finalUri += query.substring(0, query.length() - 1); // append to base URL
            }

            RestClient.RequestBodySpec requestSpec = client.method(method).uri(finalUri);

            // Handle Payload Location
            if (payloadMap != null && !payloadMap.isEmpty()) {
                switch (location) {
                    case HEADERS:
                        for (Map.Entry<String, Object> entry : payloadMap.entrySet()) {
                            requestSpec.header(entry.getKey(), String.valueOf(entry.getValue()));
                        }
                        break;
                    case QUERY_PARAMETERS:
                        // Already handled above
                        break;
                    case BODY:
                        if (method == HttpMethod.POST) {
                            DynamicAuthPayloadTypeEnum type = provider.getDynamicAuthPayloadType();
                            if (type == DynamicAuthPayloadTypeEnum.FORM_DATA) {
                                requestSpec.contentType(MediaType.APPLICATION_FORM_URLENCODED);
                                StringBuilder formData = new StringBuilder();
                                payloadMap.forEach((k, v) -> formData.append(k).append("=").append(v).append("&"));
                                String body = formData.substring(0, formData.length() - 1);
                                requestSpec.body(body);
                            } else {
                                requestSpec.contentType(MediaType.APPLICATION_JSON);
                                requestSpec.body(payloadMap);
                            }
                        }
                        break;
                }
            }

            String responseBody = requestSpec.retrieve().body(String.class);

            // Extract Token
            String extractionPath = provider.getDynamicAuthTokenExtractionPath();
            if (extractionPath == null || extractionPath.isBlank()) {
                // If empty path, assuming the raw response is the token text
                return responseBody;
            }

            JsonNode rootNode = objectMapper.readTree(responseBody);
            String[] pathParts = extractionPath.split("\\.");
            JsonNode currentNode = rootNode;

            for (String part : pathParts) {
                if (currentNode != null && currentNode.has(part)) {
                    currentNode = currentNode.get(part);
                } else {
                    currentNode = null;
                    break;
                }
            }

            if (currentNode != null && !currentNode.isNull()) {
                if (currentNode.isTextual()) {
                    return currentNode.textValue();
                } else {
                    return currentNode.toString();
                }
            }

            throw new ToolExecutionException(
                    "Could not extract token from auth response using path: " + extractionPath);

        } catch (Exception e) {
            log.error("Failed to fetch dynamic token for provider {}", provider.getId(), e);
            throw new ToolExecutionException("Failed to fetch dynamic auth token. Root cause: " + e.getMessage(), e);
        }
    }

    private String fetchOAuth2RefreshToken(ApiProvider provider) {
        if (provider.getOauth2RefreshToken() == null) {
            throw new ToolExecutionException(
                    "No refresh token available to renew OAuth2 flow for provider " + provider.getId());
        }

        try {
            RestClient client = restClientBuilder.baseUrl(provider.getOauth2TokenUrl()).build();
            String decryptedRefreshToken = encryptionService.decrypt(provider.getOauth2RefreshToken());

            StringBuilder formData = new StringBuilder();
            formData.append("client_id=").append(provider.getOauth2ClientId());
            formData.append("&client_secret=").append(encryptionService.decrypt(provider.getOauth2ClientSecret()));
            formData.append("&refresh_token=").append(decryptedRefreshToken);
            formData.append("&grant_type=refresh_token");

            String responseBody = client.post()
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formData.toString())
                    .retrieve()
                    .body(String.class);

            JsonNode rootNode = objectMapper.readTree(responseBody);
            if (rootNode.has("access_token")) {
                String newAccessToken = rootNode.get("access_token").textValue();
                provider.setApiKeyValue(encryptionService.encrypt(newAccessToken));
                // Optionally some providers return a new refresh_token
                if (rootNode.has("refresh_token")) {
                    provider.setOauth2RefreshToken(
                            encryptionService.encrypt(rootNode.get("refresh_token").textValue()));
                }
                providerRepository.save(provider);
                return newAccessToken;
            }

            throw new ToolExecutionException("Response from token endpoint did not contain access_token.");

        } catch (Exception e) {
            log.error("Failed to refresh OAuth2 token for provider {}", provider.getId(), e);
            throw new ToolExecutionException("Failed to refresh OAuth2 token: " + e.getMessage());
        }
    }

    private record CachedToken(String token, Instant expiresAt) {
    }
}
