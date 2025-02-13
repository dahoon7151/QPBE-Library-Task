package com.dahoon.qpbetask.loan;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/api/loans")
@Tag(name = "Loan CRUD")
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    @Operation(summary = "도서 대출", description = "대출 테이블에 도서와 사용자에 대한 대출 정보를 저장")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "도서 대출 성공"),
            @ApiResponse(responseCode = "400", description = "이미 대출 중인 도서"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 도서 또는 사용자")
    })
    public ResponseEntity<LoanDto> loanBook(@RequestBody @Valid LoanDto loanDto) {
        log.info("도서 대출 컨트롤러 - 도서 : {}, 사용자 : {}", loanDto.getBookId(), loanDto.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.loanBook(loanDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "대출 여부 확인", description = "반납일을 체크하여 대출 여부를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대출 여부 조회 성공", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 도서")
    })
    public ResponseEntity<String> checkLoan(
            @Parameter(description = "도서 ID", example = "1", in = ParameterIn.PATH)
            @PathVariable(value = "id") @Min(1) Long bookId) {
        log.info("대출 여부 확인 컨트롤러 - 도서ID : {}", bookId);

        return ResponseEntity.ok(loanService.checkLoan(bookId));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "도서 반납", description = "도서 ID를 통해 반납되지 않은 대출 기록을 조회 후 반납 처리")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "반납 성공", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "이미 반납된 도서")
    })
    public ResponseEntity<String> returnBook(
            @Parameter(description = "도서 ID", example = "1", in = ParameterIn.PATH)
            @PathVariable(value = "id") @Min(1) Long bookId) {
        log.info("도서 반납 컨트롤러 - 도서 ID : {}", bookId);
        loanService.returnBook(bookId);
        return ResponseEntity.ok("반납되었습니다.");
    }
}
