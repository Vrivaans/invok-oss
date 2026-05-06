package io.invok.core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "api_providers", uniqueConstraints = {
        @UniqueConstraint(name = "uk_api_providers_code", columnNames = { "code" })
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ApiProvider extends BaseModel {

    private String name;

    @Column(nullable = false)
    private String baseUrl;

    @Enumerated(EnumType.STRING)
    private AuthenticationTypeEnum authenticationType;

    @Enumerated(EnumType.STRING)
    private ApiKeyLocationEnum apiKeyLocation;

    private String apiKeyName;

    /**
     * Optional custom headers stored as JSON string. Ex: {"User-Agent": "MyAgent"}
     */
    @Column(columnDefinition = "TEXT")
    private String customHeadersJson;

    /**
     * Stores the hashed API key or token. It is strongly recommended to encrypt
     * this value before storing it.
     */
    private String apiKeyValue;

    // --- OAuth 2.0 Feature Fields ---

    @Column(columnDefinition = "boolean default false")
    @lombok.Builder.Default
    private boolean isOauth2 = false;

    @Column(name = "oauth2_client_id")
    private String oauth2ClientId;

    @Column(name = "oauth2_client_secret")
    private String oauth2ClientSecret; // Must be encrypted

    @Column(name = "oauth2_authorization_url")
    private String oauth2AuthorizationUrl;

    @Column(name = "oauth2_token_url")
    private String oauth2TokenUrl;

    @Column(name = "oauth2_scopes")
    private String oauth2Scopes;

    @Column(name = "oauth2_redirect_uri")
    private String oauth2RedirectUri;

    @Column(name = "oauth2_refresh_token")
    private String oauth2RefreshToken; // Must be encrypted

    // --- Dynamic Authentication Fields ---

    @Column(columnDefinition = "boolean default false")
    @lombok.Builder.Default
    private boolean isDynamicAuth = false;

    private String dynamicAuthUrl;

    @Enumerated(EnumType.STRING)
    private DynamicAuthMethodEnum dynamicAuthMethod;

    @Column(columnDefinition = "TEXT")
    private String dynamicAuthPayload;

    @Enumerated(EnumType.STRING)
    private DynamicAuthPayloadTypeEnum dynamicAuthPayloadType;

    @Enumerated(EnumType.STRING)
    private DynamicAuthPayloadLocationEnum dynamicAuthPayloadLocation;

    private String dynamicAuthTokenExtractionPath;

    @Column(columnDefinition = "TEXT")
    private String dynamicAuthInvalidationKeywords;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true)
    @lombok.Builder.Default
    private List<ApiTool> tools = new ArrayList<>();

    // --- Secondary Authentication Fields (App Tokens) ---

    private String secondaryApiKeyName;

    @Column(columnDefinition = "TEXT")
    private String secondaryApiKeyValue; // Must be encrypted

    @Enumerated(EnumType.STRING)
    private ApiKeyLocationEnum secondaryApiKeyLocation;
}
