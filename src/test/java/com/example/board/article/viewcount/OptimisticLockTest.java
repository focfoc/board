package com.example.board.article.viewcount;

import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "viewcount.strategy=optimistic")
public class OptimisticLockTest extends ViewCountIntegrationTest{
}
