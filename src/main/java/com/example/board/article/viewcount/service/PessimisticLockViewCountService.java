package com.example.board.article.viewcount.service;

import com.example.board.article.exception.ArticleNotFoundException;
import com.example.board.article.repository.ArticleRepository;
import com.example.board.domain.Article;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class PessimisticLockViewCountService implements ViewCountService{

    private final ArticleRepository articleRepository;

    @Override
    @Transactional
    public void increaseViewCount(Long articleNo) {
        Article article = articleRepository.findByArticleNoForUpdate(articleNo)
                .orElseThrow( () -> new ArticleNotFoundException(articleNo));
        articleRepository.incrementViewCount(articleNo);
    }
}
