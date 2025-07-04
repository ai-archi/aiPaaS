package com.aixone.auth.service;

/**
 * 第三方OAuth2服务接口
 * 封装支付宝、微信、阿里云等OAuth2流程
 */
public interface OAuth2Service {
    /**
     * 用code换取access_token，并获取支付宝用户ID
     */
    String getAlipayUserId(String code);

    /**
     * 用code换取access_token，并获取微信用户ID
     */
    String getWechatUserId(String code);

    /**
     * 用code换取access_token，并获取阿里云用户ID
     */
    String getAliyunUserId(String code);
} 