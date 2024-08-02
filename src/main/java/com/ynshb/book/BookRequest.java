package com.ynshb.book;

import jakarta.validation.constraints.NotEmpty;

public record BookRequest(
        Long id,
        @NotEmpty(message = "Title is required")
        String title,
        @NotEmpty(message = "Author is required")
        String author,
        @NotEmpty(message = "ISBN is required")
        String isbn,
        String synopsis,
        boolean shareable
) {}
