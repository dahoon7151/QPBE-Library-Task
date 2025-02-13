package com.dahoon.qpbetask.loan;

import com.dahoon.qpbetask.book.BookDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanDto {
    @NotNull
    @Min(1)
    private Long bookId;

    private String bookTitle;

    @NotNull
    @Min(1)
    private Long userId;

    private String username;

    private LocalDate loanDate;
    private LocalDate returnDate;

    public static LoanDto toDto(Loan loan) {
        return new LoanDto(loan.getBook().getId(),
                loan.getBook().getTitle(),
                loan.getUser().getId(),
                loan.getUser().getUsername(),
                loan.getLoanDate(),
                loan.getReturnDate());
    }
}
