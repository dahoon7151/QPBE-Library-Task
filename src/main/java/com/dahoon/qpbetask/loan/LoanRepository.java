package com.dahoon.qpbetask.loan;

import com.dahoon.qpbetask.book.entity.Book;
import com.fasterxml.jackson.annotation.OptBoolean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    boolean existsByBookAndReturnDateIsNull(Book book);

    Optional<Loan> findByBookAndReturnDateIsNull(Book book);

    @Query("select l " +
            "from Loan l " +
            "JOIN FETCH l.book " +
            "where l.book.id = :bookId and l.returnDate is null")
    Optional<Loan> findBorrowingLoanByBookId(@Param("bookId") Long bookId);
}
