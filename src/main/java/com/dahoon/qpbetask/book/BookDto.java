package com.dahoon.qpbetask.book;

import com.dahoon.qpbetask.book.entity.Book;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookDto {
    private Long id;

    @NotBlank(message = "도서 제목을 입력하세요")
    private String title;

    @NotBlank(message = "도서 저자 이름을 입력하세요")
    private String author;

    @NotNull(message = "도서 출판일을 입력하세요")
    @PastOrPresent(message = "입력된 출판일이 미래의 날짜입니다.")
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
        return BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publishedDate(book.getPublishedDate())
                .tagSet(book.getBookTags().stream()
                        .map(bookTag -> bookTag.getTag().getName())
                        .collect(Collectors.toSet()))
                .build();
    }
}
