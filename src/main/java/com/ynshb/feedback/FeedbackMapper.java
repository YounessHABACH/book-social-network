package com.ynshb.feedback;

import com.ynshb.book.Book;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FeedbackMapper {

    public Feedback toFeedBack(FeedbackRequest request) {
        return Feedback.builder()
                .note(request.note())
                .comment(request.comment())
                .book(
                        Book.builder()
                        .id(request.bookId())
                        .build()
                )
                .build();
    }

    public FeedbackResponse toFeedBackResponse(Feedback feedback, Long userId) {
        return FeedbackResponse.builder()
                .note(feedback.getNote())
                .comment(feedback.getComment())
                .ownFeedback(Objects.equals(userId, feedback.getCreatedBy()))
                .build();
    }
}
