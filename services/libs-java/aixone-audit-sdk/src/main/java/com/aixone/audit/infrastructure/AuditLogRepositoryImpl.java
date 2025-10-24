package com.aixone.audit.infrastructure;

import com.aixone.audit.domain.AuditLog;
import com.aixone.audit.domain.AuditLogRepository;
import com.aixone.common.api.PageRequest;
import com.aixone.common.api.PageResult;
import com.aixone.session.SessionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 审计日志仓储实现
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Component
public class AuditLogRepositoryImpl implements AuditLogRepository {
    
    private final JpaAuditLogRepository jpaRepository;
    
    @Autowired
    public AuditLogRepositoryImpl(JpaAuditLogRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public AuditLog save(AuditLog entity) {
        AuditLogEntity entityToSave = convertToEntity(entity);
        AuditLogEntity savedEntity = jpaRepository.save(entityToSave);
        return convertToDomain(savedEntity);
    }
    
    @Override
    public java.util.Optional<AuditLog> findById(Long id) {
        return jpaRepository.findById(id).map(this::convertToDomain);
    }
    
    @Override
    public AuditLog getById(Long id) {
        return jpaRepository.findById(id).map(this::convertToDomain).orElse(null);
    }
    
    @Override
    public List<AuditLog> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::convertToDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AuditLog> findByTenantId(String tenantId) {
        return jpaRepository.findByConditions(null, null, null, null, null, tenantId, 
                Pageable.unpaged()).getContent().stream()
                .map(this::convertToDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public void delete(AuditLog entity) {
        AuditLogEntity entityToDelete = convertToEntity(entity);
        jpaRepository.delete(entityToDelete);
    }
    
    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
    
    @Override
    public long count() {
        return jpaRepository.count();
    }
    
    @Override
    public long countByTenantId(String tenantId) {
        return jpaRepository.findByConditions(null, null, null, null, null, tenantId, 
                Pageable.unpaged()).getTotalElements();
    }
    
    @Override
    public List<AuditLog> findByUserId(String userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(this::convertToDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AuditLog> findByAction(String action) {
        return jpaRepository.findByAction(action).stream()
                .map(this::convertToDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AuditLog> findByResult(String result) {
        return jpaRepository.findByResult(result).stream()
                .map(this::convertToDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AuditLog> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return jpaRepository.findByTimestampBetween(startTime, endTime).stream()
                .map(this::convertToDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AuditLog> findByUserIdAndTimestampBetween(String userId, LocalDateTime startTime, LocalDateTime endTime) {
        return jpaRepository.findByUserIdAndTimestampBetween(userId, startTime, endTime).stream()
                .map(this::convertToDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AuditLog> findByActionAndTimestampBetween(String action, LocalDateTime startTime, LocalDateTime endTime) {
        return jpaRepository.findByActionAndTimestampBetween(action, startTime, endTime).stream()
                .map(this::convertToDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public PageResult<AuditLog> findPage(com.aixone.common.api.PageRequest pageRequest) {
        Pageable pageable = createPageable(pageRequest);
        String tenantId = getCurrentTenantId();
        
        Page<AuditLogEntity> page = jpaRepository.findByConditions(null, null, null, null, null, tenantId, pageable);
        return convertToPageResult(page, pageRequest);
    }
    
    @Override
    public PageResult<AuditLog> findByUserIdPage(String userId, com.aixone.common.api.PageRequest pageRequest) {
        Pageable pageable = createPageable(pageRequest);
        Page<AuditLogEntity> page = jpaRepository.findByUserId(userId, pageable);
        return convertToPageResult(page, pageRequest);
    }
    
    @Override
    public PageResult<AuditLog> findByActionPage(String action, com.aixone.common.api.PageRequest pageRequest) {
        Pageable pageable = createPageable(pageRequest);
        Page<AuditLogEntity> page = jpaRepository.findByAction(action, pageable);
        return convertToPageResult(page, pageRequest);
    }
    
    @Override
    public PageResult<AuditLog> findByTimestampBetweenPage(LocalDateTime startTime, LocalDateTime endTime, com.aixone.common.api.PageRequest pageRequest) {
        Pageable pageable = createPageable(pageRequest);
        Page<AuditLogEntity> page = jpaRepository.findByTimestampBetween(startTime, endTime, pageable);
        return convertToPageResult(page, pageRequest);
    }
    
    @Override
    public PageResult<AuditLog> findByConditionsPage(String userId, String action, String result, 
                                                    LocalDateTime startTime, LocalDateTime endTime, 
                                                    com.aixone.common.api.PageRequest pageRequest) {
        Pageable pageable = createPageable(pageRequest);
        String tenantId = getCurrentTenantId();
        
        Page<AuditLogEntity> page = jpaRepository.findByConditions(userId, action, result, startTime, endTime, tenantId, pageable);
        return convertToPageResult(page, pageRequest);
    }
    
    /**
     * 创建分页对象
     */
    private Pageable createPageable(com.aixone.common.api.PageRequest pageRequest) {
        Sort sort = Sort.unsorted();
        if (pageRequest.getSortBy() != null && !pageRequest.getSortBy().isEmpty()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(pageRequest.getSortDirection()) 
                ? Sort.Direction.DESC : Sort.Direction.ASC;
            sort = Sort.by(direction, pageRequest.getSortBy());
        }
        
        return org.springframework.data.domain.PageRequest.of(pageRequest.getPageNum() - 1, pageRequest.getPageSize(), sort);
    }
    
    /**
     * 转换为分页结果
     */
    private PageResult<AuditLog> convertToPageResult(Page<AuditLogEntity> page, com.aixone.common.api.PageRequest pageRequest) {
        List<AuditLog> domainList = page.getContent().stream()
                .map(this::convertToDomain)
                .collect(Collectors.toList());
        
        return new PageResult<>(
            page.getTotalElements(),
            pageRequest.getPageNum(),
            pageRequest.getPageSize(),
            domainList
        );
    }
    
    /**
     * 将领域对象转换为JPA实体
     */
    private AuditLogEntity convertToEntity(AuditLog domain) {
        AuditLogEntity entity = new AuditLogEntity();
        entity.setId(domain.getId());
        entity.setTenantId(domain.getTenantId());
        entity.setUserId(domain.getUserId());
        entity.setAction(domain.getAction());
        entity.setResource(domain.getResource());
        entity.setResult(domain.getResult());
        entity.setTimestamp(domain.getTimestamp());
        entity.setClientIp(domain.getClientIp());
        entity.setUserAgent(domain.getUserAgent());
        entity.setDetails(domain.getDetails());
        entity.setErrorMessage(domain.getErrorMessage());
        entity.setSessionId(domain.getSessionId());
        return entity;
    }
    
    /**
     * 将JPA实体转换为领域对象
     */
    private AuditLog convertToDomain(AuditLogEntity entity) {
        AuditLog domain = new AuditLog(
            entity.getId(),
            entity.getTenantId(),
            entity.getUserId(),
            entity.getAction(),
            entity.getResource(),
            entity.getResult(),
            entity.getTimestamp(),
            entity.getClientIp(),
            entity.getUserAgent(),
            entity.getDetails(),
            entity.getErrorMessage(),
            entity.getSessionId()
        );
        return domain;
    }
    
    /**
     * 获取当前租户ID
     */
    private String getCurrentTenantId() {
        try {
            return SessionContext.getTenantId();
        } catch (Exception e) {
            return null;
        }
    }
}
