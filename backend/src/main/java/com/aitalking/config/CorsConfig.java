package com.aitalking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域资源配置
 * <p>
 * 配置应用程序的跨域访问策略，允许前端应用安全地调用后端API。
 * 针对API路径配置了灵活的跨域设置，支持各种HTTP方法和请求头。
 *
 * @author AI Talking
 * @date 2026-04-26
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * 添加跨域映射配置
     * <p>
     * 配置/xxx/api/**路径下的跨域访问策略：
     * <ul>
     *   <li>允许所有来源通过通配符模式访问</li>
     *   <li>支持GET、POST、PUT、DELETE、OPTIONS五种HTTP方法</li>
     *   <li>允许所有请求头</li>
     *   <li>允许携带凭证信息</li>
     *   <li>预检请求缓存时间为3600秒</li>
     * </ul>
     *
     * @param registry CORS注册表，用于添加跨域映射规则
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}