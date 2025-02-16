package com.dahoon.qpbetask.loan;

import com.dahoon.qpbetask.book.entity.Book;
import com.dahoon.qpbetask.book.repository.BookRepository;
import com.dahoon.qpbetask.user.User;
import com.dahoon.qpbetask.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional
    public LoanDto loanBook(LoanDto loanIds) {
        Book book = bookRepository.findById(loanIds.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 도서가 없습니다."));
        log.info("서비스 - 도서 조회 성공");

        if (loanRepository.existsByBookAndReturnDateIsNull(book)) {
            throw new IllegalArgumentException("해당 도서는 이미 대출 중입니다.");
        }

        User user = userRepository.findById(loanIds.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 사용자가 없습니다"));
        log.info("서비스 - 사용자 조회 성공");

        Loan loan = Loan.builder()
                .book(book)
                .user(user)
                .loanDate(LocalDate.now())
                .build();

        return LoanDto.toDto(loanRepository.save(loan));
    }

    @Transactional
    public String checkLoan(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 도서가 존재하지 않습니다."));
        log.info("서비스 - 도서 조회 성공");

        if (loanRepository.existsByBookAndReturnDateIsNull(book)) {
            return "대출 중인 도서입니다.";
        } else {
            return "대출 가능한 도서입니다.";
        }
    }

    @Transactional
    public void returnBook(Long id) {
        Loan loan = loanRepository.findBorrowingLoanByBookId(id)
                .orElseThrow(() -> new EntityNotFoundException("이미 반납된 도서입니다."));
        log.info("대출 정보 조회 성공");

        loanRepository.save(loan.returnBook());
    }
}
