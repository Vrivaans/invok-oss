package io.invok.core.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import io.invok.core.dto.ToolDefinition;
import io.invok.core.dto.ToolDiscoveryResponse;
import io.invok.core.model.ApiTool;
import io.invok.core.repository.ApiToolRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ToolDiscoveryService {

    private final ToolCacheManager toolCacheManager;
    private final ApiToolRepository apiToolRepository;

    public ToolDiscoveryResponse discoverTools() {
        log.info("Discovering available tools");

        try {
            List<ApiTool> tools = toolCacheManager.getAllCachedTools();

            if (tools.isEmpty()) {
                log.info("No tools available in cache, fetching from database");
                tools = apiToolRepository.findAllEnabled();
            }

            List<ToolDefinition> toolDefinitions = tools.stream()
                    .map(ToolDefinition::from)
                    .collect(Collectors.toList());

            toolDefinitions.add(getInvokGuideTool());

            log.info("Discovered {} total tools", toolDefinitions.size());
            return new ToolDiscoveryResponse(
                    toolDefinitions,
                    toolDefinitions.size(),
                    Instant.now());
        } catch (Exception error) {
            log.error("Error during tool discovery", error);
            return new ToolDiscoveryResponse(
                    List.of(),
                    0,
                    Instant.now());
                }
        }

        private ToolDefinition getInvokGuideTool() {
                return new ToolDefinition(
                                "invok_guide",
                                "Guia oficial para crear providers y tools compatibles con Invok. Incluye reglas, formato JSON, autenticacion, dynamic auth, templates y checklist de validacion. Usa esta herramienta cuando necesites agregar nuevas APIs o endpoints al sistema Invok.",
                                "system_tool",
                                java.util.Map.of(
                                                "type", "object",
                                                "properties", java.util.Map.of(),
                                                "required", java.util.List.of()));
        }
}
