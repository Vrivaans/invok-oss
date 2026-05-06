package io.invok.core.service;

import io.invok.core.model.ApiTool;
import io.invok.core.repository.ApiToolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@ConditionalOnProperty(name = "invok.health-check.enabled", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
@Slf4j
public class HealthCheckScheduler {

    private final ApiToolRepository apiToolRepository;
    private final ToolValidationService toolValidationService;
    private final ToolCacheManager toolCacheManager;

    @Scheduled(cron = "${invok.health-check.cron:0 */5 * * * *}")
    public void runHealthChecks() {
        log.info("Running scheduled health checks...");
        List<ApiTool> tools = apiToolRepository.findAllEnabled();
        int healthyCount = 0;
        int unhealthyCount = 0;

        for (ApiTool tool : tools) {
            try {
                boolean healthy = toolValidationService.validateApiToolHealth(tool);
                tool.setHealthy(healthy);
                tool.setLastHealthCheck(Instant.now());
                apiToolRepository.save(tool);
                if (healthy) {
                    healthyCount++;
                } else {
                    unhealthyCount++;
                }
            } catch (Exception e) {
                log.warn("Health check failed for tool {}: {}", tool.getCode(), e.getMessage());
                tool.setHealthy(false);
                tool.setLastHealthCheck(Instant.now());
                apiToolRepository.save(tool);
                unhealthyCount++;
            }
        }

        toolCacheManager.refreshCache();
        log.info("Health checks completed: {} healthy, {} unhealthy", healthyCount, unhealthyCount);
    }
}
