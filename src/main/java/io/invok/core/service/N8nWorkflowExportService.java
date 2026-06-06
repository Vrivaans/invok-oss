package io.invok.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.invok.core.dto.ExportApiProviderDto;
import io.invok.core.dto.ExportApiToolDto;
import io.invok.core.dto.ExportToolParameterDto;
import io.invok.core.model.ApiKeyLocationEnum;
import io.invok.core.model.AuthenticationTypeEnum;
import io.invok.core.model.DynamicAuthPayloadLocationEnum;
import io.invok.core.model.DynamicAuthPayloadTypeEnum;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Generates an n8n-compatible workflow JSON that calls each registered tool
 * directly against the original API provider — not through the Invok proxy.
 *
 * <p>Auth types supported:
 * <ul>
 *   <li>NONE</li>
 *   <li>API_KEY  (HEADER / QUERY_PARAMETER / IN_BODY)</li>
 *   <li>BEARER_TOKEN</li>
 *   <li>BASIC_AUTH</li>
 *   <li>Dynamic Auth — generates a dedicated pre-auth HTTP Request node and chains
 *       tool nodes to it via n8n expressions.</li>
 * </ul>
 *
 * <p>OAuth2 is intentionally deferred. Credentials are always exported masked
 * (placeholder strings), never as real secrets.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class N8nWorkflowExportService {

    // -----------------------------------------------------------------------
    // Constants
    // -----------------------------------------------------------------------

    private static final String MASKED_API_KEY    = "<YOUR_API_KEY>";
    private static final String MASKED_SECONDARY  = "<YOUR_SECONDARY_KEY>";
    private static final int    NODE_X_START      = 460;
    private static final int    NODE_Y_START      = 240;
    private static final int    NODE_X_GAP        = 300;
    private static final int    NODE_Y_GAP        = 200;
    private static final int    NODES_PER_COLUMN  = 4;

    // X position for the auth pre-nodes column (left of tool nodes)
    private static final int    AUTH_NODE_X       = 140;

    private final ExportService exportService;

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * Build a full n8n workflow document for the requested provider IDs.
     *
     * @param providerIds optional filter; {@code null} exports all exportable providers
     * @param workflowName display name for the generated workflow
     * @return raw n8n workflow map ready for JSON serialisation
     */
    public Map<String, Object> buildWorkflow(List<Long> providerIds, String workflowName) {
        List<ExportApiProviderDto> providers = exportService.exportProviders(providerIds);
        log.info("Building n8n direct-export workflow for {} provider(s)", providers.size());

        List<Map<String, Object>> nodes = new ArrayList<>();
        // connections: nodeName -> { main: [ [ {node, type, index} ] ] }
        Map<String, Object> connections = new LinkedHashMap<>();

        nodes.add(buildStickyNote(providers));

        int toolIndex = 0;
        for (ExportApiProviderDto provider : providers) {

            if (Boolean.TRUE.equals(provider.isDynamicAuth())) {
                // --- Dynamic Auth: one pre-auth node + one tool node per tool ---
                String authNodeName = buildAuthNodeName(provider);
                int authRow = toolIndex % NODES_PER_COLUMN;
                int authCol = toolIndex / NODES_PER_COLUMN;
                Map<String, Object> authNode = buildDynamicAuthNode(provider, authNodeName, authCol, authRow);
                nodes.add(authNode);

                List<Map<String, Object>> authOutputConnections = new ArrayList<>();

                for (ExportApiToolDto tool : provider.tools()) {
                    String toolNodeName = sanitizeNodeName(provider.name() + " — " + tool.name());
                    Map<String, Object> toolNode = buildDynamicAuthToolNode(
                            provider, tool, toolNodeName, authNodeName, toolIndex);
                    nodes.add(toolNode);

                    // Connect auth node → tool node
                    authOutputConnections.add(Map.of(
                            "node",  toolNodeName,
                            "type",  "main",
                            "index", 0
                    ));
                    toolIndex++;
                }

                // Wire all tool nodes as outputs of the single auth node
                connections.put(authNodeName, Map.of(
                        "main", List.of(authOutputConnections)
                ));

            } else {
                // --- Standard auth: one tool node per tool ---
                for (ExportApiToolDto tool : provider.tools()) {
                    String toolNodeName = sanitizeNodeName(provider.name() + " — " + tool.name());
                    Map<String, Object> node = buildHttpRequestNode(
                            provider, tool, toolNodeName, toolIndex);
                    nodes.add(node);
                    toolIndex++;
                }
            }
        }

        Map<String, Object> workflow = new LinkedHashMap<>();
        workflow.put("name",        workflowName != null ? workflowName : "Invok — Direct API Export");
        workflow.put("nodes",       nodes);
        workflow.put("connections", connections);
        workflow.put("active",      false);
        workflow.put("settings",    Map.of("executionOrder", "v1"));
        workflow.put("versionId",   UUID.randomUUID().toString());
        workflow.put("meta",        Map.of(
                "generatedBy", "Invok",
                "note",        "Credentials are masked. Fill in placeholder values before running."
        ));

        log.info("n8n workflow built: {} nodes generated", nodes.size());
        return workflow;
    }

    // -----------------------------------------------------------------------
    // Standard auth node builder
    // -----------------------------------------------------------------------

    private Map<String, Object> buildHttpRequestNode(
            ExportApiProviderDto provider,
            ExportApiToolDto tool,
            String nodeName,
            int index) {

        int col = index / NODES_PER_COLUMN;
        int row = index % NODES_PER_COLUMN;
        List<Integer> position = List.of(
                NODE_X_START + col * NODE_X_GAP,
                NODE_Y_START + row * NODE_Y_GAP
        );

        String fullUrl = buildToolUrl(provider, tool);
        String method  = tool.httpMethod() != null ? tool.httpMethod().name() : "GET";
        boolean isGet  = method.equals("GET") || method.equals("DELETE");

        Map<String, Object> parameters = buildNodeParameters(
                provider, tool, fullUrl, method, isGet, null);

        Map<String, Object> node = new LinkedHashMap<>();
        node.put("id",          UUID.randomUUID().toString());
        node.put("name",        nodeName);
        node.put("type",        "n8n-nodes-base.httpRequest");
        node.put("typeVersion", 4.2);
        node.put("position",    position);
        node.put("parameters",  parameters);
        return node;
    }

    // -----------------------------------------------------------------------
    // Dynamic Auth node builders
    // -----------------------------------------------------------------------

    /**
     * Builds the pre-authentication HTTP Request node that fetches a short-lived
     * token from the provider's auth endpoint. The payload values are masked.
     */
    private Map<String, Object> buildDynamicAuthNode(
            ExportApiProviderDto provider,
            String authNodeName,
            int col, int row) {

        String method = provider.dynamicAuthMethod() != null
                ? provider.dynamicAuthMethod().name()
                : "POST";

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("method", method);
        params.put("url",    provider.dynamicAuthUrl() != null ? provider.dynamicAuthUrl() : "");
        params.put("authentication", "none");

        // Build the payload entries from the obfuscated dynamic auth payload
        DynamicAuthPayloadLocationEnum loc = provider.dynamicAuthPayloadLocation() != null
                ? provider.dynamicAuthPayloadLocation()
                : DynamicAuthPayloadLocationEnum.BODY;

        DynamicAuthPayloadTypeEnum payloadType = provider.dynamicAuthPayloadType() != null
                ? provider.dynamicAuthPayloadType()
                : DynamicAuthPayloadTypeEnum.JSON;

        List<Map<String, Object>> payloadEntries = buildDynamicAuthPayloadEntries(
                provider.dynamicAuthPayload());

        switch (loc) {
            case BODY -> {
                if (!payloadEntries.isEmpty()) {
                    params.put("sendBody", true);
                    if (payloadType == DynamicAuthPayloadTypeEnum.FORM_DATA) {
                        params.put("contentType",     "form-urlencoded");
                        params.put("bodyParameters",  Map.of("parameters", payloadEntries));
                    } else {
                        params.put("contentType",     "json");
                        params.put("bodyParameters",  Map.of("parameters", payloadEntries));
                    }
                } else {
                    params.put("sendBody", false);
                }
                params.put("sendQuery",   false);
                params.put("sendHeaders", false);
            }
            case QUERY_PARAMETERS -> {
                params.put("sendBody",  false);
                params.put("sendQuery", !payloadEntries.isEmpty());
                if (!payloadEntries.isEmpty()) {
                    params.put("queryParameters", Map.of("parameters", payloadEntries));
                }
                params.put("sendHeaders", false);
            }
            case HEADERS -> {
                params.put("sendBody",    false);
                params.put("sendQuery",   false);
                params.put("sendHeaders", !payloadEntries.isEmpty());
                if (!payloadEntries.isEmpty()) {
                    params.put("headerParameters", Map.of("parameters", payloadEntries));
                }
            }
        }

        params.put("options", Map.of());

        Map<String, Object> node = new LinkedHashMap<>();
        node.put("id",          UUID.randomUUID().toString());
        node.put("name",        authNodeName);
        node.put("type",        "n8n-nodes-base.httpRequest");
        node.put("typeVersion", 4.2);
        // Auth nodes are placed in a dedicated left column
        node.put("position",    List.of(
                AUTH_NODE_X + col * (NODE_X_GAP + NODE_X_START),
                NODE_Y_START + row * NODE_Y_GAP
        ));
        node.put("parameters",  params);
        return node;
    }

    /**
     * Builds a tool HTTP Request node that injects the dynamic token obtained by
     * the pre-auth node via an n8n expression reference.
     */
    private Map<String, Object> buildDynamicAuthToolNode(
            ExportApiProviderDto provider,
            ExportApiToolDto tool,
            String toolNodeName,
            String authNodeName,
            int index) {

        int col = index / NODES_PER_COLUMN;
        int row = index % NODES_PER_COLUMN;

        String fullUrl = buildToolUrl(provider, tool);
        String method  = tool.httpMethod() != null ? tool.httpMethod().name() : "GET";
        boolean isGet  = method.equals("GET") || method.equals("DELETE");

        // Build the n8n expression that reads the token from the auth node response
        String tokenExpression = buildTokenExpression(authNodeName, provider.dynamicAuthTokenExtractionPath());

        Map<String, Object> parameters = buildNodeParameters(
                provider, tool, fullUrl, method, isGet, tokenExpression);

        Map<String, Object> node = new LinkedHashMap<>();
        node.put("id",          UUID.randomUUID().toString());
        node.put("name",        toolNodeName);
        node.put("type",        "n8n-nodes-base.httpRequest");
        node.put("typeVersion", 4.2);
        node.put("position",    List.of(
                NODE_X_START + col * NODE_X_GAP,
                NODE_Y_START + row * NODE_Y_GAP
        ));
        node.put("parameters",  parameters);
        return node;
    }

    // -----------------------------------------------------------------------
    // Parameter / header / body builders
    // -----------------------------------------------------------------------

    /**
     * Builds the full {@code parameters} block for an n8n HTTP Request node.
     *
     * @param tokenExpression n8n expression string for the dynamic token, or
     *                        {@code null} for static-auth providers
     */
    private Map<String, Object> buildNodeParameters(
            ExportApiProviderDto provider,
            ExportApiToolDto tool,
            String fullUrl,
            String method,
            boolean isGet,
            String tokenExpression) {

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("method", method);
        params.put("url",    fullUrl);

        // Auth block (dynamic auth nodes inject the token via headers/query, not here)
        if (tokenExpression != null) {
            buildDynamicTokenAuthBlock(params, provider, tokenExpression);
        } else {
            buildAuthBlock(params, provider);
        }

        // Headers
        List<Map<String, Object>> headers = buildHeadersList(provider, tokenExpression);
        if (!headers.isEmpty()) {
            params.put("sendHeaders",      true);
            params.put("headerParameters", Map.of("parameters", headers));
        } else {
            params.put("sendHeaders", false);
        }

        // Query params
        List<Map<String, Object>> queryParams = buildQueryParams(provider, tool, isGet, tokenExpression);
        if (!queryParams.isEmpty()) {
            params.put("sendQuery",       true);
            params.put("queryParameters", Map.of("parameters", queryParams));
        } else {
            params.put("sendQuery", false);
        }

        // Body
        if (!isGet && tool.parameters() != null && !tool.parameters().isEmpty()) {
            params.put("sendBody",       true);
            params.put("contentType",    "json");
            params.put("bodyParameters", Map.of("parameters", buildBodyEntries(provider, tool, tokenExpression)));
        } else if (!isGet) {
            params.put("sendBody", false);
        }

        params.put("options", Map.of());
        return params;
    }

    /**
     * Sets auth fields for standard (non-dynamic) providers.
     */
    private void buildAuthBlock(Map<String, Object> params, ExportApiProviderDto provider) {
        if (provider.authenticationType() == null
                || provider.authenticationType() == AuthenticationTypeEnum.NONE) {
            params.put("authentication", "none");
            return;
        }

        switch (provider.authenticationType()) {
            case API_KEY -> {
                // Key delivered via header/query/body — the actual header injection
                // happens in buildHeadersList / buildQueryParams / buildBodyEntries.
                params.put("authentication", "none");
            }
            case BEARER_TOKEN -> {
                // Header injected via buildHeadersList
                params.put("authentication", "none");
            }
            case BASIC_AUTH -> {
                params.put("authentication",  "genericCredentialType");
                params.put("genericAuthType", "httpBasicAuth");
                params.put("_authHint", Map.of(
                        "note",     "Create an 'HTTP Basic Auth' credential in n8n with your username and password.",
                        "username", "<YOUR_USERNAME>",
                        "password", "<YOUR_PASSWORD>"
                ));
            }
            default -> params.put("authentication", "none");
        }
    }

    /**
     * Sets auth fields for dynamic-auth tool nodes. The token is always
     * delivered as an Authorization Bearer header via the expression reference.
     */
    private void buildDynamicTokenAuthBlock(
            Map<String, Object> params,
            ExportApiProviderDto provider,
            String tokenExpression) {
        // The dynamic token is injected as a header in buildHeadersList;
        // authentication field stays "none" so the header block takes effect.
        params.put("authentication", "none");
    }

    private List<Map<String, Object>> buildHeadersList(
            ExportApiProviderDto provider,
            String tokenExpression) {

        List<Map<String, Object>> headers = new ArrayList<>();

        // Custom headers (already scrubbed by ExportService)
        if (provider.customHeaders() != null) {
            provider.customHeaders().forEach((k, v) ->
                    headers.add(Map.of("name", k, "value", v != null ? v : ""))
            );
        }

        // Secondary key via header
        if (provider.secondaryApiKeyName() != null
                && provider.secondaryApiKeyLocation() == ApiKeyLocationEnum.HEADER) {
            headers.add(Map.of("name", provider.secondaryApiKeyName(), "value", MASKED_SECONDARY));
        }

        AuthenticationTypeEnum auth = provider.authenticationType();

        if (tokenExpression != null) {
            // Dynamic auth: inject the token as a Bearer Authorization header
            headers.add(Map.of("name", "Authorization", "value", "Bearer " + tokenExpression));
        } else if (auth == AuthenticationTypeEnum.API_KEY
                && provider.apiKeyLocation() == ApiKeyLocationEnum.HEADER
                && provider.apiKeyName() != null) {
            headers.add(Map.of("name", provider.apiKeyName(), "value", MASKED_API_KEY));
        } else if (auth == AuthenticationTypeEnum.BEARER_TOKEN) {
            headers.add(Map.of("name", "Authorization", "value", "Bearer " + MASKED_API_KEY));
        }

        return headers;
    }

    private List<Map<String, Object>> buildQueryParams(
            ExportApiProviderDto provider,
            ExportApiToolDto tool,
            boolean isGet,
            String tokenExpression) {

        List<Map<String, Object>> query = new ArrayList<>();

        if (isGet && tool.parameters() != null) {
            for (ExportToolParameterDto p : tool.parameters()) {
                query.add(Map.of("name", p.name(), "value", ""));
            }
        }

        // API_KEY as query param (only for static auth)
        if (tokenExpression == null
                && provider.authenticationType() == AuthenticationTypeEnum.API_KEY
                && provider.apiKeyLocation() == ApiKeyLocationEnum.QUERY_PARAMETER
                && provider.apiKeyName() != null) {
            query.add(Map.of("name", provider.apiKeyName(), "value", MASKED_API_KEY));
        }

        // Secondary key as query param
        if (provider.secondaryApiKeyName() != null
                && provider.secondaryApiKeyLocation() == ApiKeyLocationEnum.QUERY_PARAMETER) {
            query.add(Map.of("name", provider.secondaryApiKeyName(), "value", MASKED_SECONDARY));
        }

        return query;
    }

    private List<Map<String, Object>> buildBodyEntries(
            ExportApiProviderDto provider,
            ExportApiToolDto tool,
            String tokenExpression) {

        List<Map<String, Object>> entries = new ArrayList<>();

        if (tool.parameters() != null) {
            for (ExportToolParameterDto p : tool.parameters()) {
                entries.add(Map.of("name", p.name(), "value", ""));
            }
        }

        // API_KEY in body (static auth only)
        if (tokenExpression == null
                && provider.authenticationType() == AuthenticationTypeEnum.API_KEY
                && provider.apiKeyLocation() == ApiKeyLocationEnum.IN_BODY
                && provider.apiKeyName() != null) {
            entries.add(Map.of("name", provider.apiKeyName(), "value", MASKED_API_KEY));
        }

        // Secondary key in body
        if (provider.secondaryApiKeyName() != null
                && provider.secondaryApiKeyLocation() == ApiKeyLocationEnum.IN_BODY) {
            entries.add(Map.of("name", provider.secondaryApiKeyName(), "value", MASKED_SECONDARY));
        }

        return entries;
    }

    /**
     * Parses the obfuscated {@code dynamicAuthPayload} JSON and converts every
     * value to a masked placeholder, preserving the field names so the user knows
     * which credentials to fill in.
     */
    private List<Map<String, Object>> buildDynamicAuthPayloadEntries(String payloadJson) {
        List<Map<String, Object>> entries = new ArrayList<>();
        if (payloadJson == null || payloadJson.isBlank()) return entries;

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> payload = mapper.readValue(payloadJson,
                    new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
            payload.forEach((k, v) -> {
                // Values are already obfuscated by ExportService.obfuscateDynamicAuthPayload()
                // as "<YOUR_DYNAMIC_AUTH>"; we preserve them as-is.
                entries.add(Map.of("name", k, "value", v != null ? String.valueOf(v) : ""));
            });
        } catch (Exception e) {
            log.warn("Could not parse dynamicAuthPayload for n8n export: {}", e.getMessage());
        }

        return entries;
    }

    // -----------------------------------------------------------------------
    // Expression helpers
    // -----------------------------------------------------------------------

    /**
     * Converts a dot-notation extraction path (e.g. {@code data.token}) into an
     * n8n expression that reads from the given upstream node.
     *
     * <p>Examples:
     * <ul>
     *   <li>{@code "access_token"} → {@code {{ $('Auth — Acme').item.json["access_token"] }}}</li>
     *   <li>{@code "data.token"}   → {@code {{ $('Auth — Acme').item.json["data"]["token"] }}}</li>
     *   <li>{@code null / ""}      → {@code {{ $('Auth — Acme').item.json }}} (raw response)</li>
     * </ul>
     */
    private String buildTokenExpression(String authNodeName, String extractionPath) {
        String base = "{{ $('" + authNodeName + "').item.json";
        if (extractionPath == null || extractionPath.isBlank()) {
            return base + " }}";
        }
        StringBuilder expr = new StringBuilder(base);
        for (String part : extractionPath.split("\\.")) {
            expr.append("[\"").append(part).append("\"]");
        }
        expr.append(" }}");
        return expr.toString();
    }

    // -----------------------------------------------------------------------
    // Informational / utility nodes
    // -----------------------------------------------------------------------

    private Map<String, Object> buildStickyNote(List<ExportApiProviderDto> providers) {
        long dynamicCount = providers.stream()
                .filter(p -> Boolean.TRUE.equals(p.isDynamicAuth()))
                .count();

        StringBuilder content = new StringBuilder();
        content.append("## ⚡ Invok — Direct API Export\n\n");
        content.append("This workflow calls the original API providers **directly** (not through Invok).\n\n");
        content.append("### Before running\n");
        content.append("1. Replace every `<YOUR_API_KEY>`, `<YOUR_SECONDARY_KEY>`, `<YOUR_USERNAME>` and `<YOUR_PASSWORD>` ");
        content.append("placeholder with your real credentials.\n");
        content.append("2. For **Basic Auth** nodes: create an *HTTP Basic Auth* credential in n8n ");
        content.append("(*Credentials → New → HTTP Basic Auth*) and link it to the node.\n");
        content.append("3. For **Dynamic Auth** nodes: fill in the `<YOUR_DYNAMIC_AUTH>` values in the ");
        content.append("pre-auth node. Tool nodes receive the token automatically via n8n expressions.\n\n");

        if (dynamicCount > 0) {
            content.append("### 🔗 Dynamic Auth providers (").append(dynamicCount).append(")\n");
            content.append("Each dynamic-auth provider has a **pre-auth node** that fetches the token, ");
            content.append("followed by tool nodes that reference it with `$(\"Auth — …\").item.json[\"field\"]`.\n\n");
        }

        content.append("**Never commit real API keys to version control.**");

        Map<String, Object> note = new LinkedHashMap<>();
        note.put("id",          UUID.randomUUID().toString());
        note.put("name",        "Invok Export — Read Me");
        note.put("type",        "n8n-nodes-base.stickyNote");
        note.put("typeVersion", 1);
        note.put("position",    List.of(AUTH_NODE_X - 160, NODE_Y_START - 100));
        note.put("parameters",  Map.of("content", content.toString(), "height", 400, "width", 560));
        return note;
    }

    // -----------------------------------------------------------------------
    // Shared helpers
    // -----------------------------------------------------------------------

    private String buildToolUrl(ExportApiProviderDto provider, ExportApiToolDto tool) {
        String base = provider.baseUrl() != null && provider.baseUrl().endsWith("/")
                ? provider.baseUrl().substring(0, provider.baseUrl().length() - 1)
                : (provider.baseUrl() != null ? provider.baseUrl() : "");
        String path = tool.endpointPath() != null ? tool.endpointPath() : "";
        if (!path.isEmpty() && !path.startsWith("/")) path = "/" + path;
        return base + path;
    }

    private String buildAuthNodeName(ExportApiProviderDto provider) {
        return sanitizeNodeName("Auth — " + provider.name());
    }

    private String sanitizeNodeName(String raw) {
        if (raw == null) return "Unnamed Node";
        String clean = raw.replaceAll("[\"'<>]", "").trim();
        return clean.length() > 60 ? clean.substring(0, 60) : clean;
    }
}
