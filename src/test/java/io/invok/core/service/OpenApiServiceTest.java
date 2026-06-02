package io.invok.core.service;

import io.invok.core.model.ApiTool;
import io.invok.core.model.ParameterType;
import io.invok.core.model.ToolParameter;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiServiceTest {

    private final OpenApiService openApiService = new OpenApiService();

    @Test
    void generateOpenApiSpec_compilesCorrectly() {
        // Arrange
        ToolParameter p1 = ToolParameter.builder()
                .name("limit")
                .type(ParameterType.NUMBER)
                .description("Limit of items")
                .required(false)
                .defaultValue("10")
                .build();

        ApiTool tool = ApiTool.builder()
                .code("test-tool")
                .name("Test Tool")
                .description("My test tool")
                .parameters(Set.of(p1))
                .build();

        // Act
        Map<String, Object> spec = openApiService.generateOpenApiSpec("http://localhost:8080", List.of(tool));

        // Assert
        assertNotNull(spec);
        assertEquals("3.0.1", spec.get("openapi"));
        
        Map<String, Object> info = (Map<String, Object>) spec.get("info");
        assertEquals("Invok Dynamic APIs", info.get("title"));

        Map<String, Object> paths = (Map<String, Object>) spec.get("paths");
        assertTrue(paths.containsKey("/api/v1/execute/test-tool"));

        Map<String, Object> postOp = (Map<String, Object>) ((Map<String, Object>) paths.get("/api/v1/execute/test-tool")).get("post");
        assertEquals("Test Tool", postOp.get("summary"));
        assertEquals("My test tool", postOp.get("description"));
    }
}
