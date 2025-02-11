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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
@Slf4j
@Tag(name = "Book CRUD")
public class BookController {
    private final BookService bookService;
    @PostMapping()
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
}
