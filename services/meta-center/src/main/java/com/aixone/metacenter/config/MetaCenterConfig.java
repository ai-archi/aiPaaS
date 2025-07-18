package com.aixone.metacenter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 元数据服务配置类
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Configuration
@EnableJpaAuditing
@EnableTransactionManagement
@EnableAsync
@EnableAspectJAutoProxy
public class MetaCenterConfig {

    // 配置内容将在后续添加
} 