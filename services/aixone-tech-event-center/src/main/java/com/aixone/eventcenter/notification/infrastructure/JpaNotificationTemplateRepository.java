package com.aixone.eventcenter.notification.infrastructure;

import com.aixone.eventcenter.notification.domain.Notification;
import com.aixone.eventcenter.notification.domain.NotificationTemplate;
import com.aixone.eventcenter.notification.domain.NotificationTemplateRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA通知模板仓储实现
 */
@Repository
public interface JpaNotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long>, NotificationTemplateRepository {
    
    @Override
    @Query("SELECT t FROM NotificationTemplate t WHERE t.tenantId = :tenantId")
    List<NotificationTemplate> findByTenantId(@Param("tenantId") String tenantId);
    
    @Override
    @Query("SELECT t FROM NotificationTemplate t WHERE t.templateId = :templateId AND t.tenantId = :tenantId")
    java.util.Optional<NotificationTemplate> findByTemplateIdAndTenantId(
            @Param("templateId") Long templateId, 
            @Param("tenantId") String tenantId);
    
    @Override
    @Query("SELECT t FROM NotificationTemplate t WHERE t.templateName = :templateName")
    java.util.Optional<NotificationTemplate> findByTemplateName(@Param("templateName") String templateName);
    
    @Override
    @Query("SELECT t FROM NotificationTemplate t WHERE t.tenantId = :tenantId AND t.templateName = :templateName")
    java.util.Optional<NotificationTemplate> findByTenantIdAndTemplateName(
            @Param("tenantId") String tenantId, 
            @Param("templateName") String templateName);
    
    @Override
    @Query("SELECT t FROM NotificationTemplate t WHERE t.notificationType = :notificationType")
    List<NotificationTemplate> findByNotificationType(@Param("notificationType") Notification.NotificationType notificationType);
    
    @Override
    @Query("SELECT t FROM NotificationTemplate t WHERE t.tenantId = :tenantId AND t.notificationType = :notificationType")
    List<NotificationTemplate> findByTenantIdAndNotificationType(
            @Param("tenantId") String tenantId, 
            @Param("notificationType") Notification.NotificationType notificationType);
    
    @Override
    @Query("SELECT COUNT(t) FROM NotificationTemplate t WHERE t.tenantId = :tenantId")
    long countByTenantId(@Param("tenantId") String tenantId);
}

