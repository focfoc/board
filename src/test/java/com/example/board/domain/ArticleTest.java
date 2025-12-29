package com.example.board.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

public class ArticleTest {

    @Test
    @DisplayName("게시글 생성 성공")
    void createArticle_success(){
        //given
        String title = "타이틀 입니다.";

        //when
        Article article = new Article(title);

        //then
        assertThat(article.getTitle()).isEqualTo(title);
        assertThat(article.getViewCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void updateArticle_success(){
        //given
        String title = "타이틀 입니다.";
        String updateTitle = "수정된 타이틀입니다.";

        //when
        Article article = new Article(title);
        article.updateTitle(updateTitle);

        //then
        assertThat(article.getTitle()).isEqualTo(updateTitle);

    }

    @ParameterizedTest(name = "잘못된 제목: [{0}]")
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("생성 시 잘못된 제목 유효성 검사")
    void createArticle_throwsException(String invalidTitle) {
        assertThatThrownBy(() -> new Article(invalidTitle))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("제목은 필수입니다.");
    }

    @ParameterizedTest(name = "잘못된 제목: [{0}]")
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("수정 시 잘못된 제목 유효성 검사")
    void updateArticle_throwsException(String invalidTitle) {
        //given
        Article article = new Article("타이틀입니다.");

        //when & then
        assertThatThrownBy(() -> article.updateTitle(invalidTitle))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("제목은 필수입니다.");
    }
}
