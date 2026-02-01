package com.example.board.article.service;

import com.example.board.article.exception.ArticleNotFoundException;
import com.example.board.domain.Article;
import jakarta.persistence.EntityManager;
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

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("게시글 저장 후 게시글 저장 확인")
    void saveArticle_success() {

        //given
        Article article = new Article("테스트입니다");

        //when
        articleService.save(article);
        Long id = article.getArticleNo();

        //then
        em.flush();
        em.clear();
        assertThat(id).isNotNull();
        Article resultArticle = articleService.findById(id);
        assertThat(resultArticle).isNotNull();

    }

    @Test
    @DisplayName("게시글 수정 후 갱신 확인")
    void updateArticle_success(){

        //given
        Article article = new Article("테스트입니다");
        articleService.save(article);
        Long id = article.getArticleNo();

        //when
        articleService.update(id, "테스트를 수정했습니다.");

        //then
        em.flush();
        em.clear();
        Article resultArticle = articleService.findById(id);
        assertThat(resultArticle.getTitle()).isEqualTo("테스트를 수정했습니다.");
        assertThat(resultArticle.getViewCount()).isEqualTo(0L);

    }

    @Test
    @DisplayName("게시글 삭제 확인")
    void deleteArticle_success(){

        //given
        Article article = new Article("테스트입니다.");
        articleService.save(article);
        Long id = article.getArticleNo();

        //when
        articleService.delete(id);

        //then
        em.flush();
        em.clear();
        assertThatThrownBy(() -> articleService.findById(id))
                .isInstanceOf(ArticleNotFoundException.class);

    }
}
