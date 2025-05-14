package com.aixone.llm.domain.repositories.model;

import com.aixone.llm.domain.models.model.ModelConfig;
import com.aixone.llm.domain.repositories.Repository;

import reactor.core.publisher.Mono;

/**
 * 模型持久化仓储接口
 */
public interface ModelConfigRepository extends Repository<ModelConfig, String> {
    // 可根据需要添加自定义方法
    Mono<ModelConfig> findByName(String name);
} 