package com.aixone.llm.domain.repositories.model;

import com.aixone.llm.domain.models.aggregates.model_config.ModelConfig;
import com.aixone.llm.domain.repositories.Repository;

public interface ModelRepository extends Repository<ModelConfig, String> {
    // 可根据需要添加自定义方法
} 