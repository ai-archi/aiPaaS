package com.aixone.eventcenter.audit;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 审计日志仓库
 */
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    // 可扩展自定义查询
    java.util.List<AuditLog> findByTenantId(String tenantId);
    java.util.Optional<AuditLog> findByLogIdAndTenantId(Long logId, String tenantId);
} 