package com.example.stomp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesMapper;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import com.redis.om.spring.annotations.EnableRedisDocumentRepositories;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableRedisDocumentRepositories(basePackages = "com.example.stomp.*")
public class StompApplication {

	public static void main(String[] args) {
		SpringApplication.run(StompApplication.class, args);

// OAuth2ClientProperties 
		new OAuth2ClientPropertiesMapper()
	}

	


}
