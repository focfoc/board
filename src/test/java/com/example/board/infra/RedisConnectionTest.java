package com.example.board.infra;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class RedisConnectionTest {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    @DisplayName("redis 연결 테스트")
    void redis_connection_test(){
        //given
        String key = "test";
        String value = "value입니다";

        //when
        redisTemplate.opsForValue().set(key, value);
        String result = redisTemplate.opsForValue().get(key);

        //then
        assertThat(result).isEqualTo(value);

        //clear
        redisTemplate.delete(key);
    }
}
