package com.aixone.auth.service;

/**
 * RefreshToken服务接口
 */
public interface RefreshTokenService {
    /** 生成RefreshToken并存储到Redis */
    String createAndStore(String userId, String clientId, long expireSeconds);
    /** 校验RefreshToken有效性 */
    boolean validate(String refreshToken);
    /** 解析RefreshToken关联的userId */
    String getUserId(String refreshToken);
    /** 删除RefreshToken */
    void delete(String refreshToken);
} 