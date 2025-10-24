package com.aixone.common.exception;

/**
 * 统一异常基类，所有自定义异常建议继承本类
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
public class BaseException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /** 错误代码 */
    private final String errorCode;
    
    /** 错误码（数字） */
    private final int code;

    /**
     * 构造函数
     * 
     * @param errorCode 错误代码
     * @param message 错误消息
     */
    public BaseException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.code = 50000; // 默认错误码
    }

    /**
     * 构造函数
     * 
     * @param errorCode 错误代码
     * @param message 错误消息
     * @param cause 原因异常
     */
    public BaseException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.code = 50000; // 默认错误码
    }

    /**
     * 构造函数
     * 
     * @param code 错误码（数字）
     * @param errorCode 错误代码
     * @param message 错误消息
     */
    public BaseException(int code, String errorCode, String message) {
        super(message);
        this.code = code;
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     * 
     * @param code 错误码（数字）
     * @param errorCode 错误代码
     * @param message 错误消息
     * @param cause 原因异常
     */
    public BaseException(int code, String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
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

    /**
     * 获取错误码（数字）
     * 
     * @return 错误码
     */
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
