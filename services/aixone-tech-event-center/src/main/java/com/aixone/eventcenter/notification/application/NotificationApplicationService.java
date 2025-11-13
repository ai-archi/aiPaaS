package com.aixone.eventcenter.notification.application;

import com.aixone.common.exception.BizException;
import com.aixone.common.util.ValidationUtils;
import com.aixone.eventcenter.notification.domain.Notification;
import com.aixone.eventcenter.notification.domain.NotificationRepository;
import com.aixone.eventcenter.notification.domain.NotificationTemplate;
import com.aixone.eventcenter.notification.domain.NotificationTemplateRepository;
import com.aixone.eventcenter.notification.infrastructure.NotificationSender;
import com.aixone.eventcenter.notification.infrastructure.TemplateEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * 通知应用服务
 * 协调领域对象完成通知业务用例
 */
@Service
@Transactional
public class NotificationApplicationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationApplicationService.class);
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private NotificationTemplateRepository templateRepository;
    
    @Autowired
    private com.aixone.eventcenter.notification.infrastructure.CompositeNotificationSender notificationSender;
    
    @Autowired
    private com.aixone.eventcenter.notification.infrastructure.TemplateEngine templateEngine;
    
    /**
     * 发送通知
     */
    public Notification sendNotification(String tenantId, Notification.NotificationType notificationType,
                                        String recipientInfo, String notificationContent,
                                        Notification.NotificationChannel channel, Long templateId,
                                        Notification.NotificationPriority priority) {
        ValidationUtils.notBlank(tenantId, "租户ID不能为空");
        ValidationUtils.notNull(notificationType, "通知类型不能为空");
        ValidationUtils.notBlank(recipientInfo, "接收者信息不能为空");
        ValidationUtils.notBlank(notificationContent, "通知内容不能为空");
        ValidationUtils.notNull(channel, "通知渠道不能为空");
        
        // 创建通知
        Notification notification = new Notification(tenantId, notificationType, recipientInfo, 
                                                     notificationContent, channel);
        if (templateId != null) {
            notification.setTemplateId(templateId);
        }
        if (priority != null) {
            notification.setPriority(priority);
        }
        
        // 保存通知
        Notification savedNotification = notificationRepository.save(notification);
        
        try {
            // 发送通知
            boolean success = notificationSender.send(savedNotification);
            
            if (success) {
                savedNotification.markAsSent();
            } else {
                savedNotification.markAsFailed("发送失败");
            }
            
            savedNotification = notificationRepository.save(savedNotification);
            
            logger.info("通知发送完成 - NotificationId: {}, Status: {}", 
                    savedNotification.getNotificationId(), savedNotification.getStatus());
        } catch (Exception e) {
            savedNotification.markAsFailed(e.getMessage());
            savedNotification = notificationRepository.save(savedNotification);
            logger.error("通知发送异常 - NotificationId: {}, Error: {}", 
                    savedNotification.getNotificationId(), e.getMessage(), e);
            throw new BizException("NOTIFICATION_SEND_FAILED", "通知发送失败: " + e.getMessage(), e);
        }
        
        return savedNotification;
    }
    
    /**
     * 使用模板发送通知
     */
    public Notification sendNotificationWithTemplate(String tenantId, Long templateId,
                                                     String recipientInfo, String variables) {
        ValidationUtils.notBlank(tenantId, "租户ID不能为空");
        ValidationUtils.notNull(templateId, "模板ID不能为空");
        ValidationUtils.notBlank(recipientInfo, "接收者信息不能为空");
        
        // 获取模板
        NotificationTemplate template = templateRepository.findByTemplateIdAndTenantId(templateId, tenantId)
                .orElseThrow(() -> new BizException("TEMPLATE_NOT_FOUND", "模板不存在"));
        
        // 渲染模板
        String renderedSubject = templateEngine.render(template.getSubjectTemplate(), variables);
        String renderedBody = templateEngine.render(template.getBodyTemplate(), variables);
        
        // 构建通知内容
        String notificationContent = String.format("{\"subject\":\"%s\",\"body\":\"%s\"}", 
                renderedSubject, renderedBody);
        
        // 确定渠道（从模板的channels中取第一个）
        Notification.NotificationChannel channel = Notification.NotificationChannel.EMAIL; // 默认
        if (template.getChannels() != null && !template.getChannels().isEmpty()) {
            String[] channelArray = template.getChannels().split(",");
            if (channelArray.length > 0) {
                try {
                    channel = Notification.NotificationChannel.valueOf(channelArray[0].trim());
                } catch (IllegalArgumentException e) {
                    logger.warn("无效的渠道: {}", channelArray[0]);
                }
            }
        }
        
        // 发送通知
        return sendNotification(tenantId, template.getNotificationType(), recipientInfo, 
                              notificationContent, channel, templateId, null);
    }
    
    /**
     * 查询租户的所有通知
     */
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByTenant(String tenantId) {
        ValidationUtils.notBlank(tenantId, "租户ID不能为空");
        return notificationRepository.findByTenantId(tenantId);
    }
    
    /**
     * 根据ID查询通知
     */
    @Transactional(readOnly = true)
    public Optional<Notification> getNotificationById(Long notificationId, String tenantId) {
        ValidationUtils.notNull(notificationId, "通知ID不能为空");
        ValidationUtils.notBlank(tenantId, "租户ID不能为空");
        return notificationRepository.findByNotificationIdAndTenantId(notificationId, tenantId);
    }
    
    /**
     * 根据通知类型查询通知
     */
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByType(Notification.NotificationType notificationType, String tenantId) {
        ValidationUtils.notNull(notificationType, "通知类型不能为空");
        ValidationUtils.notBlank(tenantId, "租户ID不能为空");
        return notificationRepository.findByTenantIdAndNotificationType(tenantId, notificationType);
    }
    
    /**
     * 根据时间范围查询通知
     */
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByTimeRange(String tenantId, Instant startTime, Instant endTime) {
        ValidationUtils.notBlank(tenantId, "租户ID不能为空");
        return notificationRepository.findByTenantIdAndCreatedAtBetween(tenantId, startTime, endTime);
    }
    
    /**
     * 统计租户通知数量
     */
    @Transactional(readOnly = true)
    public long getNotificationCountByTenant(String tenantId) {
        ValidationUtils.notBlank(tenantId, "租户ID不能为空");
        return notificationRepository.countByTenantId(tenantId);
    }
}

