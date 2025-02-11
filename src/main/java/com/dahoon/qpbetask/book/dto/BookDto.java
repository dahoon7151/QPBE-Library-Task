package com.dahoon.qpbetask.book.dto;

import com.dahoon.qpbetask.book.Book;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.LocalDate;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookDto {
    @NotBlank(message = "도서 제목을 입력하세요")
    private String title;

    @NotBlank(message = "도서 저자 이름을 입력하세요")
    private String author;

    @NotNull(message = "도서 출판일을 입력하세요")
    @PastOrPresent(message = "입력된 출판일이 미래의 날짜입니다.")
    private LocalDate publishedDate;

    public Book toEntity() {
        return Book.builder()
                .title(title)
                .author(author)
                .publishedDate(publishedDate)
                .build();
    }

    public static BookDto toDto(Book book) {
        return BookDto.builder()
                .title(book.getTitle())
                .author(book.getAuthor())
                .publishedDate(book.getPublishedDate())
                .build();
    }
}
