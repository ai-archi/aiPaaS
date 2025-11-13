package com.aixone.eventcenter.notification.interfaces;

import com.aixone.common.api.ApiResponse;
import com.aixone.eventcenter.notification.application.TemplateApplicationService;
import com.aixone.eventcenter.notification.domain.Notification;
import com.aixone.eventcenter.notification.domain.NotificationTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * 通知模板管理接口控制器（管理员接口，支持跨租户操作）
 * /api/v1/admin/notifications/templates
 */
@RestController
@RequestMapping("/api/v1/admin/notifications/templates")
public class TemplateAdminController {
    private static final Logger logger = LoggerFactory.getLogger(TemplateAdminController.class);
    
    @Autowired
    private TemplateApplicationService templateApplicationService;

    /**
     * 管理员查询模板列表（可跨租户）
     */
    @GetMapping
    public ApiResponse<List<NotificationTemplate>> getTemplates(
            @RequestParam(required = false) String tenantId,
            @RequestParam(required = false) String templateName) {
        
        if (!StringUtils.hasText(tenantId)) {
            return ApiResponse.error(40001, "tenantId参数不能为空");
        }
        
        logger.info("管理员查询模板列表: tenantId={}, templateName={}", tenantId, templateName);
        
        List<NotificationTemplate> templates;
        if (StringUtils.hasText(templateName)) {
            Optional<NotificationTemplate> templateOpt = templateApplicationService.getTemplateByName(templateName, tenantId);
            templates = templateOpt.map(List::of).orElse(List.of());
        } else {
            templates = templateApplicationService.getTemplatesByTenant(tenantId);
        }
        
        return ApiResponse.success(templates);
    }

    /**
     * 管理员查询模板详情（可跨租户）
     */
    @GetMapping("/{templateId}")
    public ApiResponse<NotificationTemplate> getTemplateById(
            @PathVariable Long templateId,
            @RequestParam(required = false) String tenantId) {
        
        if (!StringUtils.hasText(tenantId)) {
            return ApiResponse.error(40001, "tenantId参数不能为空");
        }
        
        logger.info("管理员查询模板详情: templateId={}, tenantId={}", templateId, tenantId);
        
        return templateApplicationService.getTemplateById(templateId, tenantId)
                .map(ApiResponse::success)
                .orElseGet(() -> ApiResponse.error(40401, "模板不存在"));
    }

    /**
     * 管理员删除模板
     */
    @DeleteMapping("/{templateId}")
    public ApiResponse<Void> deleteTemplate(
            @PathVariable Long templateId,
            @RequestParam(required = false) String tenantId) {
        
        if (!StringUtils.hasText(tenantId)) {
            return ApiResponse.error(40001, "tenantId参数不能为空");
        }
        
        logger.info("管理员删除模板: templateId={}, tenantId={}", templateId, tenantId);
        
        templateApplicationService.deleteTemplate(templateId, tenantId);
        return ApiResponse.success(null);
    }
}

