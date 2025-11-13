package com.aixone.eventcenter.notification.infrastructure;

import com.aixone.eventcenter.notification.domain.Notification;
import com.aixone.eventcenter.notification.domain.NotificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * JPA通知仓储实现
 */
@Repository
public interface JpaNotificationRepository extends JpaRepository<Notification, Long>, NotificationRepository {
    
    @Override
    @Query("SELECT n FROM Notification n WHERE n.tenantId = :tenantId")
    List<Notification> findByTenantId(@Param("tenantId") String tenantId);
    
    @Override
    @Query("SELECT n FROM Notification n WHERE n.notificationId = :notificationId AND n.tenantId = :tenantId")
    java.util.Optional<Notification> findByNotificationIdAndTenantId(
            @Param("notificationId") Long notificationId, 
            @Param("tenantId") String tenantId);
    
    @Override
    @Query("SELECT n FROM Notification n WHERE n.notificationType = :notificationType")
    List<Notification> findByNotificationType(@Param("notificationType") Notification.NotificationType notificationType);
    
    @Override
    @Query("SELECT n FROM Notification n WHERE n.tenantId = :tenantId AND n.notificationType = :notificationType")
    List<Notification> findByTenantIdAndNotificationType(
            @Param("tenantId") String tenantId, 
            @Param("notificationType") Notification.NotificationType notificationType);
    
    @Override
    @Query("SELECT n FROM Notification n WHERE n.status = :status")
    List<Notification> findByStatus(@Param("status") Notification.NotificationStatus status);
    
    @Override
    @Query("SELECT n FROM Notification n WHERE n.tenantId = :tenantId AND n.status = :status")
    List<Notification> findByTenantIdAndStatus(
            @Param("tenantId") String tenantId, 
            @Param("status") Notification.NotificationStatus status);
    
    @Override
    @Query("SELECT n FROM Notification n WHERE n.createdAt BETWEEN :startTime AND :endTime")
    List<Notification> findByCreatedAtBetween(@Param("startTime") Instant startTime, @Param("endTime") Instant endTime);
    
    @Override
    @Query("SELECT n FROM Notification n WHERE n.tenantId = :tenantId AND n.createdAt BETWEEN :startTime AND :endTime")
    List<Notification> findByTenantIdAndCreatedAtBetween(
            @Param("tenantId") String tenantId, 
            @Param("startTime") Instant startTime, 
            @Param("endTime") Instant endTime);
    
    @Override
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.tenantId = :tenantId")
    long countByTenantId(@Param("tenantId") String tenantId);
}

