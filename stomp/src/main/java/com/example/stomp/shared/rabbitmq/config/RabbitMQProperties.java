package com.example.stomp.shared.rabbitmq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.rabbitmq")
public record RabbitMQProperties(
        String host,
        int port,
        String username,
        String password,
        Stomp stomp) {
    public record Stomp(int port) {
    }
}
