package com.aixone.llm.domain.services;

import com.aixone.llm.domain.models.aggregates.model_config.ModelConfig;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ModelService {
    // 模型配置管理
    Mono<ModelConfig> createModel(ModelConfig modelConfig);
    Mono<ModelConfig> updateModel(String modelId, ModelConfig modelConfig);
    Mono<Void> deleteModel(String modelId);
    
    // 模型状态管理
    Mono<ModelConfig> activateModel(String modelId);
    Mono<ModelConfig> deactivateModel(String modelId);
    
    // 模型查询
    Mono<ModelConfig> getModel(String modelId);
    Flux<ModelConfig> listModels();
    
    // 模型验证
    Mono<Boolean> validateModel(ModelConfig modelConfig);
    
    // 模型监控
    Mono<Long> getModelQPS(String modelId);
    Mono<Long> getAverageLatency(String modelId);
    Mono<Double> getSuccessRate(String modelId);
    
    // 模型成本分析
    Mono<Double> getModelCost(String modelId, String timeRange);
    Mono<Long> getTotalTokens(String modelId, String timeRange);
    Flux<ModelConfig> getTopModels(String metric, int limit);
} 