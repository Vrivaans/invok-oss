package io.invok.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogObfuscatorTest {

    private LogObfuscator logObfuscator;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        logObfuscator = new LogObfuscator(objectMapper);
    }

    @Test
    void testObfuscateSensitiveFieldsInJsonObject() {
        String json = "{\"username\":\"admin\",\"password\":\"supersecret\",\"apiKey\":\"12345-abcde\",\"metadata\":{\"status\":\"active\"}}";

        String obfuscated = logObfuscator.obfuscate(json);

        assertTrue(obfuscated.contains("\"password\":\"******\""));
        assertTrue(obfuscated.contains("\"apiKey\":\"******\""));
        assertTrue(obfuscated.contains("\"username\":\"admin\""));
        assertTrue(obfuscated.contains("\"status\":\"active\""));
    }

    @Test
    void testObfuscateNestedSensitiveFields() {
        String json = "{\"user\":{\"id\":1,\"auth\":{\"token\":\"eyJ...\"}}, \"data\":\"test\"}";

        String obfuscated = logObfuscator.obfuscate(json);

        assertTrue(obfuscated.contains("\"auth\":\"******\"") || obfuscated.contains("\"token\":\"******\""));
    }

    @Test
    void testObfuscateArrayElements() {
        String json = "[{\"name\":\"app1\",\"api_secret\":\"foo\"},{\"name\":\"app2\",\"api_secret\":\"bar\"}]";

        String obfuscated = logObfuscator.obfuscate(json);

        assertTrue(obfuscated.contains("\"api_secret\":\"******\""));
        assertTrue(obfuscated.contains("\"name\":\"app1\""));
        assertTrue(obfuscated.contains("\"name\":\"app2\""));
    }

    @Test
    void testNonJsonStringHandling() {
        String plainText = "Just a regular string that is not JSON";
        String obfuscated = logObfuscator.obfuscate(plainText);
        assertEquals(plainText, obfuscated);
    }

    @Test
    void testNullOrEmpty() {
        assertEquals("", logObfuscator.obfuscate(""));
        assertEquals(null, logObfuscator.obfuscate(null));
    }
}
