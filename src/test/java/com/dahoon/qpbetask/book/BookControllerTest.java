package com.dahoon.qpbetask.book;

import com.dahoon.qpbetask.book.entity.Book;
import com.dahoon.qpbetask.book.entity.BookTag;
import com.dahoon.qpbetask.book.entity.Tag;
import com.dahoon.qpbetask.book.repository.BookRepository;
import com.dahoon.qpbetask.book.repository.BookTagRepository;
import com.dahoon.qpbetask.book.repository.TagRepository;
import com.dahoon.qpbetask.user.User;
import com.dahoon.qpbetask.user.UserRepository;
import com.dahoon.qpbetask.user.component.JwtTokenProvider;
import com.dahoon.qpbetask.user.dto.JwtTokenDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookService bookService;
    @Autowired
    private BookTagRepository bookTagRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(BookControllerMockTest.class);
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private Book savedBook1;
    private Book savedBook2;
    private Tag tag1;
    private Tag tag2;
    private User savedUser;
    private JwtTokenDto jwtTokenDto;

    @BeforeEach
    void setUp() {
        savedBook1 = bookRepository.save(Book.builder()
                .title("ABC")
                .author("강다훈")
                .publishedDate(LocalDate.of(2025, 2, 10))
                .build());

        savedBook2 = bookRepository.save(Book.builder()
                .title("AEF")
                .author("이채영")
                .publishedDate(LocalDate.of(2024, 5, 3))
                .build());

        savedUser = userRepository.save(User.builder()
                .username("강다훈")
                .password("abc@123")
                .build());

        jwtTokenDto = jwtTokenProvider.generateToken(savedUser.getUsername());
    }

    @Test
    void 도서조회_MySQL에서생성한Id() throws Exception {
        // Given
        Long bookId = savedBook1.getId();

        // When & Then
        mockMvc.perform(get("/api/books/" + bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookId))
                .andExpect(jsonPath("$.author").value("강다훈"));
    }

    @Test
    void 도서수정() throws Exception {
        //Given
        BookDto updatedBookDto = new BookDto(
                savedBook1.getId(), "DEF", "이채영", LocalDate.now(), null
        );

        //When, Then
        mockMvc.perform(patch("/api/books/" + savedBook1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBookDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("DEF"))
                .andExpect(jsonPath("$.author").value("이채영"));

        Book updatedBook = bookRepository.findById(savedBook1.getId()).orElseThrow();
        assertThat(updatedBook.getTitle()).isEqualTo("DEF");
        assertThat(updatedBook.getAuthor()).isEqualTo("이채영");
    }

    @Test
    void 도서삭제() throws Exception {
        // Given
        Long bookId = savedBook1.getId();

        // When & Then
        mockMvc.perform(delete("/api/books/" + bookId))
                .andExpect(status().isNoContent());

        boolean isBookPresent = bookRepository.findById(bookId).isPresent();
        assertThat(isBookPresent).isFalse();
    }

    @Test
    void 도서조회_제목별() throws Exception {
        // Given
        String keyword = "A";

        // When & Then
        mockMvc.perform(get("/api/books/title/" + keyword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("ABC"))
                .andExpect(jsonPath("$[1].title").value("AEF"));
    }

    @Test
    void 중복태그추가() throws Exception {
        // Given
        Long bookId = savedBook1.getId();
        List<String> tags = List.of("소설", "베스트셀러");

        // When & Then
        mockMvc.perform(post("/api/books/" + bookId + "/tag")
                        .param("tag", tags.toArray(new String[0]))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tagSet.length()").value(2));

        mockMvc.perform(post("/api/books/" + bookId + "/tag")
                        .param("tag", tags.toArray(new String[0]))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tagSet.length()").value(2));
    }

    @Test
    void 태그별_도서필터링() throws Exception {
        // Given
        tag1 = tagRepository.save(new Tag("소설"));
        tag2 = tagRepository.save(new Tag("문학"));
        bookTagRepository.save(new BookTag(savedBook1, tag1));
        bookTagRepository.save(new BookTag(savedBook1, tag2));
        bookTagRepository.save(new BookTag(savedBook2, tag1));

        List<String> tags = List.of("소설", "문학");

        // When & Then
        mockMvc.perform(get("/api/books/tag")
                        .param("tag", tags.toArray(new String[0]))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("ABC"));
    }
}
