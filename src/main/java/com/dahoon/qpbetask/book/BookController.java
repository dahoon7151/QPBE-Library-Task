package com.dahoon.qpbetask.book;

import com.dahoon.qpbetask.book.dto.BookDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
@Slf4j
@Tag(name = "Book CRUD")
public class BookController {
    private final BookService bookService;
    @PostMapping
    @Operation(summary = "도서 등록", description = "새로운 도서를 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "도서가 등록되었습니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "", description = "") // Valid 검증
    })
    public ResponseEntity<BookDto> addBook(@RequestBody @Valid BookDto bookDto) {
        log.info("도서 추가 컨트롤러 - 제목 : {}, 저자 : {}, 출판일 : {}",
                bookDto.getTitle(),
                bookDto.getAuthor(),
                bookDto.getPublishedDate()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.addBook(bookDto));
    }

    @GetMapping
    public ResponseEntity<Page<BookDto>> showBookPage(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "sort", required = false, defaultValue = "title") String sort) {
        log.info("도서 조회 컨트롤러 - 페이지 : {}, 정렬기준 : {}", page, sort);

        return ResponseEntity.ok(bookService.showBookPage(page, sort));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> showBook(@PathVariable(value = "id") Long bookId) {
        log.info("특정 도서 조회 컨트롤러");

        return ResponseEntity.ok(bookService.showBook(bookId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable(value = "id") Long bookId) {
        log.info("도서 수정 컨트롤러");

        return ResponseEntity.ok(null);
    }
}
