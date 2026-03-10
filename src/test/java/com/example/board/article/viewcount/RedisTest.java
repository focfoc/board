package com.example.board.article.viewcount;

import com.example.board.article.viewcount.scheduler.RedisViewCountFlushScheduler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "viewcount.strategy=redis")
public class RedisTest extends ViewCountIntegrationTest{

    @Autowired
    RedisViewCountFlushScheduler redisScheduler;

    @Override
    void flushIfNeeded() {
        redisScheduler.flush();
    }
}
