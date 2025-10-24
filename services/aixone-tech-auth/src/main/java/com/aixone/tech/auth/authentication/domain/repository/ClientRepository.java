package com.aixone.tech.auth.authentication.domain.repository;

import com.aixone.tech.auth.authentication.domain.model.Client;
import java.util.List;
import java.util.Optional;

/**
 * 客户端仓储接口
 */
public interface ClientRepository {
    
    /**
     * 根据客户端ID和租户ID查找客户端
     */
    Optional<Client> findByClientIdAndTenantId(String clientId, String tenantId);
    
    /**
     * 根据租户ID查找所有客户端
     */
    List<Client> findByTenantId(String tenantId);
    
    /**
     * 保存客户端
     */
    Client save(Client client);
    
    /**
     * 更新客户端
     */
    Client update(Client client);
    
    /**
     * 删除客户端
     */
    void delete(String clientId, String tenantId);
    
    /**
     * 检查客户端是否存在
     */
    boolean existsByClientIdAndTenantId(String clientId, String tenantId);
    
    /**
     * 根据客户端ID查找客户端（跨租户）
     */
    Optional<Client> findByClientId(String clientId);
}
