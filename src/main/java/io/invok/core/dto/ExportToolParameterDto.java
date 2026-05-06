package io.invok.core.dto;

public record ExportToolParameterDto(
        String name,
        String type,
        String description,
        Boolean required,
        String defaultValue) {
}
