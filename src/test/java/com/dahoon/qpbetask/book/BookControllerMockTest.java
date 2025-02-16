package com.dahoon.qpbetask.book;

import com.dahoon.qpbetask.book.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(properties = "classpath:application-test.yml")
@AutoConfigureMockMvc
class BookControllerMockTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BookRepository bookRepository;
    @MockBean
    private BookService bookService;

    private static final Logger log = LoggerFactory.getLogger(BookControllerMockTest.class);
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Test
    void 도서등록() throws Exception {
        //Given
        BookDto bookDto = new BookDto(null, "ABC", "강다훈", LocalDate.now(), null);

        given(bookService.addBook(any(BookDto.class))).willReturn(bookDto);

        // When, Then
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("ABC"));
    }

    @Test
    void 도서목록조회_출판일순페이지() throws Exception {
        // Given
        List<BookDto> bookList = List.of(
                new BookDto(null, "ABC", "강다훈", LocalDate.now(), null)
        );

        given(bookService.showBookPage(anyInt(), anyString())).willReturn(bookList);

        // When, Then
        mockMvc.perform(get("/api/books")
                        .param("page", "0")
                        .param("sort", "date")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("ABC"));
    }
}