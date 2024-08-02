package com.ynshb.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Long> {

    @Query("""
            SELECT bth
            FROM BookTransactionHistory bth
            WHERE bth.user.id = :userId
    """)
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Long userId);


    @Query("""
            SELECT bth
            FROM BookTransactionHistory bth
            WHERE bth.book.owner.id = :userId
    """)
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Long userId);

    @Query("""
            SELECT COUNT(*) > 0 AS isBorrowed
            FROM BookTransactionHistory bth
            WHERE bth.book.id = :bookId
              AND bth.user.id = :userId
              AND bth.returnedApproved = false
    """)
    boolean isAlreadyBorrowedByUser(Long bookId, Long userId);

    @Query("""
            SELECT bth
            FROM BookTransactionHistory bth
            WHERE bth.book.id = :bookId
              AND bth.user.id = :userId
              AND bth.returned = false
              AND bth.returnApproved = false
    """)
    Optional<BookTransactionHistory> findByBookIdAndUserId(Long bookId, Long userId);

    @Query("""
            SELECT bth
            FROM BookTransactionHistory bth
            WHERE bth.book.id = :bookId
              AND bth.book.owner.id = :userId
              AND bth.returned = true
              AND bth.returnApproved = false
    """)
    Optional<BookTransactionHistory> findByBookIdAndOwnerId(Long bookId, Long id);
}
