package io.invok.core.dto;

public record RegenerateTokenResponse(
        String bridgeToken,
        String warning) {
}
