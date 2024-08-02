package com.ynshb.feedback;

import lombok.Builder;

@Builder
public record FeedbackResponse(
        Long id,
        Double note,
        String comment,
        boolean ownFeedback,
        Long bookId
) {
}
