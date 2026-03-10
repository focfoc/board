package com.example.board.article.viewcount;

import com.example.board.article.service.ArticleService;
import com.example.board.article.viewcount.service.ViewCountService;
import com.example.board.domain.Article;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

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
        flushIfNeeded();

        //then
        Article resultArticle = articleService.findById(id);
        assertThat(resultArticle.getViewCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("조회수 증가 확인: 단일 게시글 동시성 테스트")
    void incrementViewCount_concurrency() throws InterruptedException{
        //given
        int requestCnt = 500;
        int threadCnt = 32;
        Article article = new Article("테스트입니다");
        Article saveArticle = articleService.save(article);
        Long id = saveArticle.getArticleNo();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCnt);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch latch = new CountDownLatch(requestCnt);

        ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

        //when
        for(int i = 0; i < requestCnt; i++){
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

        executorService.shutdown();
        executorService.awaitTermination(30, TimeUnit.SECONDS);

        if (!errors.isEmpty()) {
            RuntimeException ex = new RuntimeException("워커 예외: " + errors.peek());
            errors.forEach(ex::addSuppressed);
            throw ex;
        }

        flushIfNeeded();

        //then
        em.clear();
        Article resultArticle = articleService.findById(id);
        assertThat(resultArticle.getViewCount()).isEqualTo((long) requestCnt);
    }

    @Test
    @DisplayName("조회수 증가 확인: 다수 게시글 조회수 정합성 테스트")
    void incrementViewCount_multiKey_concurrency() throws InterruptedException{
        //given
        int requestCnt = 1000;
        int articleCnt = 100;
        int threadCnt = 32;
        List<Long> articleNos = new ArrayList<>();

        for(int i = 0; i < articleCnt; i++){
            Article article = new Article("테스트입니다");
            Article saveArticle = articleService.save(article);
            articleNos.add(saveArticle.getArticleNo());
        }

        ExecutorService executorService = Executors.newFixedThreadPool(threadCnt);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch latch = new CountDownLatch(requestCnt);

        ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

        //when
        for(int i = 0; i < requestCnt; i++){
            Long articleNo = articleNos.get( i % articleCnt);
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    articleService.read(articleNo);
                } catch (Throwable t) {
                    errors.add(t);
                } finally {
                    latch.countDown();
                }
            });
        }

        startLatch.countDown();
        latch.await();

        executorService.shutdown();
        executorService.awaitTermination(30, TimeUnit.SECONDS);

        if (!errors.isEmpty()) {
            RuntimeException ex = new RuntimeException("워커 예외: " + errors.peek());
            errors.forEach(ex::addSuppressed);
            throw ex;
        }

        flushIfNeeded();

        //then
        em.clear();

        long base = requestCnt / articleCnt;
        long remain = requestCnt % articleCnt;

        for(int i = 0; i < articleCnt; i++){
            Long id = articleNos.get(i);
            Article resultArticle = articleService.findById(id);
            assertThat(resultArticle.getViewCount()).isEqualTo(base +  ( i < remain ? 1 : 0 ) );
        }
    }

    void flushIfNeeded(){};

}
