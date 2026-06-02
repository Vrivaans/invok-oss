package io.invok.core.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.invok.core.dto.ToolExecuteRequest;
import io.invok.core.dto.ToolExecuteResponse;
import io.invok.core.model.ApiTool;
import io.invok.core.service.OpenApiService;
import io.invok.core.service.ToolCacheManager;
import io.invok.core.service.ToolExecutionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class RecipeController {

    private final OpenApiService openApiService;
    private final ToolCacheManager toolCacheManager;
    private final ToolExecutionService toolExecutionService;

    @GetMapping(value = "/api/v1/recipes/openapi.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getOpenApiSpec(HttpServletRequest request) {
        log.info("Received request for dynamic OpenAPI specification");
        
        // Dynamically compute the server base URL from the incoming request context
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();

        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);
        if (("http".equals(scheme) && serverPort != 80) || ("https".equals(scheme) && serverPort != 443)) {
            url.append(":").append(serverPort);
        }
        url.append(contextPath);
        String serverUrl = url.toString();

        List<ApiTool> activeTools = toolCacheManager.getAllCachedTools();
        Map<String, Object> openApiSpec = openApiService.generateOpenApiSpec(serverUrl, activeTools);
        
        return ResponseEntity.ok(openApiSpec);
    }

    @PostMapping(value = "/api/v1/execute/{toolCode}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> executeRecipeTool(
            @PathVariable String toolCode,
            @RequestBody(required = false) Map<String, Object> parameters) {
        
        log.info("Received execution request for recipe tool: {}", toolCode);
        
        Map<String, Object> finalParams = parameters != null ? parameters : Map.of();
        ToolExecuteRequest request = new ToolExecuteRequest(toolCode, finalParams, null);
        
        ToolExecuteResponse response = toolExecutionService.executeApiTool(request, true);
        if (response.success()) {
            return ResponseEntity.ok(response.result());
        } else {
            log.error("Execution failed for tool {}: {}", toolCode, response.errorMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Execution failed", "message", response.errorMessage()));
        }
    }
}
