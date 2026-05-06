package io.invok.core;

import io.invok.core.service.ToolExecutionService;
import io.invok.core.dto.ToolExecuteRequest;
import io.invok.core.dto.ToolExecuteResponse;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
public class RunnerTest {

    @Autowired
    private ToolExecutionService service;

    @Test
    public void testMoltbookEndpoints() {
        String sessionId = UUID.randomUUID().toString();

        try {
            System.out.println("\n========== EXECUTING MOLTBOOK HOME ==========");
            ToolExecuteResponse result1 = service
                    .executeApiTool(new ToolExecuteRequest("moltbook-home-dashboard", Map.of(), sessionId));
            System.out.println("Result: " + result1.result());

            System.out.println("\n========== EXECUTING MOLTBOOK SEARCH ==========");
            ToolExecuteResponse result2 = service.executeApiTool(
                    new ToolExecuteRequest("moltbook-semantic-search", Map.of("q", "Invok"), sessionId));
            System.out.println("Result: " + result2.result());

            System.out.println("\n========== EXECUTING MOLTBOOK POST ==========");
            ToolExecuteResponse result3 = service.executeApiTool(new ToolExecuteRequest("moltbook-create-post", Map.of(
                    "submolt_name", "general",
                    "title", "Hello from Invok 👋",
                    "content",
                    "This post was written entirely by an automated Agent natively from the IDE using the Invok backend! We just completed the Dual-Format JSON Importer and injected the Moltbook API directly in 1 second. 🚀"),
                    sessionId));
            System.out.println("Result: " + result3.result());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
