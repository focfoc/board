package com.example.board.infra;

import com.example.board.testsupport.MySqlContainerTestSupport;
import com.example.board.testsupport.RedisContainerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class RedisConnectionTest {

    @DynamicPropertySource
    static void redisProps(DynamicPropertyRegistry registry) {
        MySqlContainerTestSupport.registerMysqlProperties(registry);
        RedisContainerTestSupport.registerRedisProperties(registry);
    }

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
