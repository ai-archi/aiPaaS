package com.aixone.llm.infrastructure.repository;

import com.aixone.llm.infrastructure.entity.ModelInvokeEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface ModelInvokeR2dbcRepository extends ReactiveCrudRepository<ModelInvokeEntity, Long> {
    
    @Query("SELECT COUNT(*) FROM model_invokes WHERE model_id = :modelId " +
           "AND invoke_time BETWEEN :start AND :end")
    Mono<Long> countInvokesByModelId(String modelId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT SUM(used_tokens) FROM model_invokes WHERE model_id = :modelId " +
           "AND invoke_time BETWEEN :start AND :end")
    Mono<Long> sumTokensByModelId(String modelId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT AVG(total_time) FROM model_invokes WHERE model_id = :modelId " +
           "AND invoke_time BETWEEN :start AND :end")
    Mono<Double> calculateAverageLatency(String modelId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT CAST(SUM(CASE WHEN is_error THEN 1 ELSE 0 END) AS FLOAT) / COUNT(*) " +
           "FROM model_invokes WHERE model_id = :modelId " +
           "AND invoke_time BETWEEN :start AND :end")
    Mono<Double> calculateErrorRate(String modelId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT SUM(used_tokens) FROM model_invokes WHERE user_id = :userId " +
           "AND model_id = :modelId AND invoke_time BETWEEN :start AND :end")
    Mono<Long> getUserTokenUsage(String userId, String modelId, LocalDateTime start, LocalDateTime end);
    
    Flux<ModelInvokeEntity> findByUserIdAndModelIdAndInvokeTimeBetween(
        String userId, String modelId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(*) FROM model_invokes WHERE model_id = :modelId " +
           "AND invoke_time >= NOW() - INTERVAL ':seconds SECONDS'")
    Mono<Long> getRecentQPS(String modelId, int seconds);
    
    @Query("SELECT AVG(total_time) FROM model_invokes WHERE model_id = :modelId " +
           "AND invoke_time >= NOW() - INTERVAL ':seconds SECONDS'")
    Mono<Long> getRecentLatency(String modelId, int seconds);
    
    @Query("SELECT CAST(SUM(CASE WHEN is_error THEN 1 ELSE 0 END) AS FLOAT) / COUNT(*) " +
           "FROM model_invokes WHERE model_id = :modelId " +
           "AND invoke_time >= NOW() - INTERVAL ':seconds SECONDS'")
    Mono<Double> getRecentErrorRate(String modelId, int seconds);
} 