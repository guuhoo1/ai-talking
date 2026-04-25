package com.aitalking.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.context.SaHolder;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加Sa-Token的拦截器
        registry.addInterceptor(new SaInterceptor(handler -> {
            // 获取当前请求路径
            String path = SaHolder.getRequest().getRequestPath();

            // 对于/api/chat/send路径，从查询参数中读取token
            if (path.equals("/api/chat/send")) {
                String token = SaHolder.getRequest().getParam("token");
                if (SaFoxUtil.isNotEmpty(token)) {
                    // 手动设置token
                    StpUtil.setTokenValue(token);
                }
            }

            // 检查登录状态
            StpUtil.checkLogin();
        })).addPathPatterns("/api/**").excludePathPatterns("/api/auth/login", "/api/auth/register");
    }
}