package com.aixone.common.exception;

/**
 * 资源未找到异常
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
public class NotFoundException extends BizException {
    
    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     * 
     * @param message 错误消息
     */
    public NotFoundException(String message) {
        super("RESOURCE_NOT_FOUND", message);
    }

    /**
     * 构造函数
     * 
     * @param message 错误消息
     * @param cause 原因异常
     */
    public NotFoundException(String message, Throwable cause) {
        super("RESOURCE_NOT_FOUND", message, cause);
    }

    /**
     * 构造函数
     * 
     * @param errorCode 错误代码
     * @param message 错误消息
     */
    public NotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * 构造函数
     * 
     * @param errorCode 错误代码
     * @param message 错误消息
     * @param cause 原因异常
     */
    public NotFoundException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
} 