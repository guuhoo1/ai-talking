package com.aitalking.dto;

/**
 * API响应封装类，用于统一API返回值格式。
 * 提供便捷的构造函数和链式调用风格。
 *
 * @author AI Talking
 * @date 2026-04-26
 */
public class ApiResponse<T> {
    /**
     * 状态码，200表示成功，其他值表示错误
     */
    private int code;

    /**
     * 响应描述信息
     */
    private String message;

    /**
     * 响应数据内容，类型由泛型T指定
     */
    private T data;

    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(T data) {
        this(200, "success", data);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}