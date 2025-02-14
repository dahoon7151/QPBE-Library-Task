package com.dahoon.qpbetask.common.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CacheInvalidationSubscriber implements MessageListener {

    private final CacheManager cacheManager;

    public CacheInvalidationSubscriber(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String cacheKey = new String(message.getBody());
        log.info("Received cache invalidation message for: " + cacheKey);

        // books 캐시에서 해당 키를 삭제
        cacheManager.getCache("books").evict(cacheKey);
    }
}