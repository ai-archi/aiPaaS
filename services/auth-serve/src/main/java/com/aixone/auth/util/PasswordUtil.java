package com.aixone.auth.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码加密工具类
 * 提供BCrypt加密与校验方法
 */
public class PasswordUtil {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /** 密码加密 */
    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    /** 密码校验 */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
} 