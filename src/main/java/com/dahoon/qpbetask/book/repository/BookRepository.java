package com.dahoon.qpbetask.book.repository;

import com.dahoon.qpbetask.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findAll(Pageable pageable);

    Optional<Book> findById(Long id);

    List<Book> findByTitleContaining(String title);

    List<Book> findByAuthorContaining(String author);

    @Query("SELECT b " +
            "FROM Book b " +
            "JOIN b.bookTags bt " +
            "JOIN bt.tag t " +
            "WHERE t.name IN (:tagNames) " +
            "GROUP BY b " +
            "HAVING COUNT(DISTINCT t) = :tagCount")
    List<Book> findByTags(@Param("tagNames") List<String> tags, @Param("tagCount") int tagCount);

    @Query("SELECT t.name " +
            "FROM BookTag bt " +
            "JOIN bt.tag t " +
            "WHERE bt.book.id = :bookId")
    List<String> findTagNamesByBookId(@Param("bookId") Long bookId);
}
