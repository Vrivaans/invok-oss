package io.invok.core.controller;

import io.invok.core.dto.ApiToolResponse;
import io.invok.core.dto.CreateApiToolRequest;
import io.invok.core.dto.ToolExecuteRequest;
import io.invok.core.dto.ToolExecuteResponse;
import io.invok.core.dto.UpdateApiToolRequest;
import io.invok.core.service.ApiToolService;
import io.invok.core.service.ToolExecutionService;
import io.invok.core.service.ToolDiscoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tools")
@RequiredArgsConstructor
public class ToolController {

    private final ApiToolService apiToolService;
    private final ToolExecutionService toolExecutionService;
    private final ToolDiscoveryService toolDiscoveryService;
    private final io.invok.core.service.ToolCacheManager toolCacheManager;

    @PostMapping("/cache/refresh")
    public Map<String, Object> refreshToolCache() {
        int totalTools = toolCacheManager.refreshCache();
        return Map.of("message", "Cache refreshed successfully", "count", totalTools);
    }

    @GetMapping
    public List<ApiToolResponse> getAllTools() {
        return apiToolService.getAllApiTools();
    }

    @GetMapping("/{id}")
    public ApiToolResponse getTool(@PathVariable Long id) {
        return apiToolService.getApiTool(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiToolResponse createTool(@RequestBody CreateApiToolRequest request) {
        return apiToolService.createApiTool(request);
    }

    @PutMapping("/{id}")
    public ApiToolResponse updateTool(
            @PathVariable Long id,
            @RequestBody UpdateApiToolRequest request) {
        return apiToolService.updateApiTool(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTool(@PathVariable Long id) {
        apiToolService.deleteApiTool(id);
    }

    @PostMapping("/{id}/validate")
    public ApiToolResponse validateTool(@PathVariable Long id) {
        return apiToolService.validateApiToolHealth(id);
    }

    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public List<ApiToolResponse> createToolsBatch(@RequestBody List<CreateApiToolRequest> requests) {
        return apiToolService.createApiToolsBatch(requests);
    }

    @PostMapping("/call")
    public ToolExecuteResponse executeTool(@RequestBody ToolExecuteRequest request) {
        return toolExecutionService.executeApiTool(request);
    }

    @GetMapping("/discover")
    public Object discoverTools() {
        return toolDiscoveryService.discoverTools();
    }
}
