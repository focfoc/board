package com.example.board.article.repository;

import com.example.board.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    Optional<Article> findByArticleNo(Long articleNo);

    @Modifying(clearAutomatically = true)
    @Query(""" 
            update Article a
            set a.viewCount = a.viewCount + 1
            where a.articleNo = :articleNo
            """)
    int incrementViewCount(@Param("articleNo") Long articleNo);

}
