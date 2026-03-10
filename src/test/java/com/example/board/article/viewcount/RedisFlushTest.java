package com.example.board.article.viewcount;

import com.example.board.article.service.ArticleService;
import com.example.board.article.viewcount.scheduler.RenameBlockHook;
import com.example.board.article.viewcount.scheduler.FlushDelayTestConfig;
import com.example.board.article.viewcount.scheduler.RedisViewCountFlushScheduler;
import com.example.board.domain.Article;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Import(FlushDelayTestConfig.class)
@TestPropertySource(properties = "viewcount.strategy=redis")
@SpringBootTest
public class RedisFlushTest {

    @Autowired
    RedisViewCountFlushScheduler redisScheduler;

    @Autowired
    ArticleService articleService;

    @Autowired
    RenameBlockHook renameBlockHook;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("분산락 확인: 데이터 누락 검증")
    void flush_snapshot_does_not_lose_updates() throws InterruptedException, ExecutionException {
        //given
        int requestCnt = 10;
        int threadCnt = 16;

        Article article = new Article("테스트입니다");
        Article saveArticle = articleService.save(article);
        Long id = saveArticle.getArticleNo();

        ExecutorService executorService = Executors.newFixedThreadPool(threadCnt);

        //when
        for(int i = 0; i < requestCnt; i++){
            articleService.read(id);
        }
        //비동기로 시작 후 멈춤
        Future<?> flushA = executorService.submit(() -> redisScheduler.flush());
        renameBlockHook.awaitRenamed(10, TimeUnit.SECONDS);

        for(int i = 0; i < requestCnt; i++){
            articleService.read(id);
        }

        //재게 후 완료 확인
        renameBlockHook.release();
        flushA.get();

        //2번째 들어온 거 반영
        redisScheduler.flush();

        executorService.shutdown();

        //then
        em.clear();
        Article resultArticle = articleService.findById(id);
        assertThat(resultArticle.getViewCount()).isEqualTo(requestCnt * 2);

    }

    @Test
    @DisplayName("중복 실행 확인: flush 호출 시 ShedLock으로 하나만 실행")
    void flush_concurrentCalls_onlyOneExecutes() throws InterruptedException, ExecutionException, TimeoutException {

        //given
        int requestCnt = 10;
        int threadCnt = 16;

        Article article = new Article("테스트입니다");
        Article saveArticle = articleService.save(article);
        Long id = saveArticle.getArticleNo();

        ExecutorService executorService = Executors.newFixedThreadPool(threadCnt);

        //초기화
        renameBlockHook.reset();

        //when
        for(int i = 0; i < requestCnt; i++){
            articleService.read(id);
        }
        //비동기로 시작 후 멈춤
        Future<?> flushA = executorService.submit(() -> redisScheduler.flush());
        renameBlockHook.awaitRenamed(60, TimeUnit.SECONDS);

        //2번째 flush
        Future<?> flushB = executorService.submit(() -> redisScheduler.flush());
        flushB.get();

        //재게 후 완료 확인
        renameBlockHook.release();
        flushA.get();

        executorService.shutdown();

        //then
        em.clear();
        Article resultArticle = articleService.findById(id);
        assertThat(resultArticle.getViewCount()).isEqualTo((long) requestCnt);
        assertThat(renameBlockHook.getCount()).isEqualTo(1);

    }

}
