package com.aixone.tech.auth.authentication.domain.repository;

import com.aixone.tech.auth.authentication.domain.model.TokenBlacklist;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 令牌黑名单仓储接口
 */
public interface TokenBlacklistRepository {

    /**
     * 保存令牌到黑名单
     */
    TokenBlacklist save(TokenBlacklist tokenBlacklist);

    /**
     * 检查令牌是否在黑名单中
     */
    boolean isTokenBlacklisted(String token);

    /**
     * 根据令牌查找黑名单记录
     */
    Optional<TokenBlacklist> findByToken(String token);

    /**
     * 删除过期的黑名单记录
     */
    void deleteExpiredTokens();

    /**
     * 删除指定租户的过期黑名单记录
     */
    void deleteExpiredTokensByTenantId(String tenantId);

    /**
     * 删除指定时间之前的黑名单记录
     */
    void deleteByCreatedAtBefore(LocalDateTime timestamp);

    /**
     * 检查令牌是否在黑名单中（按租户）
     */
    boolean isTokenBlacklistedByTenant(String token, String tenantId);

    /**
     * 根据令牌、租户ID和过期时间查找黑名单记录
     */
    Optional<TokenBlacklist> findByTokenAndTenantIdAndExpiresAtAfter(String token, String tenantId, LocalDateTime now);

    /**
     * 删除指定时间之前的黑名单记录
     */
    void deleteByExpiresAtBefore(LocalDateTime timestamp);

    /**
     * 清理过期令牌
     */
    void cleanupExpiredTokens();
}
