package io.invok.core.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.invok.core.dto.ExportApiProviderDto;
import io.invok.core.service.ExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
@Slf4j
public class ExportController {

    private final ExportService exportService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ExportApiProviderDto>> exportProviders(@RequestParam(required = false) List<Long> ids) {
        log.info("Received request to export providers. IDs: {}", ids);
        List<ExportApiProviderDto> exportData = exportService.exportProviders(ids);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invok_tools_export.json");

        return ResponseEntity.ok()
                .headers(headers)
                .body(exportData);
    }
}
