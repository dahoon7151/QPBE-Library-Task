package com.dahoon.qpbetask.book.entity;

import com.dahoon.qpbetask.book.BookDto;
import com.dahoon.qpbetask.loan.Loan;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
@Getter
@Slf4j
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(name = "published_date", nullable = false)
    private LocalDate publishedDate;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<BookTag> bookTags = new HashSet<>();

    // 도서 삭제시 대출 기록을 따로 남기지 않음
    // 남기려면 loan 객체에 책 정보를 저장하고 외래 키 ON DELETE SET NULL
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Loan> loan = new ArrayList<>();

    public Book update(BookDto bookDto) {
        log.info("update title : {}", bookDto.getTitle());
        if (bookDto.getTitle() != null) {
            this.title = bookDto.getTitle();
        }
        if (bookDto.getAuthor() != null) {
            this.author = bookDto.getAuthor();
        }
        if (bookDto.getPublishedDate() != null) {
            this.publishedDate = bookDto.getPublishedDate();
        }
        return this;
    }

    public BookTag addTag(Tag tag) {
        log.info("addTag - 태그명 : {}", tag.getName());
        BookTag bookTag = new BookTag(this, tag);
        this.bookTags.add(bookTag);
        tag.getBookTags().add(bookTag);

        return bookTag;
    }
}
