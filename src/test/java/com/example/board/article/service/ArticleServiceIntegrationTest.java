package com.example.board.article.service;

import com.example.board.domain.Article;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class ArticleServiceIntegrationTest {

    @Autowired
    private ArticleService articleService;

    @Test
    @DisplayName("게시글 저장 후 게시글 증가 확인")
    void save_then_findById_returns_saved_article() {

        //given
        Article article = new Article("테스트입니다");

        //when
        articleService.save(article);
        Long id = article.getArticleNo();

        //then
        assertThat(id).isNotNull();
        Article resultArticle = articleService.findById(id);
        assertThat(resultArticle).isNotNull();

    }
}
