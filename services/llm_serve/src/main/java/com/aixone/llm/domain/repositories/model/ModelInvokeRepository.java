package com.aixone.llm.domain.repositories.model;

import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import com.aixone.llm.domain.models.BaseModelResponse;

public interface ModelInvokeRepository {
    Mono<Void> saveInvokeRecord(String userId, String modelId, BaseModelResponse response, LocalDateTime invokeTime);
    Mono<Long> getUserTokenUsage(String userId, String modelId, LocalDateTime start, LocalDateTime end);
    Mono<Long> getRecentQPS(String modelId, int seconds);
    Mono<Long> getRecentLatency(String modelId, int seconds);
    Mono<Double> getRecentErrorRate(String modelId, int seconds);
    Mono<Long> countTokensByModelId(String modelId, LocalDateTime start, LocalDateTime end);
    Mono<Double> calculateAverageLatency(String modelId, LocalDateTime start, LocalDateTime end);
    Mono<Double> calculateErrorRate(String modelId, LocalDateTime start, LocalDateTime end);
} 