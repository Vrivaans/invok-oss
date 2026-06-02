package io.invok.core.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import io.invok.core.model.ApiTool;
import io.invok.core.repository.ApiToolRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ToolCacheManager {

    private final ApiToolRepository apiToolRepository;

    private final ConcurrentHashMap<String, ApiTool> toolCache = new ConcurrentHashMap<>();

    @EventListener(ApplicationReadyEvent.class)
    public void initCache() {
        log.info("Initializing tool cache");
        List<ApiTool> activeTools = apiToolRepository.findAllEnabled();
        activeTools.forEach(tool -> toolCache.put(tool.getCode(), tool));
        log.info("Tool cache initialized with {} tools", activeTools.size());
    }

    public List<ApiTool> getAllCachedTools() {
        return toolCache.values().stream()
                .filter(ApiTool::isEnabled)
                .toList();
    }

    public Optional<ApiTool> getCachedTool(String toolCode) {
        return Optional.ofNullable(toolCache.get(toolCode))
                .filter(ApiTool::isEnabled);
    }

    public void addOrUpdateTool(ApiTool tool) {
        if (tool.isEnabled()) {
            toolCache.put(tool.getCode(), tool);
            log.info("Tool {} added/updated in cache", tool.getCode());
        } else {
            toolCache.remove(tool.getCode());
            log.info("Tool {} removed from cache due to disabled state", tool.getCode());
        }
    }

    public void removeTool(String toolCode) {
        toolCache.remove(toolCode);
        log.info("Tool {} removed from cache", toolCode);
    }

    public int refreshCache() {
        List<ApiTool> tools = apiToolRepository.findAllEnabled();
        toolCache.clear();
        tools.forEach(tool -> toolCache.put(tool.getCode(), tool));
        log.info("Cache refreshed with {} tools", tools.size());
        return tools.size();
    }
}
