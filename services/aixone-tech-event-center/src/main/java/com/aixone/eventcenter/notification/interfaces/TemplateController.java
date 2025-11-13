package com.aixone.eventcenter.notification.interfaces;

import com.aixone.common.api.ApiResponse;
import com.aixone.eventcenter.notification.application.TemplateApplicationService;
import com.aixone.eventcenter.notification.domain.Notification;
import com.aixone.eventcenter.notification.domain.NotificationTemplate;
import com.aixone.common.session.SessionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * 通知模板接口控制器
 * /api/v1/notifications/templates
 */
@RestController
@RequestMapping("/api/v1/notifications/templates")
public class TemplateController {
    private static final Logger logger = LoggerFactory.getLogger(TemplateController.class);
    
    @Autowired
    private TemplateApplicationService templateApplicationService;

    /**
     * 创建通知模板
     */
    @PostMapping
    public ApiResponse<NotificationTemplate> createTemplate(@RequestBody CreateTemplateRequest request) {
        String tenantId = SessionContext.getTenantId();
        if (tenantId == null) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        
        NotificationTemplate template = templateApplicationService.createTemplate(
                tenantId,
                request.getTemplateName(),
                request.getNotificationType(),
                request.getSubjectTemplate(),
                request.getBodyTemplate(),
                request.getChannels(),
                request.getVariables()
        );
        
        return ApiResponse.success(template);
    }

    /**
     * 获取当前租户的模板列表
     */
    @GetMapping
    public ApiResponse<List<NotificationTemplate>> getTemplates(
            @RequestParam(required = false) String templateName,
            @RequestParam(required = false) String notificationType) {
        String tenantId = SessionContext.getTenantId();
        if (tenantId == null) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        
        List<NotificationTemplate> templates;
        if (templateName != null && !templateName.isEmpty()) {
            Optional<NotificationTemplate> templateOpt = templateApplicationService.getTemplateByName(templateName, tenantId);
            templates = templateOpt.map(List::of).orElse(List.of());
        } else if (notificationType != null && !notificationType.isEmpty()) {
            try {
                Notification.NotificationType type = Notification.NotificationType.valueOf(notificationType.toUpperCase());
                templates = templateApplicationService.getTemplatesByNotificationType(type, tenantId);
            } catch (IllegalArgumentException e) {
                return ApiResponse.error(40002, "无效的通知类型: " + notificationType);
            }
        } else {
            templates = templateApplicationService.getTemplatesByTenant(tenantId);
        }
        
        return ApiResponse.success(templates);
    }

    /**
     * 获取模板详情
     */
    @GetMapping("/{templateId}")
    public ApiResponse<NotificationTemplate> getTemplateById(@PathVariable Long templateId) {
        String tenantId = SessionContext.getTenantId();
        if (tenantId == null) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        
        return templateApplicationService.getTemplateById(templateId, tenantId)
                .map(ApiResponse::success)
                .orElseGet(() -> ApiResponse.error(40401, "模板不存在"));
    }

    /**
     * 更新通知模板
     */
    @PutMapping("/{templateId}")
    public ApiResponse<NotificationTemplate> updateTemplate(
            @PathVariable Long templateId,
            @RequestBody UpdateTemplateRequest request) {
        String tenantId = SessionContext.getTenantId();
        if (tenantId == null) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        
        NotificationTemplate template = templateApplicationService.updateTemplate(
                templateId,
                tenantId,
                request.getSubjectTemplate(),
                request.getBodyTemplate(),
                request.getChannels(),
                request.getVariables()
        );
        
        return ApiResponse.success(template);
    }

    /**
     * 删除通知模板
     */
    @DeleteMapping("/{templateId}")
    public ApiResponse<Void> deleteTemplate(@PathVariable Long templateId) {
        String tenantId = SessionContext.getTenantId();
        if (tenantId == null) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        
        templateApplicationService.deleteTemplate(templateId, tenantId);
        return ApiResponse.success(null);
    }

    /**
     * 渲染模板（预览）
     */
    @PostMapping("/{templateId}/render")
    public ApiResponse<String> renderTemplate(
            @PathVariable Long templateId,
            @RequestBody RenderTemplateRequest request) {
        String tenantId = SessionContext.getTenantId();
        if (tenantId == null) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        
        String rendered = templateApplicationService.renderTemplate(templateId, tenantId, request.getVariables());
        return ApiResponse.success(rendered);
    }

    /**
     * 创建模板请求DTO
     */
    public static class CreateTemplateRequest {
        private String templateName;
        private Notification.NotificationType notificationType;
        private String subjectTemplate;
        private String bodyTemplate;
        private String channels;
        private String variables;

        // Getters and Setters
        public String getTemplateName() { return templateName; }
        public void setTemplateName(String templateName) { this.templateName = templateName; }
        public Notification.NotificationType getNotificationType() { return notificationType; }
        public void setNotificationType(Notification.NotificationType notificationType) { this.notificationType = notificationType; }
        public String getSubjectTemplate() { return subjectTemplate; }
        public void setSubjectTemplate(String subjectTemplate) { this.subjectTemplate = subjectTemplate; }
        public String getBodyTemplate() { return bodyTemplate; }
        public void setBodyTemplate(String bodyTemplate) { this.bodyTemplate = bodyTemplate; }
        public String getChannels() { return channels; }
        public void setChannels(String channels) { this.channels = channels; }
        public String getVariables() { return variables; }
        public void setVariables(String variables) { this.variables = variables; }
    }

    /**
     * 更新模板请求DTO
     */
    public static class UpdateTemplateRequest {
        private String subjectTemplate;
        private String bodyTemplate;
        private String channels;
        private String variables;

        // Getters and Setters
        public String getSubjectTemplate() { return subjectTemplate; }
        public void setSubjectTemplate(String subjectTemplate) { this.subjectTemplate = subjectTemplate; }
        public String getBodyTemplate() { return bodyTemplate; }
        public void setBodyTemplate(String bodyTemplate) { this.bodyTemplate = bodyTemplate; }
        public String getChannels() { return channels; }
        public void setChannels(String channels) { this.channels = channels; }
        public String getVariables() { return variables; }
        public void setVariables(String variables) { this.variables = variables; }
    }

    /**
     * 渲染模板请求DTO
     */
    public static class RenderTemplateRequest {
        private String variables;

        // Getters and Setters
        public String getVariables() { return variables; }
        public void setVariables(String variables) { this.variables = variables; }
    }
}

