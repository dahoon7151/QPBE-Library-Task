package com.dahoon.qpbetask.user;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    @Cacheable(value = "refreshTokens", key = "#refreshToken", unless = "#result == null")
    Optional<User> findByRefreshToken(String refreshToken);
}
