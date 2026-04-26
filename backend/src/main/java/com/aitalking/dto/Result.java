package com.aitalking.dto;

/**
 * 通用结果封装类，用于统一API返回值格式。
 * 采用泛型设计，可封装任意类型的数据。
 *
 * @author AI Talking
 * @date 2026-04-26
 */
public class Result<T> {
    /**
     * 状态码，200表示成功，其他值表示错误
     */
    private int code;

    /**
     * 结果描述信息，成功时通常为"success"，错误时为具体的错误信息
     */
    private String message;

    /**
     * 返回的数据内容，类型由泛型T指定
     */
    private T data;

    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(400, message, null);
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
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