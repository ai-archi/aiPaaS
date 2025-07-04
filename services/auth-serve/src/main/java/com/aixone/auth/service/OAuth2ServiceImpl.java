package com.aixone.auth.service;

import org.springframework.stereotype.Service;

/**
 * 第三方OAuth2服务实现
 * 预留真实API对接点，便于后续集成SDK或HTTP调用
 */
@Service
public class OAuth2ServiceImpl implements OAuth2Service {
    @Override
    public String getAlipayUserId(String code) {
        // TODO: 调用支付宝开放平台API，用code换取access_token，再获取用户ID
        // 示例：
        // 1. POST https://openapi.alipay.com/gateway.do
        // 2. 解析响应，获取alipay_user_id
        // 3. 返回alipay_user_id
        return "mock-alipay-user-id";
    }

    @Override
    public String getWechatUserId(String code) {
        // TODO: 调用微信开放平台API，用code换取access_token，再获取用户open_id
        // 示例：
        // 1. GET https://api.weixin.qq.com/sns/oauth2/access_token
        // 2. 解析响应，获取openid
        // 3. 返回openid
        return "mock-wechat-user-id";
    }

    @Override
    public String getAliyunUserId(String code) {
        // TODO: 调用阿里云开放平台API，用code换取access_token，再获取用户ID
        // 示例：
        // 1. POST https://oauth.aliyun.com/token
        // 2. 解析响应，获取user_id
        // 3. 返回user_id
        return "mock-aliyun-user-id";
    }
} 