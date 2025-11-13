package com.aixone.eventcenter.notification.interfaces;

import com.aixone.common.api.ApiResponse;
import com.aixone.eventcenter.notification.application.NotificationApplicationService;
import com.aixone.eventcenter.notification.domain.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * 通知管理接口控制器（管理员接口，支持跨租户操作）
 * /api/v1/admin/notifications
 */
@RestController
@RequestMapping("/api/v1/admin/notifications")
public class NotificationAdminController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationAdminController.class);
    
    @Autowired
    private NotificationApplicationService notificationApplicationService;

    /**
     * 管理员查询通知列表（可跨租户）
     */
    @GetMapping
    public ApiResponse<List<Notification>> getNotifications(
            @RequestParam(required = false) String tenantId,
            @RequestParam(required = false) String notificationType,
            @RequestParam(required = false) String status) {
        
        if (!StringUtils.hasText(tenantId)) {
            return ApiResponse.error(40001, "tenantId参数不能为空");
        }
        
        logger.info("管理员查询通知列表: tenantId={}, notificationType={}, status={}", 
                tenantId, notificationType, status);
        
        List<Notification> notifications;
        if (StringUtils.hasText(notificationType)) {
            try {
                Notification.NotificationType type = Notification.NotificationType.valueOf(notificationType.toUpperCase());
                notifications = notificationApplicationService.getNotificationsByType(type, tenantId);
            } catch (IllegalArgumentException e) {
                return ApiResponse.error(40002, "无效的通知类型: " + notificationType);
            }
        } else {
            notifications = notificationApplicationService.getNotificationsByTenant(tenantId);
        }
        
        return ApiResponse.success(notifications);
    }

    /**
     * 管理员查询通知详情（可跨租户）
     */
    @GetMapping("/{notificationId}")
    public ApiResponse<Notification> getNotificationById(
            @PathVariable Long notificationId,
            @RequestParam(required = false) String tenantId) {
        
        if (!StringUtils.hasText(tenantId)) {
            return ApiResponse.error(40001, "tenantId参数不能为空");
        }
        
        logger.info("管理员查询通知详情: notificationId={}, tenantId={}", notificationId, tenantId);
        
        return notificationApplicationService.getNotificationById(notificationId, tenantId)
                .map(ApiResponse::success)
                .orElseGet(() -> ApiResponse.error(40401, "通知不存在"));
    }
}

