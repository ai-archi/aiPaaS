package com.aixone.metacenter.common.exception;

import com.aixone.common.exception.BaseException;

/**
 * 元数据服务异常基类
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
public class MetaException extends BaseException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     * 
     * @param errorCode 错误代码
     * @param message 错误消息
     */
    public MetaException(String errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * 构造函数
     * 
     * @param errorCode 错误代码
     * @param message 错误消息
     * @param cause 原因异常
     */
    public MetaException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    /**
     * 构造函数
     * 
     * @param code 错误码（数字）
     * @param errorCode 错误代码
     * @param message 错误消息
     */
    public MetaException(int code, String errorCode, String message) {
        super(code, errorCode, message);
    }

    /**
     * 构造函数
     * 
     * @param code 错误码（数字）
     * @param errorCode 错误代码
     * @param message 错误消息
     * @param cause 原因异常
     */
    public MetaException(int code, String errorCode, String message, Throwable cause) {
        super(code, errorCode, message, cause);
    }
} 