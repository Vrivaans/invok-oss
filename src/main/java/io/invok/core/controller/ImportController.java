package io.invok.core.controller;

import java.util.List;
import java.util.Map;

import io.invok.core.dto.ImportApiProviderRequest;
import io.invok.core.service.ImportService;
import io.invok.core.service.OpenApiImportService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
@Slf4j
public class ImportController {

    private final ImportService importService;
    private final OpenApiImportService openApiImportService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<Map<String, Object>> importData(@RequestBody JsonNode body) {
        if (body.isArray()) {
            List<ImportApiProviderRequest> importRequests = objectMapper.convertValue(
                    body,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ImportApiProviderRequest.class));
            log.info("Importing {} providers in Invok native format", importRequests.size());
            importService.importProviders(importRequests);
            return ResponseEntity.ok(Map.of(
                    "message", "Import successful",
                    "providers", importRequests.size()));
        } else if (body.isObject() && (body.has("openapi") || body.has("swagger") || body.has("paths"))) {
            log.info("Detected OpenAPI format, parsing...");
            Map<String, Object> result = openApiImportService.importOpenApi(body);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid input format",
                    "message", "Expected an array (Invok format) or an OpenAPI specification object"));
        }
    }
}
