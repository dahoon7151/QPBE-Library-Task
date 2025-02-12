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

    @Query("select b " +
            "from Book b " +
            "JOIN b.bookTags bt " +
            "JOIN bt.tag t " +
            "where t.name in :tagNames " +
            "group by b " +
            "having count(distinct t) = :tagCount ")
    List<Book> findByTags(@Param("tagNames") List<String> tags, @Param("tagCount") int tagCount);
}
