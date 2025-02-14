package com.dahoon.qpbetask.book;

import com.dahoon.qpbetask.book.entity.Book;
import com.dahoon.qpbetask.book.entity.Tag;
import com.dahoon.qpbetask.book.repository.BookRepository;
import com.dahoon.qpbetask.book.repository.BookTagRepository;
import com.dahoon.qpbetask.book.repository.TagRepository;
import com.dahoon.qpbetask.common.cache.CacheInvalidationPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;
    private final TagRepository tagRepository;
    private final BookTagRepository bookTagRepository;
    private final CacheInvalidationPublisher cacheInvalidationPublisher;

    @Transactional
    @CacheEvict(value = "books", allEntries = true)
    public BookDto addBook(BookDto bookDto) {
        Book book = bookRepository.save(bookDto.toEntity());

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                log.info("캐시 afterCommit");
                cacheInvalidationPublisher.publishInvalidationMessage("books");
            }
        });

        return BookDto.toDto(book);
    }

    @Cacheable(value = "books", key = "#page + '-' + #sort")
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

    @Cacheable(value = "book", key = "#id")
    public BookDto showBook(Long id) {
        log.info("서비스 - 특정 도서 조회");
        return bookRepository.findById(id)
                .map(BookDto::toDto)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 도서가 없습니다."));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "book", key = "#id"), // 개별 도서 캐시 삭제
            @CacheEvict(value = "books", allEntries = true) // 목록 캐시 삭제
    })
    public BookDto updateBook(BookDto bookDto, Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 도서가 없습니다."));
        Book newBook = book.update(bookDto);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                log.info("캐시 afterCommit");
                cacheInvalidationPublisher.publishInvalidationMessage("books");
                cacheInvalidationPublisher.publishInvalidationMessage("book::" + id);
            }
        });

        return BookDto.toDto(newBook);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "book", key = "#id"), // 개별 도서 캐시 삭제
            @CacheEvict(value = "books", allEntries = true) // 목록 캐시 삭제
    })
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 도서가 없습니다."));
        bookRepository.delete(book);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                log.info("캐시 afterCommit");
                cacheInvalidationPublisher.publishInvalidationMessage("books");
                cacheInvalidationPublisher.publishInvalidationMessage("book::" + id);
            }
        });
    }

    @Cacheable(value = "booksByTitle", key = "#title")
    public List<BookDto> findBookByTitle(String title) {
        List<Book> bookList = bookRepository.findByTitleContaining(title);
        return bookList.stream()
                .map(BookDto::toDto)
                .toList();
    }

    @Cacheable(value = "booksByAuthor", key = "#author")
    public List<BookDto> findBookByAuthor(String author) {
        List<Book> bookList = bookRepository.findByAuthorContaining(author);
        return bookList.stream()
                .map(BookDto::toDto)
                .toList();
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "book", key = "#id"),
            @CacheEvict(value = "books", allEntries = true)
    })
    public BookDto addTags(Long id, List<String> tags) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 도서가 없습니다."));
        List<String> affectedTags = new ArrayList<>();

        for (String t : tags) {
            Tag tag = tagRepository.findByName(t)
                    .orElseGet(() -> tagRepository.save(new Tag(t)));

            if (bookTagRepository.existsByBookAndTag(book, tag)) {
                continue;
            }
            bookTagRepository.save(book.addTag(tag));
            affectedTags.add(t);
        }
        log.info("각 태그 저장 완료");

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                log.info("캐시 afterCommit");
                cacheInvalidationPublisher.publishInvalidationMessage("book::" + id);
                cacheInvalidationPublisher.publishInvalidationMessage("books");

                String sortedTagKey = getSortedTagsKey(affectedTags);
                if (!sortedTagKey.equals("empty")) {
                    cacheInvalidationPublisher.publishInvalidationMessage("booksByTag::" + sortedTagKey);
                }
            }
        });

        return BookDto.toDto(book);
    }

    @Cacheable(value = "booksByTag", key = "@bookService.getSortedTagsKey(#tags)")
    public List<BookDto> searchBooksByTags(List<String> tags) {
        List<Book> bookList = bookRepository.findByTags(tags, tags.size());

        return bookList.stream()
                .map(BookDto::toDto)
                .toList();
    }

    // 캐시 저장용 태그 정렬
    public String getSortedTagsKey(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return "empty";
        }

        List<String> sortedTags = new ArrayList<>(tags);
        Collections.sort(sortedTags);

        try {
            return new ObjectMapper().writeValueAsString(sortedTags);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("캐시 키 변환 오류", e);
        }
    }
}
