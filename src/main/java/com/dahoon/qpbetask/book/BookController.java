package com.dahoon.qpbetask.book;

import com.dahoon.qpbetask.book.dto.BookDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
@Slf4j
@Validated
@Tag(name = "Book CRUD")
public class BookController {

    private final BookService bookService;

    @PostMapping
    @Operation(summary = "도서 등록", description = "새로운 도서를 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "등록 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 데이터 입력")
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
    @Operation(summary = "도서 목록 조회", description = "등록된 모든 도서를 페이지로 나누어 조회하고 제목 또는 출판일 기준으로 정렬합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공, 없다면 빈 페이지 반환", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 정렬기준")
    })
    public ResponseEntity<Page<BookDto>> showBookPage(
            @Parameter(description = "페이지 번호", example = "1", in = ParameterIn.QUERY)
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,

            @Parameter(description = "정렬 기준", example = "date", in = ParameterIn.QUERY)
            @RequestParam(name = "sort", required = false, defaultValue = "title") String sort) {
        log.info("도서 조회 컨트롤러 - 페이지 : {}, 정렬기준 : {}", page, sort);

        return ResponseEntity.ok(bookService.showBookPage(page, sort));
    }

    @GetMapping("/{id}")
    @Operation(summary = "특정 도서 조회", description = "도서 ID로 특정 도서 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "해당 ID의 도서가 없음"),
            @ApiResponse(responseCode = "400", description = "도서 ID는 0보다 큽니다")
    })
    public ResponseEntity<BookDto> showBook(
            @Parameter(description = "도서 ID", example = "1", in = ParameterIn.PATH)
            @PathVariable(value = "id") @Min(1) Long bookId) {
        log.info("특정 도서 조회 컨트롤러");

        return ResponseEntity.ok(bookService.showBook(bookId));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "도서 수정", description = "특정 도서의 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "해당 ID의 도서가 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 데이터 입력")
    })
    public ResponseEntity<BookDto> updateBook(@RequestBody @Valid BookDto bookDto,
                                              @Parameter(description = "도서 ID", example = "1", in = ParameterIn.PATH)
                                              @PathVariable(value = "id")
                                              @Min(0) @NotNull(message = "도서 ID를 입력하세요") Long bookId) {
        log.info("도서 수정 컨트롤러 title : {}", bookDto.getTitle());

        return ResponseEntity.ok(bookService.updateBook(bookDto, bookId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "도서 삭제", description = "특정 도서 정보를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 도서가 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 데이터 입력")
    })
    public ResponseEntity<String> deleteBook(
            @Parameter(description = "도서 ID", example = "1", in = ParameterIn.PATH)
            @PathVariable(value = "id")
            @Min(0) @NotNull(message = "도서 ID를 입력하세요") Long bookId) {
        log.info("도서 삭제 컨트롤러");
        bookService.deleteBook(bookId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/title/{title}")
    @Operation(summary = "제목으로 도서 검색", description = "검색어가 제목에 포함된 도서 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공, 없다면 빈 리스트 반환"),
            @ApiResponse(responseCode = "400", description = "잘못된 데이터 입력")
    })
    public ResponseEntity<List<BookDto>> findBookByTitle(
            @Parameter(description = "제목 검색어", in = ParameterIn.PATH)
            @PathVariable(value = "title")
            @NotBlank(message = "제목을 입력하세요") String title) {
        log.info("도서 검색 title : {}", title);

        return ResponseEntity.ok(bookService.findBookByTitle(title));
    }

    @GetMapping("/author/{author}")
    @Operation(summary = "저자명으로 도서 검색", description = "검색어가 저자명에 포함된 도서 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공, 없다면 빈 리스트 반환"),
            @ApiResponse(responseCode = "400", description = "잘못된 데이터 입력")
    })
    public ResponseEntity<List<BookDto>> findBookByAuthor(
            @Parameter(description = "저자명 검색어", in = ParameterIn.PATH)
            @PathVariable(value = "author")
            @NotBlank(message = "저자명을 입력하세요") String author) {
        log.info("도서 검색 author : {}", author);

        return ResponseEntity.ok(bookService.findBookByAuthor(author));
    }
    @PostMapping("/{id}/tag")
    @Operation(summary = "도서에 태그 추가", description = "도서에 태그를 한개 이상 추가합니다. 이미 추가된 태그는 추가하지 않습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "태그 추가 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "해당 ID의 도서가 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 데이터 입력")
    })
    public ResponseEntity<BookDto> addBookTags(
            @Parameter(description = "도서 ID", example = "1", in = ParameterIn.PATH)
            @PathVariable(value = "id")
            @Min(0) @NotNull(message = "도서 ID를 입력하세요") Long bookId,

            @Parameter(description = "추가할 태그", example = "소설", in = ParameterIn.QUERY)
            @RequestParam(name = "tag") List<String> tags) {
        log.info("도서 태그 추가 컨트롤러 - 태그 목록 : {}", tags);

        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.addTags(bookId, tags));
    }

    @GetMapping("/tag")
    @Operation(summary = "태그별 도서 필터링", description = "선택한 태그별 도서를 필터링하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "조회 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "해당 ID의 도서가 없음")
    })
    public ResponseEntity<List<BookDto>> searchBooksByTags(
            @Parameter(description = "추가할 태그", example = "소설", in = ParameterIn.QUERY)
            @RequestParam(name = "tag") List<String> tags) {
        log.info("도서 태그 필터링 컨트롤러 - 태그 목록 : {}", tags);

        return ResponseEntity.ok(bookService.searchBooksByTags(tags));
    }
}
