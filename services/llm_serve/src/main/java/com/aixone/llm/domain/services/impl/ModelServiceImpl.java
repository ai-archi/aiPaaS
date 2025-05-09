package com.aixone.llm.domain.services.impl;

import com.aixone.llm.domain.models.aggregates.model_config.ModelConfig;
import com.aixone.llm.domain.repositories.model.ModelInvokeRepository;
import com.aixone.llm.domain.repositories.model.ModelConfigRepository;
import com.aixone.llm.domain.services.ModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {
    private final ModelInvokeRepository modelInvokeRepository;
    private final ModelConfigRepository modelRepository;
    private static final int MONITORING_WINDOW_SECONDS = 60;

    
    @Override
    @Transactional
    public Mono<ModelConfig> createModel(ModelConfig modelConfig) {
        return modelRepository.save(modelConfig);
    }
    
    @Override
    @Transactional
    public Mono<ModelConfig> updateModel( ModelConfig modelConfig) {
        return modelRepository.save(modelConfig);
    }
    
    @Override
    public Mono<ModelConfig> getModel(String modelId) {
        return modelRepository.findById(modelId);
    }

    @Override
    public Mono<ModelConfig> getModelByName(String modelName) {
        return modelRepository.findByName(modelName);
    }
    
    @Override
    public Flux<ModelConfig> listModels() {
        return modelRepository.findAll();
    }
    
    @Override
    @Transactional
    public Mono<Void> deleteModel(String modelId) {
        return modelRepository.deleteById(modelId);
    }
    
    @Override
    @Transactional
    public Mono<ModelConfig> activateModel(String modelId) {
        return modelRepository.findById(modelId)
                .flatMap(config -> {
                    config.setActive(true);
                    return modelRepository.save(config);
                });
    }
    
    @Override
    @Transactional
    public Mono<ModelConfig> deactivateModel(String modelId) {
        return modelRepository.findById(modelId)
                .flatMap(config -> {
                    config.setActive(false);
                    return modelRepository.save(config);
                });
    }
    
    @Override
    public Mono<Boolean> validateModel(ModelConfig modelConfig) {
        boolean valid = modelConfig.getName() != null && !modelConfig.getName().isEmpty()
                && modelConfig.getEndpoint() != null && !modelConfig.getEndpoint().isEmpty()
                && modelConfig.getApiKey() != null && !modelConfig.getApiKey().isEmpty()
                && modelConfig.getMaxTokens() != null && modelConfig.getMaxTokens() > 0
                && modelConfig.getMinInputPrice() != null && modelConfig.getMinInputPrice().compareTo(java.math.BigDecimal.ZERO) >= 0
                && modelConfig.getMinOutputPrice() != null && modelConfig.getMinOutputPrice().compareTo(java.math.BigDecimal.ZERO) >= 0;
        return Mono.just(valid);
    }

    @Override
    public Mono<Long> getModelQPS(String modelId) {
        return modelInvokeRepository.getRecentQPS(modelId, MONITORING_WINDOW_SECONDS);
    }

    @Override
    public Mono<Long> getAverageLatency(String modelId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusMinutes(5);
        return modelInvokeRepository.calculateAverageLatency(modelId, start, now)
            .map(Double::longValue);
    }

    @Override
    public Mono<Double> getSuccessRate(String modelId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusMinutes(5);
        return modelInvokeRepository.calculateErrorRate(modelId, start, now)
            .map(errorRate -> 1 - errorRate);
    }

    @Override
    public Mono<Double> getModelCost(String modelId, String timeRange) {
        // 简单返回0.0
        return Mono.just(0.0);
    }

    @Override
    public Mono<Long> getTotalTokens(String modelId, String timeRange) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusHours(1); // 默认查询最近1小时
        return modelInvokeRepository.countTokensByModelId(modelId, start, now);
    }

    @Override
    public Flux<ModelConfig> getTopModels(String metric, int limit) {
        // 简单返回全部
        return modelRepository.findAll().take(limit);
    }
} 