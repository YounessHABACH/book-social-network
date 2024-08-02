package com.ynshb.book;


import com.ynshb.file.FileUtils;
import com.ynshb.history.BookTransactionHistory;
import org.springframework.stereotype.Service;

@Service
public class BookMapper {

    public Book toBook(BookRequest bookRequest) {
        return Book.builder()
                .id(bookRequest.id())
                .title(bookRequest.title())
                .author(bookRequest.author())
                .synopsis(bookRequest.synopsis())
                .isbn(bookRequest.isbn())
                .shareable(bookRequest.shareable())
                .archived(false)
                .build();
    }

    public BookResponse toBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .synopsis(book.getSynopsis())
                .isbn(book.getIsbn())
                .shareable(book.isShareable())
                .archived(book.isArchived())
                .rating(book.getRate())
                .owner(book.getOwner().fullName())
                .cover(FileUtils.readFileFromLocation(book.getCover()))
                .build();
    }

    public BorrowedBookResponse toBorrowBookResponse(BookTransactionHistory history) {
        return BorrowedBookResponse.builder()
                .id(history.getId())
                .title(history.getBook().getTitle())
                .author(history.getBook().getAuthor())
                .isbn(history.getBook().getIsbn())
                .rating(history.getBook().getRate())
                .returned(history.isReturned())
                .returnApproved(history.isReturnApproved())
                .build();
    }
}
