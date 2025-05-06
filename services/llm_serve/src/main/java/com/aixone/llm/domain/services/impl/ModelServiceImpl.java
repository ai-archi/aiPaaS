package com.aixone.llm.domain.services.impl;

import com.aixone.llm.domain.models.aggregates.model_config.ModelConfig;
import com.aixone.llm.domain.repositories.assistant.ModelInvokeRepository;
import com.aixone.llm.domain.services.ModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {
    private final ModelInvokeRepository modelInvokeRepository;
    private static final int MONITORING_WINDOW_SECONDS = 60;
    
    private final Map<String, ModelConfig> modelStore = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);
    
    @Override
    @Transactional
    public Mono<ModelConfig> createModel(ModelConfig modelConfig) {
        String id = String.valueOf(idGen.getAndIncrement());
        modelConfig.setId(id);
        modelStore.put(id, modelConfig);
        return Mono.just(modelConfig);
    }
    
    @Override
    @Transactional
    public Mono<ModelConfig> updateModel(String modelId, ModelConfig modelConfig) {
        modelConfig.setId(modelId);
        modelStore.put(modelId, modelConfig);
        return Mono.just(modelConfig);
    }
    
    @Override
    public Mono<ModelConfig> getModel(String modelId) {
        ModelConfig config = modelStore.get(modelId);
        return config != null ? Mono.just(config) : Mono.empty();
    }
    
    @Override
    public Flux<ModelConfig> listModels() {
        return Flux.fromIterable(modelStore.values());
    }
    
    @Override
    @Transactional
    public Mono<Void> deleteModel(String modelId) {
        modelStore.remove(modelId);
        return Mono.empty();
    }
    
    @Override
    @Transactional
    public Mono<ModelConfig> activateModel(String modelId) {
        ModelConfig config = modelStore.get(modelId);
        if (config != null) {
            config.activate();
            modelStore.put(modelId, config);
            return Mono.just(config);
        }
        return Mono.empty();
    }
    
    @Override
    @Transactional
    public Mono<ModelConfig> deactivateModel(String modelId) {
        ModelConfig config = modelStore.get(modelId);
        if (config != null) {
            config.deactivate();
            modelStore.put(modelId, config);
            return Mono.just(config);
        }
        return Mono.empty();
    }
    
    @Override
    public Mono<Boolean> validateModel(ModelConfig modelConfig) {
        return Mono.just(modelConfig.isValid());
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
        return Flux.fromIterable(modelStore.values()).take(limit);
    }
} 