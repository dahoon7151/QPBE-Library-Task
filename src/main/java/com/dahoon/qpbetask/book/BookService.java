package com.dahoon.qpbetask.book;

import com.dahoon.qpbetask.book.dto.BookDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {
    private final BookRepository bookRepository;
    @Transactional
    public BookDto addBook(BookDto bookDto) {
        log.info("도서 추가");
        return BookDto.toDto(bookRepository.save(bookDto.toEntity()));
    }

}
