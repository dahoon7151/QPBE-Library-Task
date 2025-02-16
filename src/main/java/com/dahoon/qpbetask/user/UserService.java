package com.dahoon.qpbetask.user;

import com.dahoon.qpbetask.common.cache.CacheInvalidationPublisher;
import com.dahoon.qpbetask.user.component.JwtTokenProvider;
import com.dahoon.qpbetask.user.dto.JwtTokenDto;
import com.dahoon.qpbetask.user.dto.UserDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CacheInvalidationPublisher cacheInvalidationPublisher;

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public UserDto join(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("이미 등록된 사용자 이름입니다.");
        }

        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .build();
        log.info("User 객체 생성");
        UserDto savedUserDto = UserDto.toDto(userRepository.save(user));

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                log.info("캐시 afterCommit");
                cacheInvalidationPublisher.publishInvalidationMessage("users");
            }
        });

        return savedUserDto;
    }

    @Transactional
    public JwtTokenDto login(UserDto userDto) {
        User user = userRepository.findByUsername(userDto.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("등록되지 않은 사용자입니다."));
        if (!passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }
        log.info("서비스 - 올바른 로그인 정보");

        JwtTokenDto jwtTokenDto = jwtTokenProvider.generateToken(user.getUsername());
        user.updateRefreshToken(jwtTokenDto.getRefreshToken());

        return jwtTokenDto;
    }

    @Transactional
    @CacheEvict(value = "refreshTokens", key = "#jwtTokenDto.refreshToken")
    public JwtTokenDto refreshToken(JwtTokenDto jwtTokenDto) {
        User user = userRepository.findByRefreshToken(jwtTokenDto.getRefreshToken())
                .orElseThrow(() -> new EntityNotFoundException("해당 토큰을 가진 사용자가 없습니다."));

        String username = user.getUsername();
        log.info("서비스 - 재발급 사용자 이름 : {}", username);

        JwtTokenDto newTokenDto = jwtTokenProvider.generateToken(username);
        user.updateRefreshToken(newTokenDto.getRefreshToken());

        // refreshToken은 TTL을 짧게 설정했고 정합성 문제가 발생해도 큰 문제가 없어 pub sub를 호출하지 않았음

        return newTokenDto;
    }

    @Transactional
    public void logout(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자입니다."));
        String refreshToken = user.getRefreshToken();

        if (refreshToken != null) {
            evictRefreshTokenCache(refreshToken);  // 🔹 캐시 무효화 메서드 호출
        }

        user.updateRefreshToken(null);
        userRepository.save(user);
    }

    @Cacheable(value = "users")
    public List<UserDto> showUserList() {
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .map(UserDto::toDto)
                .toList();
    }

    @Cacheable(value = "user", key = "#id")
    public UserDto showUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 사용자가 없습니다."));
        return UserDto.toDto(user);
    }

    @CacheEvict(value = "refreshTokens", key = "#refreshToken")
    public void evictRefreshTokenCache(String refreshToken) {
        // 서비스단에서 토큰 조회 이후 캐시 무효화하기 위한 용도
    }
}
