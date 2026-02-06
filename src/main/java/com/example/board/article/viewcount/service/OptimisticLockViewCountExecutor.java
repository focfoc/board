package com.example.board.article.viewcount.service;

import com.example.board.article.exception.ArticleNotFoundException;
import com.example.board.article.repository.ArticleRepository;
import com.example.board.domain.Article;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OptimisticLockViewCountExecutor {

    private final ArticleRepository articleRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void executeIncrease(Long articleNo){
        Article article = articleRepository.findByArticleNo(articleNo)
                .orElseThrow(() -> new ArticleNotFoundException(articleNo));
        article.incrementViewCount();
    }
}
