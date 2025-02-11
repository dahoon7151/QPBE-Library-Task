package com.dahoon.qpbetask.book;

import com.dahoon.qpbetask.book.dto.BookDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    public Page<BookDto> showAllBook(int page, String order) {
        List<Sort.Order> sorts = new ArrayList<>();
        if (order.equals("title")) {
            sorts.add(Sort.Order.asc("title"));
        } else if (order.equals("date")) {
            sorts.add(Sort.Order.asc("publishedDate"));
        } else {
            throw new IllegalArgumentException("잘못된 정렬 기준");
        }
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Page<Book> bookPage = bookRepository.findAl(pageable);

        if (bookPage.isEmpty()) {
            return Page.empty(pageable);
        }
        return bookPage.map(BookDto::toDto);
    }
}
