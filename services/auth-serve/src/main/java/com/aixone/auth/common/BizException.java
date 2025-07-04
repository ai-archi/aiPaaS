package com.aixone.auth.common;

/**
 * 业务异常，支持错误码和国际化消息
 */
public class BizException extends RuntimeException {
    private final int code;
    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }
    public int getCode() {
        return code;
    }
} 