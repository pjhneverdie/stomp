package com.example.stomp.infra.integration;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.images.builder.Transferable;

public class ContainerInitializer
                implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        private static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.4");

        private static final RabbitMQContainer RABBIT = new RabbitMQContainer("rabbitmq:4.2-management")
                        .withCopyToContainer(
                                        Transferable.of("[rabbitmq_management,rabbitmq_stomp]."),
                                        "/etc/rabbitmq/enabled_plugins")
                        .withExposedPorts(5672, 15672, 61613);

        private static final GenericContainer<?> REDIS = new GenericContainer<>("redis/redis-stack:7.4.0-v8")
                        .withExposedPorts(6379, 8001);

        private static boolean started = false;

        @Override
        public void initialize(ConfigurableApplicationContext context) {
                if (!started) {
                        startContainers();
                        started = true;
                }

                TestPropertyValues.of(
                                "spring.datasource.url=" + MYSQL.getJdbcUrl(),
                                "spring.datasource.username=" + MYSQL.getUsername(),
                                "spring.datasource.password=" + MYSQL.getPassword(),

                                "spring.data.redis.host=" + REDIS.getHost(),
                                "spring.data.redis.port=" + REDIS.getMappedPort(6379),

                                "spring.rabbitmq.host=" + RABBIT.getHost(),
                                "spring.rabbitmq.port=" + RABBIT.getAmqpPort(),
                                "spring.rabbitmq.username=" + RABBIT.getAdminUsername(),
                                "spring.rabbitmq.password=" + RABBIT.getAdminPassword(),
                                "spring.rabbitmq.stomp.port=" + 61613

                )
                                .applyTo(context.getEnvironment());
        }

        private void startContainers() {

                if (!REDIS.isRunning())
                        REDIS.start();
                if (!RABBIT.isRunning())
                        RABBIT.start();
                if (!MYSQL.isRunning())
                        MYSQL.start();
        }
}