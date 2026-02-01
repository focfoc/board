package com.example.board.article.viewcount.service;

import com.example.board.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class DbIncrementViewCountService implements ViewCountService{

    private final ArticleRepository articleRepository;

    @Override
    @Transactional
    public void increaseViewCount(Long articleNo) {
        articleRepository.incrementViewCount(articleNo);
    }
}
