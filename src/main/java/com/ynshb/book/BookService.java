package com.ynshb.book;


import com.ynshb.common.PageResponse;
import com.ynshb.exception.OperationNotPermittedException;
import com.ynshb.file.FileStorageService;
import com.ynshb.history.BookTransactionHistory;
import com.ynshb.history.BookTransactionHistoryRepository;
import com.ynshb.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository bookHistoryRepository;
    private final BookMapper bookMapper;
    private final FileStorageService fileStorageService;


    public Long save(BookRequest bookRequest, Authentication authentication) {
        User connectedUser = (User) authentication.getPrincipal();
        Book book = bookMapper.toBook(bookRequest);
        book.setOwner(connectedUser);
        return bookRepository.save(book).getId();
    }

    public BookResponse findById(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::toBookResponse)
                .orElseThrow(
                        () -> new EntityNotFoundException("Book with id " + id + " not found")
                );
    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication authentication) {
        User connectedUser = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, connectedUser.getId());
        List<BookResponse> bookResponses = books.stream().map(bookMapper::toBookResponse).toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BookResponse> findAllOwnerBooks(int page, int size, Authentication authentication) {
        User connectedUser = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(BookSpecification.withOwner(connectedUser.getId()), pageable);
        List<BookResponse> bookResponses = books.stream().map(bookMapper::toBookResponse).toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication authentication) {
        User connectedUser = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks =
                bookHistoryRepository.findAllBorrowedBooks(pageable, connectedUser.getId());
        List<BorrowedBookResponse> borrowedBookResponses = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowBookResponse)
                .toList();
        return new PageResponse<>(
                borrowedBookResponses,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication authentication) {
        User connectedUser = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks =
                bookHistoryRepository.findAllReturnedBooks(pageable, connectedUser.getId());
        List<BorrowedBookResponse> borrowedBookResponses = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowBookResponse)
                .toList();
        return new PageResponse<>(
                borrowedBookResponses,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    public Long updateShareableStatus(Long bookId, Authentication authentication) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id " + bookId + " not found"));
        User connectedUser = (User) authentication.getPrincipal();
        if (!Objects.equals(book.getOwner().getId(), connectedUser.getId())) {
            throw new OperationNotPermittedException("You are not allowed to update this book shareable status");
        }
        book.setShareable(!book.isShareable());
        return bookRepository.save(book).getId();
    }

    public Long updateArchivedStatus(Long bookId, Authentication authentication) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id " + bookId + " not found"));
        User connectedUser = (User) authentication.getPrincipal();
        if (!Objects.equals(book.getOwner().getId(), connectedUser.getId())) {
            throw new OperationNotPermittedException("You are not allowed to update this book archived status");
        }
        book.setArchived(!book.isArchived());
        return bookRepository.save(book).getId();
    }

    public Long borrowBook(Long bookId, Authentication authentication) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id " + bookId + " not found"));

        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("This book cannot be be borrowed");
        }
        User connectedUser = (User) authentication.getPrincipal();
        if (Objects.equals(book.getOwner().getId(), connectedUser.getId())) {
            throw new OperationNotPermittedException("You are not allowed to borrow your own book");
        }
        final boolean isAlreadyBorrowed =
                bookHistoryRepository.isAlreadyBorrowedByUser(bookId, connectedUser.getId());
        if (isAlreadyBorrowed) {
            throw new OperationNotPermittedException("You have already borrowed this book");
        }
        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
                .book(book)
                .user(connectedUser)
                .returned(false)
                .returnApproved(false)
                .build();

        return bookHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Long returnBorrowedBook(Long bookId, Authentication authentication) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id " + bookId + " not found"));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("This book cannot be be borrowed");
        }
        User connectedUser = (User) authentication.getPrincipal();
        if (Objects.equals(book.getOwner().getId(), connectedUser.getId())) {
            throw new OperationNotPermittedException("You are not allowed to borrow your own book");
        }
        BookTransactionHistory bookTransactionHistory =
                bookHistoryRepository.findByBookIdAndUserId(bookId, connectedUser.getId())
                .orElseThrow(() -> new OperationNotPermittedException("You have not borrowed this book"));
        bookTransactionHistory.setReturned(true);
        return bookHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Long returnBorrowedApprovedBook(Long bookId, Authentication authentication) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id " + bookId + " not found"));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("This book cannot be be borrowed");
        }
        User connectedUser = (User) authentication.getPrincipal();
        if (Objects.equals(book.getOwner().getId(), connectedUser.getId())) {
            throw new OperationNotPermittedException("You are not allowed to borrow your own book");
        }
        BookTransactionHistory bookTransactionHistory =
                bookHistoryRepository.findByBookIdAndOwnerId(bookId, connectedUser.getId())
                        .orElseThrow(() -> new OperationNotPermittedException("You have not returned this book"));
        bookTransactionHistory.setReturnApproved(true);
        return bookHistoryRepository.save(bookTransactionHistory).getId();
    }

    public void uploadBookCoverPicture(MultipartFile file, Long bookId, Authentication authentication) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id " + bookId + " not found"));
        User connectedUser = (User) authentication.getPrincipal();
        var bookCover = fileStorageService.saveFile(file, connectedUser.getId());
        book.setCover(bookCover);
        bookRepository.save(book);
    }
}
