package com.crud_base.ratelimit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class FixedWindowRateLimiter {

    private final StringRedisTemplate stringRedisTemplate;

    public FixedWindowRateLimiter(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public boolean allowRequest(
            String clientId,
            int limit,
            Duration windowSize
    ) {
        long windowIndex = System.currentTimeMillis() / windowSize.toMillis();
        String key = String.format("rate:%s:%s", clientId, windowIndex);

        Long countHits = stringRedisTemplate.opsForValue()
                .increment(key);

        if(countHits != null && countHits == 1L){
            stringRedisTemplate.expire(key, windowSize);
        }

        return clientId != null && countHits <= limit;
    }
}
