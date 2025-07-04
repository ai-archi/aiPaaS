package com.aixone.auth.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BizException.class)
    public ApiResponse<?> handleBizException(BizException ex) {
        // TODO: 国际化消息可通过MessageSource获取
        return ApiResponse.error(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleException(Exception ex) {
        // 生产环境可细化异常类型
        return ApiResponse.error(ErrorCode.INTERNAL_ERROR, ex.getMessage());
    }
} 