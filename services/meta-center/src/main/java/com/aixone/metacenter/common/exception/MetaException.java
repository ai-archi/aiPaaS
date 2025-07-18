package com.aixone.metacenter.common.exception;

/**
 * 元数据服务异常基类
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
public class MetaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** 错误代码 */
    private final String errorCode;

    /**
     * 构造函数
     * 
     * @param errorCode 错误代码
     * @param message 错误消息
     */
    public MetaException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     * 
     * @param errorCode 错误代码
     * @param message 错误消息
     * @param cause 原因异常
     */
    public MetaException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * 获取错误代码
     * 
     * @return 错误代码
     */
    public String getErrorCode() {
        return errorCode;
    }
} 