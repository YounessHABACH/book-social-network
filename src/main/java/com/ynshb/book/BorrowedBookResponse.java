package com.ynshb.book;

import lombok.Builder;

@Builder
public record BorrowedBookResponse(
        Long id,
        String title,
        String author,
        String isbn,
        double rating,
        boolean returned,
        boolean returnApproved
) {}
