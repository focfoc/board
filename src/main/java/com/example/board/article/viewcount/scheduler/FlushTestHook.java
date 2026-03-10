package com.example.board.article.viewcount.scheduler;

//운영 환경에서 사용하지 않는 훅입니다.
public interface FlushTestHook {

    default void beforeFlush() {}

    default void afterRename() {}

}
