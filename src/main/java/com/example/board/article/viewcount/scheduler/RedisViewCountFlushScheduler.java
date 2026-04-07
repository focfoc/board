package com.example.board.article.viewcount.scheduler;

import com.example.board.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RedisViewCountFlushScheduler {

    private static final Logger log = LoggerFactory.getLogger(RedisViewCountFlushScheduler.class);
    private final StringRedisTemplate redisTemplate;
    private final ArticleRepository articleRepository;
    private final FlushTestHook flushTestHook;

    private static final String KEY = "article:views";
    private static final String SYNC_KEY_PREFIX = "article:view:sync";

    @Scheduled(fixedDelay = 60 * 1000)
    @SchedulerLock(name = "flushViewCount")
    @Transactional
    public void flush(){

        //실패한 flush가 있는 경우 재시도
        recoverSyncKeys();

        flushTestHook.beforeFlush();

        if(!redisTemplate.hasKey(KEY)) return;

        String syncKey = SYNC_KEY_PREFIX + System.currentTimeMillis() + ":" + UUID.randomUUID();
        redisTemplate.rename(KEY, syncKey);

        flushTestHook.afterRename();

        flushSyncKey(syncKey);
    }


    public void recoverSyncKeys(){
        ScanOptions scanOptions = ScanOptions.scanOptions()
                            .match(SYNC_KEY_PREFIX + "*").count(100).build();
        Cursor<String> cursor = redisTemplate.scan(scanOptions);
        while(cursor.hasNext()){
            String syncKey = cursor.next();
            flushSyncKey(syncKey);
        }

    }

    private void flushSyncKey(String syncKey){

        Map<@NonNull Object, Object> entries = redisTemplate.opsForHash().entries(syncKey);

        if(entries.isEmpty()){
            redisTemplate.delete(syncKey); // 빈 값인 경우에도 삭제
            return;
        }

        try{
            for(Map.Entry<Object, Object> map : entries.entrySet()){
                Long articleNo = Long.parseLong(String.valueOf(map.getKey()));
                Long increment = Long.parseLong(String.valueOf(map.getValue()));

                articleRepository.updateViewCount(articleNo, increment);
            }
        } catch (Exception e){
            log.error("flush error : " + syncKey);
            throw e;
        }

        redisTemplate.delete(syncKey);
    }

}
