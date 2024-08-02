package com.ynshb.feedback;


import com.ynshb.book.Book;
import com.ynshb.book.BookRepository;
import com.ynshb.common.PageResponse;
import com.ynshb.exception.OperationNotPermittedException;
import com.ynshb.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final BookRepository bookRepository;
    private final FeedbackRepository feedbackRepository;
    private FeedbackMapper feedbackMapper;

    public Long save(FeedbackRequest request, Authentication authentication) {
        Long bookId = request.bookId();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id " + bookId + " not found"));

        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("You can't give feedback to this book cause of archived or not shareable");
        }
        User connectedUser = (User) authentication.getPrincipal();
        if (Objects.equals(book.getOwner().getId(), connectedUser.getId())) {
            throw new OperationNotPermittedException("You are not allowed to rate your own book");
        }
        Feedback feedback = feedbackMapper.toFeedBack(request);
        return feedbackRepository.save(feedback).getId();
    }

    public PageResponse<FeedbackResponse> findAllBookFeedbacks(Long bookId, int page, int size, Authentication authentication) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id " + bookId + " not found"));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("You can't give feedback to this book cause of archived or not shareable");
        }
        Pageable pageable = PageRequest.of(page, size);
        User user = (User) authentication.getPrincipal();
        Page<Feedback> feedbacks = feedbackRepository.findAllByBookId(book,  pageable);
        List<FeedbackResponse> feedbackResponses = feedbacks.stream()
                .map(feedback -> feedbackMapper.toFeedBackResponse(feedback, user.getId()))
                .toList();

        return new PageResponse<>(
                feedbackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );
    }
}
