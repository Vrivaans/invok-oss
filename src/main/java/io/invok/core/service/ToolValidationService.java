package io.invok.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.invok.core.model.ApiTool;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class ToolValidationService {

    private final RestClient.Builder restClientBuilder;

    public boolean validateApiToolHealth(ApiTool apiTool) {
        try {
            String url = apiTool.getProvider().getBaseUrl();

            // Simple ping check to validate if the API is accessible
            RestClient restClient = restClientBuilder.baseUrl(url).build();

            // Hacemos un HEAD request para validar que el endpoint responde
            restClient.head()
                    .retrieve()
                    .toBodilessEntity();

            log.info("API tool health check successful for {}", apiTool.getName());
            return true;

        } catch (Exception e) {
            log.warn("API tool health check failed for {}: {}", apiTool.getName(), e.getMessage());
            return false;
        }
    }
}
