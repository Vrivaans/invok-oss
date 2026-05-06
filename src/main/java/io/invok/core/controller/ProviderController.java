package io.invok.core.controller;

import lombok.RequiredArgsConstructor;
import io.invok.core.dto.ApiProviderResponse;
import io.invok.core.dto.CreateApiProviderRequest;
import io.invok.core.dto.UpdateApiProviderRequest;
import io.invok.core.service.ApiProviderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ApiProviderService apiProviderService;

    @GetMapping
    public List<ApiProviderResponse> getAllProviders() {
        return apiProviderService.getAllProviders();
    }

    @GetMapping("/{id}")
    public ApiProviderResponse getProviderById(@PathVariable Long id) {
        return apiProviderService.getProviderById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiProviderResponse createProvider(@RequestBody CreateApiProviderRequest request) {
        return apiProviderService.createProvider(request);
    }

    @PutMapping("/{id}")
    public ApiProviderResponse updateProvider(
            @PathVariable Long id,
            @RequestBody UpdateApiProviderRequest request) {
        return apiProviderService.updateProvider(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProvider(@PathVariable Long id) {
        apiProviderService.deleteProvider(id);
    }
}
