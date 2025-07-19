package com.aixone.common.exception;

/**
 * 禁止访问异常
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
public class ForbiddenException extends BizException {
    
    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     * 
     * @param message 错误消息
     */
    public ForbiddenException(String message) {
        super("FORBIDDEN", message);
    }

    /**
     * 构造函数
     * 
     * @param message 错误消息
     * @param cause 原因异常
     */
    public ForbiddenException(String message, Throwable cause) {
        super("FORBIDDEN", message, cause);
    }

    /**
     * 构造函数
     * 
     * @param errorCode 错误代码
     * @param message 错误消息
     */
    public ForbiddenException(String errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * 构造函数
     * 
     * @param errorCode 错误代码
     * @param message 错误消息
     * @param cause 原因异常
     */
    public ForbiddenException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
} 