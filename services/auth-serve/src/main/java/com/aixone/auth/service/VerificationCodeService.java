package com.aixone.auth.service;

/**
 * 验证码服务接口
 * 定义验证码生成、校验、发送等方法
 */
public interface VerificationCodeService {
    /** 生成验证码 */
    String generateCode(String key);
    /** 校验验证码 */
    boolean validateCode(String key, String code);
    /** 发送短信验证码 */
    void sendSmsCode(String phone, String code);
    /** 发送邮箱验证码 */
    void sendEmailCode(String email, String code);
} 