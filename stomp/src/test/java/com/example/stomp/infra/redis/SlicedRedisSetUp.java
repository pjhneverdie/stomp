package com.example.stomp.infra.redis;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.example.stomp.app.infra.redis.config.RedisConfig;
import com.example.stomp.app.infra.redis.config.RedisProperties;
import com.example.stomp.infra.redis.SlicedRedisSetUp.GsonBean;

import com.redis.om.spring.RedisModulesConfiguration;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited

@ExtendWith(RedisContainer.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@ActiveProfiles("test")
@DataRedisTest
@EnableConfigurationProperties(RedisProperties.class)
@Import({
        GsonBean.class,
        RedisModulesConfiguration.class,
        RedisConfig.class
})
public @interface SlicedRedisSetUp {

    @TestConfiguration
    class GsonBean {
        @Bean
        public com.google.gson.Gson gson() {
            return new com.google.gson.Gson();
        }
    }

    

}
