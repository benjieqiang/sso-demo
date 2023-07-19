package com.ben.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


@SpringBootApplication
@EnableRedisHttpSession
public class SsoCartApplication {
    public static void main(String[] args) {
        SpringApplication.run(SsoCartApplication.class, args);
    }
}
