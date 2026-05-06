package io.invok.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Utility class to obfuscate sensitive data in JSON payloads before logging or
 * storing them.
 */
@Slf4j
@Component
public class LogObfuscator {

    private final ObjectMapper objectMapper;
    private final List<Pattern> sensitiveKeysPatterns;
    private static final String OBFUSCATED_VALUE = "******";

    public LogObfuscator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        // Default patterns for sensitive keys
        this.sensitiveKeysPatterns = List.of(
                Pattern.compile("(?i).*password.*"),
                Pattern.compile("(?i).*token.*"),
                Pattern.compile("(?i).*secret.*"),
                Pattern.compile("(?i).*key.*"),
                Pattern.compile("(?i).*auth.*"),
                Pattern.compile("(?i).*credential.*"));
    }

    /**
     * Parses a JSON string, obfuscates sensitive fields, and returns the modified
     * JSON string.
     * If the string is not valid JSON, it returns the original string or a fallback
     * if it fails.
     *
     * @param json Payload to obfuscate.
     * @return Obfuscated JSON string (or original if not JSON).
     */
    public String obfuscate(String json) {
        if (json == null || json.isBlank()) {
            return json;
        }

        try {
            JsonNode rootNode = objectMapper.readTree(json);
            obfuscateNode(rootNode);
            return objectMapper.writeValueAsString(rootNode);
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse and obfuscate JSON payload: {}", e.getMessage());
            // If it's not a valid JSON, we can't safely parse and obfuscate fields.
            // Returning the original string could leak data if the caller expected it to be
            // JSON but it was malformed.
            // As a fallback, we return a generic message to prevent leakage in case it
            // contains raw secrets.
            // However, it might just be a regular string response from an API.
            // A safer approach for non-JSON is to check if it contains any of our keywords
            // broadly,
            // but for simplicity, we'll assume most payloads are JSON.
            return isSensitiveString(json) ? "****** (Obfuscated non-JSON strict string)" : json;
        }
    }

    private void obfuscateNode(JsonNode node) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            Iterator<Map.Entry<String, JsonNode>> fields = objectNode.properties().iterator();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String key = field.getKey();
                JsonNode valueNode = field.getValue();

                if (isSensitiveKey(key)) {
                    if (valueNode.isTextual() || valueNode.isNumber() || valueNode.isBoolean()) {
                        objectNode.put(key, OBFUSCATED_VALUE);
                    } else if (valueNode.isArray() || valueNode.isObject()) {
                        // We could replace the whole object/array with ****** or traverse it.
                        // Better to replace it completely if the key implies the whole object is a
                        // secret
                        objectNode.put(key, OBFUSCATED_VALUE);
                    }
                } else {
                    // Recurse into objects and arrays
                    obfuscateNode(valueNode);
                }
            }
        } else if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) node;
            for (JsonNode arrayElement : arrayNode) {
                obfuscateNode(arrayElement);
            }
        }
    }

    private boolean isSensitiveKey(String key) {
        if (key == null)
            return false;
        for (Pattern pattern : sensitiveKeysPatterns) {
            if (pattern.matcher(key).matches()) {
                return true;
            }
        }
        return false;
    }

    private boolean isSensitiveString(String text) {
        if (text == null)
            return false;
        String lower = text.toLowerCase();
        return lower.contains("password=") || lower.contains("token=") || lower.contains("secret=")
                || lower.contains("key=");
    }
}
