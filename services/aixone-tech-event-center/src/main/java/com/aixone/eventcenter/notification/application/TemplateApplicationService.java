package com.aixone.eventcenter.notification.application;

import com.aixone.common.exception.BizException;
import com.aixone.common.util.ValidationUtils;
import com.aixone.eventcenter.notification.domain.Notification;
import com.aixone.eventcenter.notification.domain.NotificationTemplate;
import com.aixone.eventcenter.notification.domain.NotificationTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * 模板应用服务
 * 协调领域对象完成模板管理业务用例
 */
@Service
@Transactional
public class TemplateApplicationService {
    private static final Logger logger = LoggerFactory.getLogger(TemplateApplicationService.class);
    
    @Autowired
    private NotificationTemplateRepository templateRepository;
    
    /**
     * 创建模板
     */
    public NotificationTemplate createTemplate(String tenantId, String templateName,
                                              Notification.NotificationType notificationType,
                                              String subjectTemplate, String bodyTemplate,
                                              String channels, String variables) {
        ValidationUtils.notBlank(tenantId, "租户ID不能为空");
        ValidationUtils.notBlank(templateName, "模板名称不能为空");
        ValidationUtils.notNull(notificationType, "通知类型不能为空");
        ValidationUtils.notBlank(bodyTemplate, "模板内容不能为空");
        
        // 检查模板名称是否已存在
        if (templateRepository.findByTenantIdAndTemplateName(tenantId, templateName).isPresent()) {
            throw new BizException("TEMPLATE_NAME_EXISTS", "模板名称已存在: " + templateName);
        }
        
        NotificationTemplate template = new NotificationTemplate(tenantId, templateName, notificationType,
                                                                subjectTemplate, bodyTemplate, channels);
        if (variables != null) {
            template.setVariables(variables);
        }
        
        return templateRepository.save(template);
    }
    
    /**
     * 更新模板
     */
    public NotificationTemplate updateTemplate(Long templateId, String tenantId,
                                              String subjectTemplate, String bodyTemplate,
                                              String channels, String variables) {
        ValidationUtils.notNull(templateId, "模板ID不能为空");
        ValidationUtils.notBlank(tenantId, "租户ID不能为空");
        
        NotificationTemplate template = templateRepository.findByTemplateIdAndTenantId(templateId, tenantId)
                .orElseThrow(() -> new BizException("TEMPLATE_NOT_FOUND", "模板不存在"));
        
        if (subjectTemplate != null || bodyTemplate != null) {
            template.updateContent(subjectTemplate, bodyTemplate);
        }
        if (channels != null) {
            template.updateChannels(channels);
        }
        if (variables != null) {
            template.updateVariables(variables);
        }
        
        return templateRepository.save(template);
    }
    
    /**
     * 删除模板
     */
    public void deleteTemplate(Long templateId, String tenantId) {
        ValidationUtils.notNull(templateId, "模板ID不能为空");
        ValidationUtils.notBlank(tenantId, "租户ID不能为空");
        
        NotificationTemplate template = templateRepository.findByTemplateIdAndTenantId(templateId, tenantId)
                .orElseThrow(() -> new BizException("TEMPLATE_NOT_FOUND", "模板不存在"));
        
        templateRepository.delete(template);
    }
    
    /**
     * 查询租户的所有模板
     */
    @Transactional(readOnly = true)
    public List<NotificationTemplate> getTemplatesByTenant(String tenantId) {
        ValidationUtils.notBlank(tenantId, "租户ID不能为空");
        return templateRepository.findByTenantId(tenantId);
    }
    
    /**
     * 根据ID查询模板
     */
    @Transactional(readOnly = true)
    public Optional<NotificationTemplate> getTemplateById(Long templateId, String tenantId) {
        ValidationUtils.notNull(templateId, "模板ID不能为空");
        ValidationUtils.notBlank(tenantId, "租户ID不能为空");
        return templateRepository.findByTemplateIdAndTenantId(templateId, tenantId);
    }
    
    /**
     * 根据模板名称查询模板
     */
    @Transactional(readOnly = true)
    public Optional<NotificationTemplate> getTemplateByName(String templateName, String tenantId) {
        ValidationUtils.notBlank(templateName, "模板名称不能为空");
        ValidationUtils.notBlank(tenantId, "租户ID不能为空");
        return templateRepository.findByTenantIdAndTemplateName(tenantId, templateName);
    }
    
    /**
     * 根据通知类型查询模板
     */
    @Transactional(readOnly = true)
    public List<NotificationTemplate> getTemplatesByNotificationType(Notification.NotificationType notificationType, String tenantId) {
        ValidationUtils.notNull(notificationType, "通知类型不能为空");
        ValidationUtils.notBlank(tenantId, "租户ID不能为空");
        return templateRepository.findByTenantIdAndNotificationType(tenantId, notificationType);
    }
    
    /**
     * 渲染模板
     */
    @Transactional(readOnly = true)
    public String renderTemplate(Long templateId, String tenantId, String variables) {
        ValidationUtils.notNull(templateId, "模板ID不能为空");
        ValidationUtils.notBlank(tenantId, "租户ID不能为空");
        ValidationUtils.notBlank(variables, "变量不能为空");
        
        NotificationTemplate template = templateRepository.findByTemplateIdAndTenantId(templateId, tenantId)
                .orElseThrow(() -> new BizException("TEMPLATE_NOT_FOUND", "模板不存在"));
        
        // 使用模板引擎渲染
        com.aixone.eventcenter.notification.infrastructure.TemplateEngine templateEngine = 
                new com.aixone.eventcenter.notification.infrastructure.TemplateEngine();
        return templateEngine.render(template.getBodyTemplate(), variables);
    }
    
    /**
     * 统计租户模板数量
     */
    @Transactional(readOnly = true)
    public long getTemplateCountByTenant(String tenantId) {
        ValidationUtils.notBlank(tenantId, "租户ID不能为空");
        return templateRepository.countByTenantId(tenantId);
    }
}

