package com.dahoon.qpbetask.book;

import com.dahoon.qpbetask.book.dto.BookDto;
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
    @Transactional
    public BookDto addBook(BookDto bookDto) {
        log.info("도서 등록 서비스단");
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
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 도서가 없습니다"));
    }
}
