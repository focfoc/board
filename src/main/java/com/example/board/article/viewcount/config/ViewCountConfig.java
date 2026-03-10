package com.example.board.article.viewcount.config;

import com.example.board.article.repository.ArticleRepository;
import com.example.board.article.viewcount.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class ViewCountConfig {

    @Bean
    public ViewCountService viewCountService(
            ArticleRepository articleRepository,
            OptimisticLockViewCountExecutor optimisticLockViewCountExecutor,
            StringRedisTemplate stringRedisTemplate,
            @Value("${viewcount.strategy:db}") String strategy){
        if(strategy.equals("optimistic")){
            return new OptimisticLockViewCountService(optimisticLockViewCountExecutor);
        }else if(strategy.equals("pessimistic")){
            return new PessimisticLockViewCountService(articleRepository);
        }else if(strategy.equals("redis")){
            return new RedisViewCountService(stringRedisTemplate);
        }

        return new DbIncrementViewCountService(articleRepository);
    }

}
