package com.aixone.common.exception;

/**
 * 未授权异常
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
public class UnauthorizedException extends BizException {
    
    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     * 
     * @param message 错误消息
     */
    public UnauthorizedException(String message) {
        super("UNAUTHORIZED", message);
    }

    /**
     * 构造函数
     * 
     * @param message 错误消息
     * @param cause 原因异常
     */
    public UnauthorizedException(String message, Throwable cause) {
        super("UNAUTHORIZED", message, cause);
    }

    /**
     * 构造函数
     * 
     * @param errorCode 错误代码
     * @param message 错误消息
     */
    public UnauthorizedException(String errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * 构造函数
     * 
     * @param errorCode 错误代码
     * @param message 错误消息
     * @param cause 原因异常
     */
    public UnauthorizedException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
