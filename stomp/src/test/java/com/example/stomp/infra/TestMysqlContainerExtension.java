package com.example.stomp.infra;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.MySQLContainer;

public class TestMysqlContainerExtension implements BeforeAllCallback {

    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.4");

    @Override
    public void beforeAll(ExtensionContext context) {
        if (!MYSQL.isRunning()) {
            MYSQL.start();

            System.setProperty("spring.datasource.url", MYSQL.getJdbcUrl());
            System.setProperty("spring.datasource.username", MYSQL.getUsername());
            System.setProperty("spring.datasource.password", MYSQL.getPassword());
        }
    }

}
