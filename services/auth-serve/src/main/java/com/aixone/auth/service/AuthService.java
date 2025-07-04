package com.aixone.auth.service;

import com.aixone.auth.user.User;

/**
 * 认证业务服务接口
 * 定义注册、登录、Token颁发与刷新等核心方法
 */
public interface AuthService {
    /** 用户注册 */
    Object register(Object registerRequest);
    /** 用户名密码登录 */
    Object login(String username, String password);
    /** 刷新Token */
    Object refreshToken(String refreshToken);
    // 可扩展：短信/邮箱/第三方登录等
} 