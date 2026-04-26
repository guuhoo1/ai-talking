package com.aitalking.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.context.SaHolder;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token认证配置
 * <p>
 * 配置Sa-Token认证拦截器，实现基于token的登录验证功能。
     * 拦截所有/api/**路径的请求，排除登录和注册接口。
     * 对于特定的聊天发送接口，支持从查询参数中读取token进行认证。
 *
 * @author AI Talking
 * @date 2026-04-26
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    /**
     * 添加Sa-Token认证拦截器
     * <p>
     * 配置认证拦截器的行为：
     * <ul>
     *   <li>拦截所有/api/**路径的请求</li>
     *   <li>排除/api/auth/login和/api/auth/register路径，无需认证即可访问</li>
     *   <li>对于/api/chat/send路径，支持从查询参数中获取token</li>
     *   <li>其他路径的请求必须携带有效的登录token</li>
     * </ul>
     *
     * @param registry 拦截器注册表，用于添加Sa-Token拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> {
            String path = SaHolder.getRequest().getRequestPath();

            if (path.equals("/api/chat/send")) {
                String token = SaHolder.getRequest().getParam("token");
                if (SaFoxUtil.isNotEmpty(token)) {
                    StpUtil.setTokenValue(token);
                }
            }

            StpUtil.checkLogin();
        })).addPathPatterns("/api/**").excludePathPatterns("/api/auth/login", "/api/auth/register");
    }
}