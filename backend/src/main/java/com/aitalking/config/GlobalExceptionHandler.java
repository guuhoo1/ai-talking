package com.aitalking.config;

import cn.dev33.satoken.exception.NotLoginException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<?> handleNotLoginException(NotLoginException e) {
        // 返回401状态码和错误信息，告诉前端需要重新登录
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("token 无效或已过期，请重新登录");
    }
}
