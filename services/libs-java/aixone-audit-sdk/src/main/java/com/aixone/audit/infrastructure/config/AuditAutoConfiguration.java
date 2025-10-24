package com.aixone.audit.infrastructure.config;

import com.aixone.audit.application.AuditService;
import com.aixone.audit.application.AuditQueryService;
import com.aixone.audit.infrastructure.AuditLogRepositoryImpl;
import com.aixone.audit.infrastructure.AuditEventPublisher;
import com.aixone.audit.infrastructure.JpaAuditLogRepository;
import com.aixone.audit.domain.AuditLogRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * 审计模块自动配置类
 * 提供审计相关的Bean配置
 */
@Configuration
@ConditionalOnClass(AuditService.class)
public class AuditAutoConfiguration {

    /**
     * 配置审计事件发布器
     */
    @Bean
    @ConditionalOnMissingBean
    public AuditEventPublisher auditEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new AuditEventPublisher(applicationEventPublisher);
    }

    /**
     * 配置审计日志仓库实现
     */
    @Bean
    @ConditionalOnMissingBean
    public AuditLogRepository auditLogRepository(JpaAuditLogRepository jpaAuditLogRepository) {
        return new AuditLogRepositoryImpl(jpaAuditLogRepository);
    }

    /**
     * 配置审计服务
     */
    @Bean
    @ConditionalOnMissingBean
    public AuditService auditService(AuditLogRepository auditLogRepository, AuditEventPublisher auditEventPublisher) {
        return new AuditService(auditLogRepository, auditEventPublisher);
    }

    /**
     * 配置审计查询服务
     */
    @Bean
    @ConditionalOnMissingBean
    public AuditQueryService auditQueryService(AuditLogRepository auditLogRepository) {
        return new AuditQueryService(auditLogRepository);
    }
}
