package com.dahoon.qpbetask.common.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheInvalidationPublisher {

    private final StringRedisTemplate redisTemplate;

    public void publishInvalidationMessage(String cacheKey) {
        redisTemplate.convertAndSend("cacheInvalidationChannel", cacheKey);
    }
}
