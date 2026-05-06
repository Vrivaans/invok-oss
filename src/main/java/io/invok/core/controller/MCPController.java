package io.invok.core.controller;

import io.invok.core.dto.McpResponse;
import io.invok.core.dto.McpToolCallRequest;
import io.invok.core.dto.McpToolCallResponse;
import io.invok.core.dto.McpToolsListResponse;
import io.invok.core.service.McpRpcHandlerService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/mcp")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MCPController {

        private final McpRpcHandlerService mcpRpcHandlerService;

        @GetMapping("/tools/list")
        public McpResponse<McpToolsListResponse> discoverTools() {
                return mcpRpcHandlerService.listToolsLegacy();
        }

        @PostMapping("/tools/call")
        public McpResponse<McpToolCallResponse> executeApiTool(@RequestBody McpToolCallRequest request) {
                return mcpRpcHandlerService.callToolLegacy(request);
        }
}
