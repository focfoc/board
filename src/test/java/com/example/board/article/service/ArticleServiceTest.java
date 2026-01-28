package com.example.board.article.service;

import com.example.board.article.exception.ArticleNotFoundException;
import com.example.board.article.repository.ArticleRepository;
import com.example.board.domain.Article;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;
    private ArticleService articleService;

    @BeforeEach
    void setUp() {
        articleService = new ArticleService(articleRepository);
    }

    @Test
    @DisplayName("존재하지 않는 게시글 단건 조회")
    void findById_throwsException_whenNotExistArticle(){
        //given
        Long id = 1L;
        when(articleRepository.findByArticleNo(id))
                .thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> articleService.findById(id))
                .isInstanceOf(ArticleNotFoundException.class);

    }

}
