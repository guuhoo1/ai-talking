package com.aitalking;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.aitalking.mapper")
public class AiChatbotApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiChatbotApplication.class, args);
    }
}