package com.aixone.metacenter.common.exception;

/**
 * 元数据未找到异常
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
public class MetaNotFoundException extends MetaException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     * 
     * @param message 错误消息
     */
    public MetaNotFoundException(String message) {
        super("META_NOT_FOUND", message);
    }

    /**
     * 构造函数
     * 
     * @param message 错误消息
     * @param cause 原因异常
     */
    public MetaNotFoundException(String message, Throwable cause) {
        super("META_NOT_FOUND", message, cause);
    }
} 