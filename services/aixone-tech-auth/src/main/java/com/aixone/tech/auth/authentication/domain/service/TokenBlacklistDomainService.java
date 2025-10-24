package com.aixone.tech.auth.authentication.domain.service;

import com.aixone.tech.auth.authentication.domain.model.TokenBlacklist;
import com.aixone.tech.auth.authentication.domain.repository.TokenBlacklistRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 令牌黑名单领域服务
 */
@Service
public class TokenBlacklistDomainService {

    private final TokenBlacklistRepository tokenBlacklistRepository;

    public TokenBlacklistDomainService(TokenBlacklistRepository tokenBlacklistRepository) {
        this.tokenBlacklistRepository = tokenBlacklistRepository;
    }

    /**
     * 将令牌加入黑名单
     */
    public TokenBlacklist addToBlacklist(String token, String tenantId, String reason, LocalDateTime expiresAt) {
        TokenBlacklist tokenBlacklist = new TokenBlacklist(token, tenantId, expiresAt, reason);
        return tokenBlacklistRepository.save(tokenBlacklist);
    }

    /**
     * 检查令牌是否在黑名单中
     */
    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklistRepository.isTokenBlacklisted(token);
    }

    /**
     * 检查令牌是否在黑名单中（按租户）
     */
    public boolean isTokenBlacklistedByTenant(String token, String tenantId) {
        return tokenBlacklistRepository.isTokenBlacklistedByTenant(token, tenantId);
    }

    /**
     * 清理过期黑名单记录
     */
    public void cleanupExpiredTokens() {
        tokenBlacklistRepository.deleteExpiredTokens();
    }

    /**
     * 清理指定租户的过期黑名单记录
     */
    public void cleanupExpiredTokensByTenant(String tenantId) {
        tokenBlacklistRepository.deleteExpiredTokensByTenantId(tenantId);
    }

    /**
     * 将令牌加入黑名单（兼容测试代码）
     */
    public void blacklistToken(String token, String tenantId, LocalDateTime expiresAt) {
        addToBlacklist(token, tenantId, "Manual blacklist", expiresAt);
    }
}
