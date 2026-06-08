package io.invok.core.service;

import io.invok.core.dto.ExportApiProviderDto;
import io.invok.core.dto.ExportApiToolDto;
import io.invok.core.dto.ExportToolParameterDto;
import io.invok.core.model.AuthenticationTypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class N8nWorkflowExportServiceTest {

    @Mock
    private ExportService exportService;

    @InjectMocks
    private N8nWorkflowExportService n8nWorkflowExportService;

    @SuppressWarnings("unchecked")
    @Test
    void buildWorkflow_substitutesPathParametersInUrlAndExcludesThemFromQueryParams() {
        ExportToolParameterDto boardIdParam = new ExportToolParameterDto(
                "board_id", "STRING", "ID of the board", true, null);
        ExportToolParameterDto filterParam = new ExportToolParameterDto(
                "filter", "STRING", "Filter value", false, null);

        ExportApiToolDto tool = new ExportApiToolDto(
                "Get Lists",
                "trello-get-lists",
                "Gets board lists",
                "/1/boards/{board_id}/lists",
                null,
                io.invok.core.model.HttpMethodEnum.GET,
                List.of(boardIdParam, filterParam));

        ExportApiProviderDto provider = new ExportApiProviderDto(
                1L,
                "Trello",
                "trello",
                "https://api.trello.com",
                AuthenticationTypeEnum.NONE,
                null, null, null, null, null, null,
                false, null, null, null, null, null, null, null,
                null, List.of(tool));

        when(exportService.exportProviders(List.of(1L))).thenReturn(List.of(provider));

        Map<String, Object> workflow = n8nWorkflowExportService.buildWorkflow(List.of(1L), "Test Workflow");

        assertNotNull(workflow);
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) workflow.get("nodes");
        assertNotNull(nodes);
        
        // Index 0 is the sticky note, Index 1 is the tool node
        assertEquals(2, nodes.size());
        
        Map<String, Object> toolNode = nodes.get(1);
        assertEquals("Trello — Get Lists", toolNode.get("name"));
        
        Map<String, Object> parameters = (Map<String, Object>) toolNode.get("parameters");
        assertNotNull(parameters);
        
        // Assert URL has path parameter replaced with n8n expression
        assertEquals("https://api.trello.com/1/boards/{{ $json.board_id }}/lists", parameters.get("url"));
        
        // Assert queryParameters has 'filter' but NOT 'board_id'
        Map<String, Object> queryParameters = (Map<String, Object>) parameters.get("queryParameters");
        assertNotNull(queryParameters);
        List<Map<String, Object>> paramsList = (List<Map<String, Object>>) queryParameters.get("parameters");
        assertNotNull(paramsList);
        
        boolean hasFilter = false;
        boolean hasBoardId = false;
        for (Map<String, Object> p : paramsList) {
            if ("filter".equals(p.get("name"))) {
                hasFilter = true;
            }
            if ("board_id".equals(p.get("name"))) {
                hasBoardId = true;
            }
        }
        
        assertTrue(hasFilter);
        assertFalse(hasBoardId);
    }
}
