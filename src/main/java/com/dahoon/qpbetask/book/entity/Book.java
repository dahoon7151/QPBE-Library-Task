package com.dahoon.qpbetask.book;

import com.dahoon.qpbetask.book.dto.BookDto;
import com.dahoon.qpbetask.loan.Loan;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.HashSet;
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

    private String title;

    private String author;

    @Column(name = "published_date")
    private LocalDate publishedDate;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<BookTag> bookTags = new HashSet<>();

    @OneToOne(mappedBy = "book")
    private Loan loan;

    public Book update(BookDto bookDto) {
        log.info("update title : {}", bookDto.getTitle());
        this.title = bookDto.getTitle();
        this.author = bookDto.getAuthor();
        this.publishedDate = bookDto.getPublishedDate();

        return this;
    }

    public BookTag addTag(Tag tag) {
        log.info("addTag - 태그명 : {}", tag.getName());
        BookTag bookTag = new BookTag(this, tag);
        bookTags.add(bookTag);
        tag.getBookTags().add(bookTag);

        return bookTag;
    }
}
