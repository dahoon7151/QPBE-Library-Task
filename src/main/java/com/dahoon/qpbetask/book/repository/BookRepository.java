package com.dahoon.qpbetask.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findAll(Pageable pageable);

    Optional<Book> findById(Long id);

    List<Book> findByTitleContaining(String title);

    List<Book> findByAuthorContaining(String author);
}
