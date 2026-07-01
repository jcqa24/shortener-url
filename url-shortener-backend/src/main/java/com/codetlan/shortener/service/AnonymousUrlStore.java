package com.codetlan.shortener.service;

import com.codetlan.shortener.entity.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;


@Service
@RequiredArgsConstructor
public class AnonymousUrlStore {
    private static final String KEY_PREFIX = "short";
    private static final Duration TTL = Duration.ofHours(2);

    private final RedisTemplate<String, String> redisTemplate;

    public void save(String shortCode, String originalUrl){
        redisTemplate.opsForValue().set(KEY_PREFIX + shortCode, originalUrl, TTL);

    }

    public Optional<String> find(String shortCode){
        return Optional.ofNullable(redisTemplate.opsForValue().get(KEY_PREFIX + shortCode));
    }

    public boolean exists(String shortCode){
        return Boolean.TRUE.equals(redisTemplate.hasKey(KEY_PREFIX + shortCode));
    }

    public Optional<Long> remainSeconds(String shortCode){
        Long ttl = redisTemplate.getExpire(KEY_PREFIX + shortCode, TimeUnit.SECONDS);
        return (ttl != null || ttl <0 ) ? Optional.of(ttl) : Optional.empty();
    }

}
