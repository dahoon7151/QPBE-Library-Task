package com.dahoon.qpbetask.loan;

import com.dahoon.qpbetask.book.entity.Book;
import com.dahoon.qpbetask.book.repository.BookRepository;
import com.dahoon.qpbetask.user.User;
import com.dahoon.qpbetask.user.UserRepository;
import com.dahoon.qpbetask.user.component.JwtTokenProvider;
import com.dahoon.qpbetask.user.dto.JwtTokenDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private Book savedBook1;
    private Book savedBook2;
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
    void 도서대출() throws Exception {
        // Given
        LoanDto loanDto = new LoanDto(savedBook1.getId(), null, savedUser.getId(), null, null, null);
        String jsonRequest = objectMapper.writeValueAsString(loanDto);

        // When & Then
        mockMvc.perform(post("/api/loans")
                        .header("Authorization", "Bearer " + jwtTokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookId").value(savedBook1.getId()))
                .andExpect(jsonPath("$.userId").value(savedUser.getId()))
                .andExpect(jsonPath("$.loanDate").exists())
                .andExpect(jsonPath("$.returnDate").isEmpty());
    }

    @Test
    void 대출된도서_대출여부조회() throws Exception {
        // Given - 도서를 대출 처리
        loanRepository.save(Loan.builder()
                .book(savedBook1)
                .user(savedUser)
                .loanDate(LocalDate.now())
                .build());

        // When & Then
        mockMvc.perform(get("/api/loans/" + savedBook1.getId())
                        .header("Authorization", "Bearer " + jwtTokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("대출 중인 도서입니다."));
    }

    @Test
    void 도서반납() throws Exception {
        // Given
        Loan savedLoan = loanRepository.save(Loan.builder()
                .book(savedBook1)
                .user(savedUser)
                .loanDate(LocalDate.now())
                .build());

        // When & Then
        mockMvc.perform(patch("/api/loans/" + savedBook1.getId())
                        .header("Authorization", "Bearer " + jwtTokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("반납되었습니다."));

        Loan loanAfterReturn = loanRepository.findById(savedLoan.getId()).orElseThrow();
        assertThat(loanAfterReturn.getReturnDate()).isNotNull();
    }
}
