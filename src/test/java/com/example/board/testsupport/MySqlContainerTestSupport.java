package com.example.board.testsupport;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.MySQLContainer;

public final class MySqlContainerTestSupport {

    private static final MySQLContainer<?> MYSQL_CONTAINER =
            new MySQLContainer<>("mysql:8.0.36")
                    .withDatabaseName("board_test")
                    .withUsername("app")
                    .withPassword("app1234");

    static {
        MYSQL_CONTAINER.start();
    }

    private MySqlContainerTestSupport() {
    }

    public static void registerMysqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MYSQL_CONTAINER::getPassword);
        registry.add("spring.datasource.driver-class-name", MYSQL_CONTAINER::getDriverClassName);
    }
}