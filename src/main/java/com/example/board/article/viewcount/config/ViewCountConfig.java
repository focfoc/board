package com.example.board.article.viewcount.config;

import com.example.board.article.repository.ArticleRepository;
import com.example.board.article.viewcount.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ViewCountConfig {

    @Bean
    public ViewCountService viewCountService(
            ArticleRepository articleRepository,
            OptimisticLockViewCountExecutor optimisticLockViewCountExecutor,
            @Value("${viewcount.strategy:db}") String strategy){
        if(strategy.equals("optimistic")){
            return new OptimisticLockViewCountService(optimisticLockViewCountExecutor);
        }else if(strategy.equals("pessimistic")){
            return new PessimisticLockViewCountService(articleRepository);
        }

        return new DbIncrementViewCountService(articleRepository);
    }

}
