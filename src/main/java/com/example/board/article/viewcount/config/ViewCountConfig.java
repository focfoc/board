package com.example.board.article.viewcount.config;

import com.example.board.article.repository.ArticleRepository;
import com.example.board.article.viewcount.service.DbIncrementViewCountService;
import com.example.board.article.viewcount.service.ViewCountService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ViewCountConfig {

    @Bean
    public ViewCountService viewCountService(
            ArticleRepository articleRepository, @Value("${viewcount.strategy:db}") String strategy){
        return new DbIncrementViewCountService(articleRepository);
    }


    /* @Bean
    public ViewCountService viewCountService(
            ArticleRepository articleRepository,
            @Value("${viewcount.strategy:db}") String strategy
    ) {
        return switch (strategy) {
            case "db" -> new DbIncrementViewCountService(articleRepository);
            case "optimistic" -> new OptimisticViewCountService(articleRepository);
            default -> throw new IllegalArgumentException();
        };
    }*/
}
