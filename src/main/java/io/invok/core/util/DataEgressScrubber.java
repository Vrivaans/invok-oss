package io.invok.core.util;

import org.springframework.stereotype.Component;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

@Component
public class DataEgressScrubber {

    private static final String REDACTED_MARKER = "[REDACTED_SECRET]";

    // Regex 1: JWT Tokens
    // Format: base64Url.base64Url.base64Url (very common for eyJ...)
    private static final Pattern JWT_PATTERN = Pattern.compile("eyJ[a-zA-Z0-9_-]+\\.[a-zA-Z0-9_-]+\\.[a-zA-Z0-9_-]+");

    // Regex 2: Generic API Keys / Bearer Tokens (Conservative)
    // Matches keys preceded by keywords like bearer, token, key, secret,
    // authorization
    // Requires at least 16 chars of base64-like characters.
    private static final Pattern GENERIC_KEY_PATTERN = Pattern.compile(
            "(?i)(bearer|token|key|secret|authorization|api[\\s_-]?key)[\\s=:\"]+([A-Za-z0-9\\-\\._~\\+/]{16,}=*)");

    // Regex 3: Private Keys (RSA, OPENSSH, PGP, etc)
    private static final Pattern PRIVATE_KEY_PATTERN = Pattern.compile(
            "-----BEGIN (RSA|OPENSSH|DSA|EC|PGP) PRIVATE KEY-----.*?-----END \\1 PRIVATE KEY-----", Pattern.DOTALL);

    /**
     * Recursively scrubs a map of parameters to prevent data exfiltration.
     */
    public Map<String, Object> scrubParameters(Map<String, Object> parameters) {
        if (parameters == null) {
            return null;
        }

        Map<String, Object> scrubbed = new HashMap<>();
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            scrubbed.put(entry.getKey(), scrubValue(entry.getValue()));
        }

        return scrubbed;
    }

    /**
     * Recursively scrubs individual values (Maps, Lists, Strings).
     */
    @SuppressWarnings("unchecked")
    private Object scrubValue(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof String) {
            return scrubString((String) value);
        } else if (value instanceof Map) {
            Map<String, Object> nestedMap = (Map<String, Object>) value;
            return scrubParameters(nestedMap);
        } else if (value instanceof List) {
            List<Object> nestedList = (List<Object>) value;
            List<Object> scrubbedList = new ArrayList<>();
            for (Object item : nestedList) {
                scrubbedList.add(scrubValue(item));
            }
            return scrubbedList;
        }

        // Return primitives, numbers, booleans as-is
        return value;
    }

    /**
     * Applies the regex redactions to a string.
     */
    public String scrubString(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String scrubbed = input;

        // 1. Scrub JWTs
        scrubbed = JWT_PATTERN.matcher(scrubbed).replaceAll(REDACTED_MARKER);

        // 2. Scrub Private Keys
        scrubbed = PRIVATE_KEY_PATTERN.matcher(scrubbed).replaceAll(REDACTED_MARKER);

        // 3. Scrub Generic Keys (using groups to keep the keyword prefix intact but
        // replacing the actual secret)
        Matcher matcher = GENERIC_KEY_PATTERN.matcher(scrubbed);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            // matcher.group(1) is the prefix (e.g. "Bearer ")
            // matcher.group(0) is the entire match. We replace the secret part with
            // REDACTED
            String replacement = matcher.group(0).replace(matcher.group(2), REDACTED_MARKER);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        scrubbed = sb.toString();

        return scrubbed;
    }
}
