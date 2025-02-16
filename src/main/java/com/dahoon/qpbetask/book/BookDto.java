package com.dahoon.qpbetask.book;

import com.dahoon.qpbetask.book.entity.Book;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class BookDto {

    public interface OnUpdate {}

    private Long id;

    @NotBlank(message = "도서 제목을 입력하세요")
    private String title;

    @NotBlank(message = "도서 저자 이름을 입력하세요")
    private String author;

    @NotNull(message = "도서 출판일을 입력하세요")
    @PastOrPresent(message = "입력된 출판일이 미래의 날짜입니다.", groups = OnUpdate.class)
    private LocalDate publishedDate;

    private Set<String> tagSet;

    public Book toEntity() {
        return Book.builder()
                .title(title)
                .author(author)
                .publishedDate(publishedDate)
                .build();
    }

    public static BookDto toDto(Book book) {
        log.info("BookDto toDto 메소드");
        return new BookDto(book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublishedDate(),
                book.getBookTags().stream()
                        .map(bookTag -> bookTag.getTag().getName())
                        .collect(Collectors.toSet()));
    }
}
