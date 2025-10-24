package com.aixone.session;

/**
 * 上下文相关异常
 */
public class SessionException extends RuntimeException {
    public SessionException(String message) {
        super(message);
    }
    public SessionException(String message, Throwable cause) {
        super(message, cause);
    }
} 