package com.example.board.article.viewcount.scheduler;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RenameBlockHook implements FlushTestHook{

    private AtomicInteger count = new AtomicInteger();

    private CountDownLatch renamedLatch = new CountDownLatch(1);
    private CountDownLatch proceedLatch = new CountDownLatch(1);

    @Override
    public void beforeFlush() {
        count.incrementAndGet();
    }

    @Override
    public void afterRename() {
        renamedLatch.countDown();

        try {
            boolean ok = proceedLatch.await(60, TimeUnit.SECONDS);
            if (!ok) {
                throw new IllegalStateException("afterRename 대기 타임아웃");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }

    // flush가 rename 지점까지 도달했는지 기다림
    public void awaitRenamed(long timeout, TimeUnit unit) throws InterruptedException {
        boolean ok = renamedLatch.await(timeout, unit);
        if (!ok) {
            throw new IllegalStateException("rename 도달 대기 타임아웃");
        }
    }

    public void release() {
        proceedLatch.countDown();
    }

    public int getCount() {
        return count.get();
    }

    public void reset() {
        count.set(0);
        renamedLatch = new CountDownLatch(1);
        proceedLatch = new CountDownLatch(1);
    }
}
