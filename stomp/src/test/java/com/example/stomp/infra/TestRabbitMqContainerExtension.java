package com.example.stomp.infra;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.RabbitMQContainer;

public class TestRabbitMqContainerExtension implements BeforeAllCallback {

    private static final RabbitMQContainer RABBIT = new RabbitMQContainer("rabbitmq:4.2-management");

    @Override
    public void beforeAll(ExtensionContext context) {
        if (!RABBIT.isRunning()) {
            RABBIT.start();

            System.setProperty("spring.rabbitmq.host", RABBIT.getHost());
            System.setProperty("spring.rabbitmq.port",
                    String.valueOf(RABBIT.getAmqpPort()));
            System.setProperty("spring.rabbitmq.username",
                    RABBIT.getAdminUsername());
            System.setProperty("spring.rabbitmq.password",
                    RABBIT.getAdminPassword());
        }
    }

}
