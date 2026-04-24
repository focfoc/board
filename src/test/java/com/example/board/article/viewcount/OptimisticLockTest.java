package com.example.board.article.viewcount;

import org.junit.jupiter.api.Tag;
import org.springframework.test.context.TestPropertySource;

@Tag("experiment")
@TestPropertySource(properties = "viewcount.strategy=optimistic")
public class OptimisticLockTest extends ViewCountIntegrationTest{
}
