package com.ynshb.feedback;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FeedbackRequest(
        @Positive(message = "Feedback rate must be positive")
        @Min(value = 1, message = "Feedback rate must be at least 1")
        @Max(value = 5, message = "Feedback rate must not exceed 5")
        Double note,
        @NotEmpty(message = "Comment must be no null or empty")
        String comment,
        @NotNull(message = "Book id is required")
        Long bookId
) {
}
