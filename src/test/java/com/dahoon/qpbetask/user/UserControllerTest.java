package com.dahoon.qpbetask.user;

import com.dahoon.qpbetask.user.component.JwtTokenProvider;
import com.dahoon.qpbetask.user.dto.JwtTokenDto;
import com.dahoon.qpbetask.user.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private User savedUser;

    @BeforeEach
    void setUp() {
        savedUser = userRepository.save(
                User.builder()
                        .username("testuser")
                        .password(passwordEncoder.encode("abcd@1234"))
                        .build()
        );
    }

    @Test
    void 사용자_등록() throws Exception {
        // Given
        UserDto testUser = new UserDto(null,"newuser", "abcd@1234");
        String jsonRequest = objectMapper.writeValueAsString(testUser);

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(testUser.getUsername()));
    }

    @Test
    void 로그인() throws Exception {
        // Given
        UserDto validUser = new UserDto(null, "testuser", "abcd@1234");
        String jsonRequest = objectMapper.writeValueAsString(validUser);

        // When & Then
        mockMvc.perform(post("/api/users/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());

        User user = userRepository.findByUsername(validUser.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("등록되지 않은 사용자"));
        assertThat(user.getRefreshToken()).isNotEmpty();
    }

    @Test
    void 로그인_Valid검증() throws Exception {
        // Given
        UserDto invalidUser = new UserDto(null, "", "abcd1234");
        String jsonRequest = objectMapper.writeValueAsString(invalidUser);

        // When & Then
        mockMvc.perform(post("/api/users/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 로그아웃_AuthorizationPrincipal() throws Exception {
        // Given
        JwtTokenDto jwtTokenDto = jwtTokenProvider.generateToken(savedUser.getUsername());

        // When & Then
        mockMvc.perform(delete("/api/users/token")
                        .header("Authorization", "Bearer " + jwtTokenDto.getAccessToken()))
                .andExpect(status().isNoContent());

        User userAfterLogout = userRepository.findByUsername(savedUser.getUsername()).orElseThrow();
        assertThat(userAfterLogout.getRefreshToken()).isNull();
    }

    @Test
    void 사용자목록_조회() throws Exception {
        // Given
        userRepository.save(User.builder()
                .username("강다훈")
                .password(passwordEncoder.encode("kangdahoon!1234"))
                .build());
        JwtTokenDto jwtTokenDto = jwtTokenProvider.generateToken(savedUser.getUsername());

        // When & Then
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + jwtTokenDto.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[1].username").value("강다훈"));
    }

    @Test
    void 비밀번호_특수문자없음_회원가입_실패() throws Exception {
        // Given
        UserDto invalidUser = new UserDto(null, "pwWrong", "Password123");
        String jsonRequest = objectMapper.writeValueAsString(invalidUser);

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }
}