package com.dahoon.qpbetask.loan;

import com.dahoon.qpbetask.book.Book;
import com.dahoon.qpbetask.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_id")
    private Long id;

    @Column(name = "loan_date")
    private LocalDateTime loanDate;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    @OneToOne
    @JoinColumn(name = "book_id", unique = true)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
