package com.aixone.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现，基于Redis
 */
@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {
    private static final String PREFIX = "verify_code:";
    private static final long EXPIRE_SECONDS = 5 * 60; // 5分钟
    private final Random random = new Random();
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public String generateCode(String key) {
        String code = String.format("%06d", random.nextInt(1000000));
        String redisKey = PREFIX + key;
        redisTemplate.opsForValue().set(redisKey, code, EXPIRE_SECONDS, TimeUnit.SECONDS);
        return code;
    }

    @Override
    public boolean validateCode(String key, String code) {
        String redisKey = PREFIX + key;
        String realCode = redisTemplate.opsForValue().get(redisKey);
        return code != null && code.equals(realCode);
    }

    @Override
    public void sendSmsCode(String phone, String code) {
        // TODO: 调用短信网关发送验证码
        System.out.println("发送短信验证码到:" + phone + ", code=" + code);
    }

    @Override
    public void sendEmailCode(String email, String code) {
        // TODO: 调用邮件服务发送验证码
        System.out.println("发送邮箱验证码到:" + email + ", code=" + code);
    }
} 