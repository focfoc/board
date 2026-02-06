package com.example.board.article.viewcount.service;

import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@RequiredArgsConstructor
public class OptimisticLockViewCountService implements ViewCountService{

    private int retry = 3;

    private final OptimisticLockViewCountExecutor optimisticLockViewCountExecutor;

    @Override
    public void increaseViewCount(Long articleNo) {

        for(int i = 1; i <= retry; i++){
            try{
                optimisticLockViewCountExecutor.executeIncrease(articleNo);
                return;
            } catch (ObjectOptimisticLockingFailureException e){
                if(i == retry) throw e;
            }
        }
    }
}
