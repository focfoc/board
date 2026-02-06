package com.example.board.article.viewcount;

import com.example.board.article.service.ArticleService;
import com.example.board.article.viewcount.service.ViewCountService;
import com.example.board.domain.Article;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public abstract class ViewCountIntegrationTest {

    @Autowired
    ArticleService articleService;

    @Autowired
    ViewCountService viewCountService;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("조회수 증가 확인")
    void incrementViewCount_success(){
        //given
        Article article = new Article("테스트입니다");
        Article saveArticle = articleService.save(article);
        Long id = saveArticle.getArticleNo();

        //when
        articleService.read(id);

        //then
        Article resultArticle = articleService.findById(id);
        assertThat(resultArticle.getViewCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("조회수 증가 확인: 동시성 테스트")
    void incrementViewCount_concurrency() throws InterruptedException{
        //given
        int threadCnt = 200;
        Article article = new Article("테스트입니다");
        Article saveArticle = articleService.save(article);
        Long id = saveArticle.getArticleNo();
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch latch = new CountDownLatch(threadCnt);

        ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

        //when
        for(int i = 0; i < threadCnt; i++){
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    articleService.read(id);
                    //viewCountService.increaseViewCount(id);
                } catch (Throwable t) {
                    errors.add(t);
                } finally {
                    latch.countDown();
                }
            });
        }

        startLatch.countDown();
        latch.await();

        if (!errors.isEmpty()) {
            RuntimeException ex = new RuntimeException("워커 예외: " + errors.peek());
            errors.forEach(ex::addSuppressed);
            throw ex;
        }

        //then
        em.clear();
        Article resultArticle = articleService.findById(id);
        assertThat(resultArticle.getViewCount()).isEqualTo((long) threadCnt);
    }

}
