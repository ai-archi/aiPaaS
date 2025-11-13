package com.aixone.eventcenter.notification.domain;

import com.aixone.common.ddd.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * 通知仓储接口
 * 定义通知聚合的持久化操作
 */
public interface NotificationRepository extends Repository<Notification, Long> {
    
    /**
     * 根据租户ID查找通知
     */
    List<Notification> findByTenantId(String tenantId);
    
    /**
     * 根据通知ID和租户ID查找通知
     */
    Optional<Notification> findByNotificationIdAndTenantId(Long notificationId, String tenantId);
    
    /**
     * 根据通知类型查找通知
     */
    List<Notification> findByNotificationType(Notification.NotificationType notificationType);
    
    /**
     * 根据租户ID和通知类型查找通知
     */
    List<Notification> findByTenantIdAndNotificationType(String tenantId, Notification.NotificationType notificationType);
    
    /**
     * 根据状态查找通知
     */
    List<Notification> findByStatus(Notification.NotificationStatus status);
    
    /**
     * 根据租户ID和状态查找通知
     */
    List<Notification> findByTenantIdAndStatus(String tenantId, Notification.NotificationStatus status);
    
    /**
     * 根据时间范围查找通知
     */
    List<Notification> findByCreatedAtBetween(Instant startTime, Instant endTime);
    
    /**
     * 根据租户ID和时间范围查找通知
     */
    List<Notification> findByTenantIdAndCreatedAtBetween(String tenantId, Instant startTime, Instant endTime);
    
    /**
     * 统计租户的通知数量
     */
    long countByTenantId(String tenantId);
}

