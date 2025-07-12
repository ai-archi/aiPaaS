package com.aixone.common.exception;

/**
 * 统一异常基类，所有自定义异常建议继承本类
 */
public class BaseException extends RuntimeException {
    private int code;
    private String message;

    public BaseException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    public int getCode() { return code; }
    @Override
    public String getMessage() { return message; }
} 