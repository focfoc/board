package com.example.board.article.viewcount.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

@RequiredArgsConstructor
public class RedisViewCountService implements ViewCountService{

    private final StringRedisTemplate redisTemplate;

    @Override
    public void increaseViewCount(Long articleNo) {

        redisTemplate.opsForHash().increment("article:views", String.valueOf(articleNo), 1);

    }
}
