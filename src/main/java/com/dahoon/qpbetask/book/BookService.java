package com.dahoon.qpbetask.book;

import com.dahoon.qpbetask.book.dto.BookDto;
import com.dahoon.qpbetask.book.entity.Book;
import com.dahoon.qpbetask.book.entity.Tag;
import com.dahoon.qpbetask.book.repository.BookRepository;
import com.dahoon.qpbetask.book.repository.BookTagRepository;
import com.dahoon.qpbetask.book.repository.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;
    private final TagRepository tagRepository;
    private final BookTagRepository bookTagRepository;

    @Transactional
    public BookDto addBook(BookDto bookDto) {
        return BookDto.toDto(bookRepository.save(bookDto.toEntity()));
    }

    @Transactional
    public Page<BookDto> showBookPage(int page, String sort) {
        List<Sort.Order> sorts = new ArrayList<>();
        if (sort.equals("title")) {
            sorts.add(Sort.Order.asc("title"));
        } else if (sort.equals("date")) {
            sorts.add(Sort.Order.asc("publishedDate"));
        } else {
            throw new IllegalArgumentException("잘못된 정렬 기준");
        }
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Page<Book> bookPage = bookRepository.findAll(pageable);

        if (bookPage.isEmpty()) {
            return Page.empty(pageable);
        }
        return bookPage.map(BookDto::toDto);
    }

    @Transactional
    public BookDto showBook(Long id) {
        return bookRepository.findById(id)
                .map(BookDto::toDto)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 도서가 없습니다."));
    }

    @Transactional
    public BookDto updateBook(BookDto bookDto, Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 도서가 없습니다."));
        return BookDto.toDto(book.update(bookDto));
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 도서가 없습니다."));
        bookRepository.delete(book);
    }

    @Transactional
    public List<BookDto> findBookByTitle(String title) {
        List<Book> bookList = bookRepository.findByTitleContaining(title);
        return bookList.stream()
                .map(BookDto::toDto)
                .toList();
    }

    @Transactional
    public List<BookDto> findBookByAuthor(String author) {
        List<Book> bookList = bookRepository.findByAuthorContaining(author);
        return bookList.stream()
                .map(BookDto::toDto)
                .toList();
    }

    @Transactional
    public BookDto addTags(Long id, List<String> tags) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 도서가 없습니다."));

        for (String t : tags) {
            Tag tag = tagRepository.findByName(t)
                    .orElseGet(() -> tagRepository.save(new Tag(t)));

            if (bookTagRepository.existsByBookAndTag(book, tag)) {
                continue;
            }
            bookTagRepository.save(book.addTag(tag));
        }

        return BookDto.toDto(book);
    }

    @Transactional
    public List<BookDto> searchBooksByTags(List<String> tags) {
        List<Book> bookList = bookRepository.findByTags(tags, tags.size());

        return bookList.stream()
                .map(BookDto::toDto)
                .toList();
    }
}
