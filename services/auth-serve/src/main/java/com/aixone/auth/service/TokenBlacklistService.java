package com.aixone.auth.service;

/**
 * Token黑名单服务接口
 * 定义添加、校验、移除Token等方法
 */
public interface TokenBlacklistService {
    /** 添加Token到黑名单 */
    void addToBlacklist(String token);
    /** 校验Token是否在黑名单 */
    boolean isBlacklisted(String token);
    /** 移除Token（可选） */
    void removeFromBlacklist(String token);
} 