package dev.engine.searchengine;

import jakarta.validation.constraints.NotEmpty;

public record Link(
        @NotEmpty
        String url,
        @NotEmpty
        String title,
        @NotEmpty
        String description,
        @NotEmpty
        String content
) {
}
