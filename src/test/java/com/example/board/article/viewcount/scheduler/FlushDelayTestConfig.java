package com.example.board.article.viewcount.scheduler;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class FlushDelayTestConfig {

    @Bean
    @Primary
    public FlushTestHook flushTestHook(){
        return new RenameBlockHook();
    }
}
