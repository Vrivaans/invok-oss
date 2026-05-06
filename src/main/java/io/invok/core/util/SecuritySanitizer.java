package io.invok.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@Slf4j
@RequiredArgsConstructor
public class SecuritySanitizer {

    private final ObjectMapper objectMapper;

    // A list of common prompt injection phrasing patterns
    private static final String[] MALICIOUS_PATTERNS = {
            "(?i)ignore previous",
            "(?i)system:",
            "(?i)forget your (instructions|prompt)",
            "(?i)you are now",
            "(?i)new instructions",
            "(?i)disregard previous",
            "(?i)ignore all previous"
    };

    private static final String START_TAG = "<UntrustedExternalContent>";
    private static final String END_TAG = "</UntrustedExternalContent>";
    private static final String REDACTED_MARKER = "[REDACTED_POTENTIAL_PROMPT_INJECTION]";

    /**
     * Sanitizes and wraps the object returned from external API calls.
     * Ensure the LLM explicitly handles this as external and untrusted data.
     * 
     * @param rawResponse The raw returned object from an MCP tool
     * @return Sanitized string wrapped in XML-like tags.
     */
    public String sanitizeToolResponse(Object rawResponse) {
        if (rawResponse == null) {
            return START_TAG + "null" + END_TAG;
        }

        String serializedResponse;
        try {
            if (rawResponse instanceof String) {
                serializedResponse = (String) rawResponse;
            } else {
                serializedResponse = objectMapper.writeValueAsString(rawResponse);
            }
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize response for sanitization. Falling back to toString().", e);
            serializedResponse = rawResponse.toString();
        }

        // 1. Defang any existing tags that match our wrapper to prevent escaping
        serializedResponse = serializedResponse.replaceAll("(?i)" + Pattern.quote(START_TAG),
                "&lt;UntrustedExternalContent&gt;");
        serializedResponse = serializedResponse.replaceAll("(?i)" + Pattern.quote(END_TAG),
                "&lt;/UntrustedExternalContent&gt;");

        // 2. Redact common prompt injection patterns
        for (String pattern : MALICIOUS_PATTERNS) {
            serializedResponse = serializedResponse.replaceAll(pattern, REDACTED_MARKER);
        }

        // 3. Wrap the response
        return START_TAG + "\n" + serializedResponse + "\n" + END_TAG;
    }
}
