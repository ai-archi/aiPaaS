package com.aixone.common.exception;

/**
 * 业务异常，适用于业务逻辑错误
 */
public class BizException extends BaseException {
    public BizException(int code, String message) {
        super(code, message);
    }
} 