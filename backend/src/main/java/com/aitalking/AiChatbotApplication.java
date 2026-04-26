package com.aitalking;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AI Talking 聊天机器人应用主类
 * 启动 Spring Boot 应用程序，配置 MyBatis Mapper 扫描路径
 *
 * @author AI Talking
 * @date 2026-04-26
 */
@SpringBootApplication
@MapperScan("com.aitalking.mapper")
public class AiChatbotApplication {

    /**
     * 应用启动入口
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(AiChatbotApplication.class, args);
    }
}