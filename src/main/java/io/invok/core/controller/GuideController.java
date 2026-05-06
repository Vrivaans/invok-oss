package io.invok.core.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/guide")
public class GuideController {

    @GetMapping
    public Map<String, Object> getGuide() {
        return Map.of(
                "purpose", "Invok is a universal bridge between MCP clients (LLMs) and external REST APIs. Define API providers and tools once, and any MCP-compatible agent can discover and call them dynamically without custom integration code.",
                "public_docs_endpoint", "GET /api/guide (this endpoint) — also available as a tool for LLM agents",
                "agent_tool_available", Map.of(
                        "name", "invok_guide",
                        "how_to_use", "Call this tool when you need to understand how to define new API providers and tools in Invok. It returns the rules, JSON format, and validation checklist."
                ),
                "core_rules", List.of(
                        "Define ONE provider per external API (e.g., GitHub, Stripe, WeatherAPI).",
                        "Each provider can have MULTIPLE tools (endpoints).",
                        "Use the correct authentication type: API_KEY, BEARER_TOKEN, BASIC_AUTH, OAUTH2_AUTHORIZATION_CODE, or NONE.",
                        "API keys, tokens, and secrets are encrypted with Jasypt before storage.",
                        "The 'code' field is the unique identifier — use snake_case (e.g., 'github_api', 'get_user').",
                        "Endpoint paths can include dynamic segments: /users/{userId}/posts.",
                        "Parameters support types: STRING, NUMBER, BOOLEAN, ARRAY, FILE.",
                        "For POST/PUT/PATCH, you can provide a bodyPayloadTemplate with {{parameter_name}} placeholders for complex JSON structures.",
                        "Dynamic Auth allows automatic token refresh when tokens expire (login endpoint → extract token → retry).",
                        "Secondary API keys allow apps that need multiple credentials (e.g., App ID + App Token)."
                ),
                "advanced_cases", Map.of(
                        "dynamic_auth", List.of(
                                "Configure 'isDynamicAuth = true' on the provider.",
                                "Set 'dynamicAuthUrl' to the login/token endpoint.",
                                "Define 'dynamicAuthPayload' as a JSON map with credentials (keys/values will be encrypted).",
                                "Set 'dynamicAuthTokenExtractionPath' to the JSON path where the token is returned (e.g., 'access_token').",
                                "Set 'dynamicAuthInvalidationKeywords' for detecting expired tokens (default: 'invalid_token,token_expired,unauthorized')."
                        ),
                        "secondary_auth", List.of(
                                "Set 'secondaryApiKeyName' and 'secondaryApiKeyValue' on the provider.",
                                "Choose 'secondaryApiKeyLocation': HEADER, QUERY_PARAMETER, or IN_BODY.",
                                "Useful for APIs that require both an App ID header and an API key in the body."
                        ),
                        "body_payload_template", List.of(
                                "Use {{parameter_name}} placeholders in the template.",
                                "Example: {\"query\": \"{{search_term}}\", \"limit\": {{max_results}}}.",
                                "Unresolved optional placeholders are automatically cleaned up.",
                                "If no template is provided, all parameters are sent as a flat JSON object."
                        )
                ),
                "recommended_authoring_flow", List.of(
                        "1. Read the API documentation of the service you want to integrate.",
                        "2. Identify the base URL and authentication method.",
                        "3. Create the provider first (POST /api/providers) with the base URL and auth config.",
                        "4. For each endpoint you want to expose, create a tool (POST /api/tools) linked to the provider.",
                        "5. Define parameters: name (must match API docs), type, whether required, and a description.",
                        "6. Test the tool using POST /api/tools/{id}/validate.",
                        "7. Export your configuration as backup with GET /api/export."
                ),
                "checklist", List.of(
                        "✅ Provider baseUrl does NOT include the endpoint path (only the root).",
                        "✅ Tool endpointPath starts with '/' and matches the API docs exactly.",
                        "✅ Authentication type matches what the API expects.",
                        "✅ API key name matches exactly what the API documentation specifies (e.g., 'X-API-Key', 'Authorization').",
                        "✅ Parameters match the API's expected query/path/body parameters.",
                        "✅ For Dynamic Auth, the token extraction path is correct (test manually first).",
                        "✅ The tool is enabled and healthy before connecting an agent."
                )
        );
    }
}
