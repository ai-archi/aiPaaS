package com.aixone.auth.common;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 统一API返回结构
 */
public class ApiResponse<T> {
    /** 状态码 */
    private int code;
    /** 提示信息 */
    private String message;
    /** 返回数据 */
    private T data;

    public ApiResponse() {}
    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(0, "success", data);
    }
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
    // getter/setter 省略
} 