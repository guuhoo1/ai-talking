package com.aitalking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web通用配置
 * <p>
 * 配置应用程序的Web相关设置，包括跨域访问策略等。
 * 提供全局的跨域配置，确保各路径下的请求都能正常访问。
 *
 * @author AI Talking
 * @date 2026-04-26
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 添加跨域映射配置
     * <p>
     * 配置全局跨域访问策略：
     * <ul>
     *   <li>允许所有路径跨域访问</li>
     *   <li>支持GET、POST、PUT、DELETE、OPTIONS五种HTTP方法</li>
     *   <li>允许所有请求头</li>
     *   <li>允许携带凭证信息</li>
     * </ul>
     *
     * @param registry CORS注册表，用于添加跨域映射规则
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}