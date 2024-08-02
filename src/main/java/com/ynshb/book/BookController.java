package com.ynshb.book;

import com.ynshb.common.PageResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Book", description = "Book API")
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<Long> saveBook(
            @RequestBody @Valid BookRequest book,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(bookService.save(book, connectedUser));
    }

    @GetMapping("{id}")
    public ResponseEntity<BookResponse> getBook(@PathVariable("id") Long id) {
        return ResponseEntity.ok(bookService.findById(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication authentication
    ) {
        return ResponseEntity.ok(bookService.findAllBooks(page, size, authentication));
    }

    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>> findAllOwnerBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication authentication
    ) {
        return ResponseEntity.ok(bookService.findAllOwnerBooks(page, size, authentication));
    }

    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findBorrowedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication authentication
    ) {
        return ResponseEntity.ok(bookService.findAllBorrowedBooks(page, size, authentication));
    }

    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findReturnedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication authentication
    ) {
        return ResponseEntity.ok(bookService.findAllReturnedBooks(page, size, authentication));
    }

    @PatchMapping("/shareable/{book-id}")
    public ResponseEntity<Long> updateShareableStatus(
            @PathVariable("book-id") Long bookId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(bookService.updateShareableStatus(bookId, authentication));
    }

    @PatchMapping("/archived/{book-id}")
    public ResponseEntity<Long> updateArchivedStatus(
            @PathVariable("book-id") Long bookId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(bookService.updateArchivedStatus(bookId, authentication));
    }

    @PostMapping("/borrow/{book-id}")
    public ResponseEntity<Long> saveBook(
            @PathVariable("book-id") Long bookId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(bookService.borrowBook(bookId, authentication));
    }

    @PostMapping("/borrow/return/{book-id}")
    public ResponseEntity<Long> returnBorrowedBook(
            @PathVariable("book-id") Long bookId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(bookService.returnBorrowedBook(bookId, authentication));
    }

    @PostMapping("/borrow/return/approve/{book-id}")
    public ResponseEntity<Long> returnBorrowedApprovedBook(
            @PathVariable("book-id") Long bookId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(bookService.returnBorrowedApprovedBook(bookId, authentication));
    }

    @PostMapping(value = "/cover/{book-id}", consumes = "multipart/form-data")
    public ResponseEntity<Void> uploadCover(
            @RequestPart MultipartFile file,
            @Parameter()
            @PathVariable("book-id") Long bookId,
            Authentication authentication
    ) {
        bookService.uploadBookCoverPicture(file, bookId, authentication);
        return ResponseEntity.accepted().build();
    }

}
