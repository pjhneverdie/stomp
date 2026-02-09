package com.example.stomp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class StompApplication {

	public static void main(String[] args) {
		SpringApplication.run(StompApplication.class, args);
	}

}
