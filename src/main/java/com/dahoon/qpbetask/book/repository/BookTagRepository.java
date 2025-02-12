package com.dahoon.qpbetask.book.repository;

import com.dahoon.qpbetask.book.entity.Book;
import com.dahoon.qpbetask.book.entity.BookTag;
import com.dahoon.qpbetask.book.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookTagRepository extends JpaRepository<BookTag, Long> {
    boolean existsByBookAndTag(Book book, Tag tag);
}
