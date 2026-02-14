package com.example.board.article.viewcount;

import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "viewcount.strategy=pessimistic")
public class PessimisticLockTest extends ViewCountIntegrationTest{
}
