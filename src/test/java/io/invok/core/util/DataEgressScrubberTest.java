package io.invok.core.util;

import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DataEgressScrubberTest {

    private final DataEgressScrubber scrubber = new DataEgressScrubber();

    @Test
    public void testScrubJwtToken() {
        String input = "Here is my token eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c it is a secret.";
        String expected = "Here is my token [REDACTED_SECRET] it is a secret.";
        assertEquals(expected, scrubber.scrubString(input));
    }

    @Test
    public void testScrubBearerToken() {
        String input = "Authorization: Bearer mySecretTokenThatIsLongEnough123==";
        String expected = "Authorization: Bearer [REDACTED_SECRET]";
        assertEquals(expected, scrubber.scrubString(input));
    }

    @Test
    public void testScrubBearerTokenIgnoringShortTokens() {
        // Less than 16 chars shouldn't be redacted as generic key
        String input = "Authorization: Bearer shortToken123";
        assertEquals(input, scrubber.scrubString(input));
    }

    @Test
    public void testScrubPrivateKey() {
        String input = "My key is:\n-----BEGIN RSA PRIVATE KEY-----\nMIIEpQIBAAKCAQEA3...\n-----END RSA PRIVATE KEY-----\nDon't tell anyone.";
        String expected = "My key is:\n[REDACTED_SECRET]\nDon't tell anyone.";
        assertEquals(expected, scrubber.scrubString(input));
    }

    @Test
    public void testScrubNestedMapAndList() {
        Map<String, Object> innerMap = Map.of(
                "normalText", "hello world",
                "secretToken", "Bearer abcdefghijklmnopqrstuvwxyz12345==");

        List<Object> list = List.of(
                "plain string",
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
                innerMap);

        Map<String, Object> payload = Map.of(
                "user", "ivanv",
                "data", list);

        Map<String, Object> scrubbed = scrubber.scrubParameters(payload);

        assertEquals("ivanv", scrubbed.get("user"));

        @SuppressWarnings("unchecked")
        List<Object> scrubbedList = (List<Object>) scrubbed.get("data");
        assertEquals("plain string", scrubbedList.get(0));
        assertEquals("[REDACTED_SECRET]", scrubbedList.get(1));

        @SuppressWarnings("unchecked")
        Map<String, Object> scrubbedInnerMap = (Map<String, Object>) scrubbedList.get(2);
        assertEquals("hello world", scrubbedInnerMap.get("normalText"));
        assertEquals("Bearer [REDACTED_SECRET]", scrubbedInnerMap.get("secretToken"));
    }
}
