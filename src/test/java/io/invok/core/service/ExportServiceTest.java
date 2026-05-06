package io.invok.core.service;

import io.invok.core.dto.ExportApiProviderDto;
import io.invok.core.model.ApiProvider;
import io.invok.core.model.ApiTool;
import io.invok.core.repository.ApiProviderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExportServiceTest {

    @Mock
    private ApiProviderRepository apiProviderRepository;

    @InjectMocks
    private ExportService exportService;

    @Test
    void exportProviders_masksApiKeyAndIncludesAllTools() {
        ApiTool tool1 = ApiTool.builder()
                .name("Tool 1")
                .build();
        ApiTool tool2 = ApiTool.builder()
                .name("Tool 2")
                .build();

        ApiProvider providerWithKey = ApiProvider.builder()
                .name("Provider 1")
                .apiKeyValue("SUPER_SECRET_KEY")
                .tools(Arrays.asList(tool1, tool2))
                .build();

        ApiProvider providerWithoutKey = ApiProvider.builder()
                .name("Provider 2")
                .apiKeyValue(null)
                .tools(List.of())
                .build();

        when(apiProviderRepository.findAll())
                .thenReturn(Arrays.asList(providerWithKey, providerWithoutKey));

        List<ExportApiProviderDto> result = exportService.exportProviders(null);

        assertEquals(2, result.size());

        ExportApiProviderDto exportedProvider1 = result.get(0);
        assertEquals("Provider 1", exportedProvider1.name());
        assertEquals("<YOUR_API_KEY>", exportedProvider1.apiKeyValue(), "API Key MUST be masked");
        assertEquals(2, exportedProvider1.tools().size(),
                "All tools should be included");
    }
}
