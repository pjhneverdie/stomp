package com.example.stomp.infra;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;

public class TestRedisContainerExtension implements BeforeAllCallback {

    private static final GenericContainer<?> REDIS = new GenericContainer<>("redis/redis-stack:7.4.0-v8")
            .withExposedPorts(6379);

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (!REDIS.isRunning()) {
            REDIS.start();

            System.setProperty("spring.data.redis.host", REDIS.getHost());
            System.setProperty("spring.data.redis.port", REDIS.getMappedPort(6379).toString());
        }
    }

}
