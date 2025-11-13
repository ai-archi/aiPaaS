package com.aixone.eventcenter.notification.domain;

import com.aixone.common.ddd.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 通知模板仓储接口
 * 定义通知模板聚合的持久化操作
 */
public interface NotificationTemplateRepository extends Repository<NotificationTemplate, Long> {
    
    /**
     * 根据租户ID查找模板
     */
    List<NotificationTemplate> findByTenantId(String tenantId);
    
    /**
     * 根据模板ID和租户ID查找模板
     */
    Optional<NotificationTemplate> findByTemplateIdAndTenantId(Long templateId, String tenantId);
    
    /**
     * 根据模板名称查找模板
     */
    Optional<NotificationTemplate> findByTemplateName(String templateName);
    
    /**
     * 根据租户ID和模板名称查找模板
     */
    Optional<NotificationTemplate> findByTenantIdAndTemplateName(String tenantId, String templateName);
    
    /**
     * 根据通知类型查找模板
     */
    List<NotificationTemplate> findByNotificationType(Notification.NotificationType notificationType);
    
    /**
     * 根据租户ID和通知类型查找模板
     */
    List<NotificationTemplate> findByTenantIdAndNotificationType(String tenantId, Notification.NotificationType notificationType);
    
    /**
     * 统计租户的模板数量
     */
    long countByTenantId(String tenantId);
}

