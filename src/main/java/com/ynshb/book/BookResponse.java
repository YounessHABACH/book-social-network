package com.ynshb.book;

import lombok.Builder;

@Builder
public record BookResponse(
        Long id,
        String title,
        String author,
        String isbn,
        String synopsis,
        String owner,
        byte[] cover,
        double rating,
        boolean archived,
        boolean shareable
) {}
