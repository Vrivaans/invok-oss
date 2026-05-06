package io.invok.core.dto;

public record ApiResponse(
        int statusCode,
        String message) {
}
