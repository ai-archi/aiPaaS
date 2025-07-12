package com.aixone.common.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，统一处理自定义异常和系统异常
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BaseException.class)
    public ErrorResponse handleBaseException(BaseException ex) {
        return new ErrorResponse(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception ex) {
        return new ErrorResponse(50001, ex.getMessage() != null ? ex.getMessage() : "系统异常");
    }

    /**
     * 错误响应体
     */
    public static class ErrorResponse {
        private int code;
        private String message;
        public ErrorResponse(int code, String message) {
            this.code = code;
            this.message = message;
        }
        public int getCode() { return code; }
        public String getMessage() { return message; }
    }
} 