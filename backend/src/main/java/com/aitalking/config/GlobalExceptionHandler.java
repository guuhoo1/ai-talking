package com.aitalking.config;

import com.aitalking.dto.Result;
import cn.dev33.satoken.exception.NotLoginException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * <p>
 * 用于捕获并处理应用程序中的各类异常，统一返回格式化的错误信息。
 * 主要处理未登录异常和其他通用异常，确保前端能够获得清晰的错误提示。
 *
 * @author AI Talking
 * @date 2026-04-26
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理未登录异常
     * <p>
     * 当用户未登录或token无效/已过期时调用此方法。
     * 返回401状态码和错误信息，提示前端需要重新登录。
     *
     * @param e NotLoginException异常对象
     * @return 包含错误信息的Result对象，状态码为401
     */
    @ExceptionHandler(NotLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<?> handleNotLoginException(NotLoginException e) {
        return Result.fail(401, "token 无效或已过期，请重新登录");
    }

    /**
     * 处理通用异常
     * <p>
     * 捕获所有其他未明确处理的异常，确保应用程序在发生错误时
     * 也能返回统一的错误格式，便于前端进行错误处理。
     *
     * @param e Exception异常对象
     * @return 包含错误信息的Result对象，状态码为400
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleException(Exception e) {
        return Result.fail(400, e.getMessage());
    }
}