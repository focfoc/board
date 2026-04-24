package com.example.board.article.viewcount;

import com.example.board.article.viewcount.scheduler.RedisViewCountFlushScheduler;
import com.example.board.testsupport.RedisContainerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "viewcount.strategy=redis")
public class RedisTest extends ViewCountIntegrationTest {

    @DynamicPropertySource
    static void redisProps(DynamicPropertyRegistry registry) {
        RedisContainerTestSupport.registerRedisProperties(registry);
    }

    @Autowired
    RedisViewCountFlushScheduler redisScheduler;

    @Override
    void flushIfNeeded() {
        redisScheduler.flush();
    }
}
