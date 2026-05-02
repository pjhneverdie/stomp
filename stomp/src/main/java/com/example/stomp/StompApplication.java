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

// 일단 다 mysql 기반으로 바꾸고
// redis는 mysql에 채팅 업데이트할 때 메시지 쌓아뒀다가 저장하는 구조
// 기본적으로 프론트앤드 캐싱으로 실시간은 버티고
// 나갔다 들어와서 캐싱깨지거나 하면 그떄 redis 50 rule로 업데이트 치기

