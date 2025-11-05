package com.aixone.tech.auth.authentication.domain.repository;

import com.aixone.tech.auth.authentication.domain.model.Token;
import java.util.List;
import java.util.Optional;

/**
 * 令牌仓储接口
 */
public interface TokenRepository {
    
    /**
     * 根据令牌值查找令牌
     */
    Optional<Token> findByToken(String token);
    
    /**
     * 根据用户ID和租户ID查找所有令牌
     */
    List<Token> findByUserIdAndTenantId(String userId, String tenantId);
    
    /**
     * 根据客户端ID和租户ID查找所有令牌
     */
    List<Token> findByClientIdAndTenantId(String clientId, String tenantId);
    
    /**
     * 根据用户ID、客户端ID和租户ID查找令牌
     */
    List<Token> findByUserIdAndClientIdAndTenantId(String userId, String clientId, String tenantId);
    
    /**
     * 保存令牌
     */
    Token save(Token token);
    
    /**
     * 删除令牌
     */
    void delete(String token);
    
    /**
     * 删除用户的所有令牌
     */
    void deleteByUserIdAndTenantId(String userId, String tenantId);
    
    /**
     * 删除客户端的所有令牌
     */
    void deleteByClientIdAndTenantId(String clientId, String tenantId);
    
    /**
     * 检查令牌是否存在
     */
    boolean existsByToken(String token);
    
    /**
     * 查找过期的令牌
     */
    List<Token> findExpiredTokens();
    
    /**
     * 删除过期令牌
     */
    void deleteExpiredTokens();
    
    /**
     * 根据租户ID查找所有令牌
     */
    List<Token> findByTenantId(String tenantId);
}
