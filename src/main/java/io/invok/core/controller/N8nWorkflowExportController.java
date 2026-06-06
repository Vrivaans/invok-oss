package io.invok.core.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.invok.core.service.N8nWorkflowExportService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Exposes the n8n direct-export endpoint.
 *
 * <p>
 * Example usage:
 * 
 * <pre>
 *   GET /api/export/n8n-workflow                          → exports all exportable providers
 *   GET /api/export/n8n-workflow?ids=1,2,3               → exports specific providers
 *   GET /api/export/n8n-workflow?name=My%20Workflow       → custom workflow name
 * </pre>
 */
@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
@Slf4j
public class N8nWorkflowExportController {

    private final N8nWorkflowExportService n8nWorkflowExportService;

    @GetMapping(value = "/n8n-workflow", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> exportN8nWorkflow(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(required = false, defaultValue = "Invok — Direct API Export") String name) {

        log.info("Received n8n direct-export request. Provider IDs: {}, Workflow name: {}",
                ids != null ? ids : "ALL", name);

        Map<String, Object> workflow = n8nWorkflowExportService.buildWorkflow(ids, name);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("invok_n8n_workflow.json")
                        .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(workflow);
    }
}
