package com.aixone.llm.infrastructure.repository;

import com.aixone.llm.domain.repositories.assistant.ModelInvokeRepository;
import com.aixone.llm.domain.models.values.config.ModelResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public class ModelInvokeRepositoryImpl implements ModelInvokeRepository {

    private final ModelInvokeR2dbcRepository r2dbcRepository;

    @Autowired
    public ModelInvokeRepositoryImpl(ModelInvokeR2dbcRepository r2dbcRepository) {
        this.r2dbcRepository = r2dbcRepository;
    }

    @Override
    public Mono<Void> saveInvokeRecord(String userId, String modelId, ModelResponse response, LocalDateTime invokeTime) {
        // TODO: 实现保存逻辑，需将ModelResponse转为ModelInvokeEntity
        return Mono.empty();
    }

    @Override
    public Mono<Long> getUserTokenUsage(String userId, String modelId, LocalDateTime start, LocalDateTime end) {
        return r2dbcRepository.getUserTokenUsage(userId, modelId, start, end);
    }

    @Override
    public Mono<Long> getRecentQPS(String modelId, int seconds) {
        return r2dbcRepository.getRecentQPS(modelId, seconds);
    }

    @Override
    public Mono<Long> getRecentLatency(String modelId, int seconds) {
        return r2dbcRepository.getRecentLatency(modelId, seconds);
    }

    @Override
    public Mono<Double> getRecentErrorRate(String modelId, int seconds) {
        return r2dbcRepository.getRecentErrorRate(modelId, seconds);
    }

    @Override
    public Mono<Long> countTokensByModelId(String modelId, LocalDateTime start, LocalDateTime end) {
        return r2dbcRepository.sumTokensByModelId(modelId, start, end);
    }

    @Override
    public Mono<Double> calculateAverageLatency(String modelId, LocalDateTime start, LocalDateTime end) {
        return r2dbcRepository.calculateAverageLatency(modelId, start, end);
    }

    @Override
    public Mono<Double> calculateErrorRate(String modelId, LocalDateTime start, LocalDateTime end) {
        return r2dbcRepository.calculateErrorRate(modelId, start, end);
    }
} 