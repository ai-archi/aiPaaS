package com.aixone.eventcenter.audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * 审计日志服务
 */
@Service
public class AuditLogService {
    @Autowired
    private AuditLogRepository auditLogRepository;

    /**
     * 查询所有审计日志
     */
    public List<AuditLog> getAllLogs(String tenantId) {
        return auditLogRepository.findByTenantId(tenantId);
    }

    /**
     * 按ID查询日志
     */
    public Optional<AuditLog> getLogById(Long id, String tenantId) {
        return auditLogRepository.findByLogIdAndTenantId(id, tenantId);
    }
} 