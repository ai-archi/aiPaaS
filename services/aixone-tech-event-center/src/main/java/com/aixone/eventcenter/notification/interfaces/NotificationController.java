package com.aixone.eventcenter.notification.interfaces;

import com.aixone.common.api.ApiResponse;
import com.aixone.eventcenter.notification.application.NotificationApplicationService;
import com.aixone.eventcenter.notification.domain.Notification;
import com.aixone.common.session.SessionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * 通知接口控制器
 * /api/v1/notifications
 */
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    
    @Autowired
    private NotificationApplicationService notificationApplicationService;

    /**
     * 发送通知
     */
    @PostMapping
    public ApiResponse<Notification> sendNotification(@RequestBody SendNotificationRequest request) {
        String tenantId = SessionContext.getTenantId();
        if (tenantId == null) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        
        Notification notification = notificationApplicationService.sendNotification(
                tenantId,
                request.getNotificationType(),
                request.getRecipientInfo(),
                request.getNotificationContent(),
                request.getChannel(),
                request.getTemplateId(),
                request.getPriority()
        );
        
        return ApiResponse.success(notification);
    }

    /**
     * 使用模板发送通知
     */
    @PostMapping("/template")
    public ApiResponse<Notification> sendNotificationWithTemplate(@RequestBody SendNotificationWithTemplateRequest request) {
        String tenantId = SessionContext.getTenantId();
        if (tenantId == null) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        
        Notification notification = notificationApplicationService.sendNotificationWithTemplate(
                tenantId,
                request.getTemplateId(),
                request.getRecipientInfo(),
                request.getVariables()
        );
        
        return ApiResponse.success(notification);
    }

    /**
     * 获取当前租户的通知列表
     */
    @GetMapping
    public ApiResponse<List<Notification>> getNotifications(
            @RequestParam(required = false) String notificationType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        String tenantId = SessionContext.getTenantId();
        if (tenantId == null) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        
        List<Notification> notifications;
        if (notificationType != null && !notificationType.isEmpty()) {
            try {
                Notification.NotificationType type = Notification.NotificationType.valueOf(notificationType.toUpperCase());
                notifications = notificationApplicationService.getNotificationsByType(type, tenantId);
            } catch (IllegalArgumentException e) {
                return ApiResponse.error(40002, "无效的通知类型: " + notificationType);
            }
        } else if (startTime != null && endTime != null) {
            Instant start = Instant.parse(startTime);
            Instant end = Instant.parse(endTime);
            notifications = notificationApplicationService.getNotificationsByTimeRange(tenantId, start, end);
        } else {
            notifications = notificationApplicationService.getNotificationsByTenant(tenantId);
        }
        
        return ApiResponse.success(notifications);
    }

    /**
     * 获取通知详情
     */
    @GetMapping("/{notificationId}")
    public ApiResponse<Notification> getNotificationById(@PathVariable Long notificationId) {
        String tenantId = SessionContext.getTenantId();
        if (tenantId == null) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        
        return notificationApplicationService.getNotificationById(notificationId, tenantId)
                .map(ApiResponse::success)
                .orElseGet(() -> ApiResponse.error(40401, "通知不存在"));
    }

    /**
     * 发送通知请求DTO
     */
    public static class SendNotificationRequest {
        private Notification.NotificationType notificationType;
        private String recipientInfo;
        private String notificationContent;
        private Notification.NotificationChannel channel;
        private Long templateId;
        private Notification.NotificationPriority priority;

        // Getters and Setters
        public Notification.NotificationType getNotificationType() { return notificationType; }
        public void setNotificationType(Notification.NotificationType notificationType) { this.notificationType = notificationType; }
        public String getRecipientInfo() { return recipientInfo; }
        public void setRecipientInfo(String recipientInfo) { this.recipientInfo = recipientInfo; }
        public String getNotificationContent() { return notificationContent; }
        public void setNotificationContent(String notificationContent) { this.notificationContent = notificationContent; }
        public Notification.NotificationChannel getChannel() { return channel; }
        public void setChannel(Notification.NotificationChannel channel) { this.channel = channel; }
        public Long getTemplateId() { return templateId; }
        public void setTemplateId(Long templateId) { this.templateId = templateId; }
        public Notification.NotificationPriority getPriority() { return priority; }
        public void setPriority(Notification.NotificationPriority priority) { this.priority = priority; }
    }

    /**
     * 使用模板发送通知请求DTO
     */
    public static class SendNotificationWithTemplateRequest {
        private Long templateId;
        private String recipientInfo;
        private String variables;

        // Getters and Setters
        public Long getTemplateId() { return templateId; }
        public void setTemplateId(Long templateId) { this.templateId = templateId; }
        public String getRecipientInfo() { return recipientInfo; }
        public void setRecipientInfo(String recipientInfo) { this.recipientInfo = recipientInfo; }
        public String getVariables() { return variables; }
        public void setVariables(String variables) { this.variables = variables; }
    }
}

