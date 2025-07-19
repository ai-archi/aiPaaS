package com.aixone.metacenter.common.exception;

import com.aixone.common.exception.ValidationException;

/**
 * 元数据校验异常
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
public class MetaValidationException extends ValidationException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     * 
     * @param message 错误消息
     */
    public MetaValidationException(String message) {
        super("META_VALIDATION_ERROR", message);
    }

    /**
     * 构造函数
     * 
     * @param message 错误消息
     * @param cause 原因异常
     */
    public MetaValidationException(String message, Throwable cause) {
        super("META_VALIDATION_ERROR", message, cause);
    }
} 