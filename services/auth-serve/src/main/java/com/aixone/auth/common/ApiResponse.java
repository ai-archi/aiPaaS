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
    public static <T> ApiResponse<T> success(T data, MessageSource messageSource) {
        String msg = messageSource.getMessage("success", null, LocaleContextHolder.getLocale());
        return new ApiResponse<>(0, msg, data);
    }
    public static <T> ApiResponse<T> error(int code, String messageKey, MessageSource messageSource) {
        String msg = messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
        return new ApiResponse<>(code, msg, null);
    }
    // getter/setter 省略
} 