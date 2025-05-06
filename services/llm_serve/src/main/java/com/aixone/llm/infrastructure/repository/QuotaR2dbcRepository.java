package com.aixone.llm.infrastructure.repository;

import com.aixone.llm.infrastructure.entity.QuotaEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface QuotaR2dbcRepository extends ReactiveCrudRepository<QuotaEntity, Long> {
    Mono<QuotaEntity> findByUserIdAndModelId(String userId, String modelId);
    Flux<QuotaEntity> findByUserId(String userId);
    Flux<QuotaEntity> findByModelId(String modelId);
    Mono<Void> deleteByUserIdAndModelId(String userId, String modelId);
    
    @Query("UPDATE quotas SET token_used = token_used + :tokens, updated_at = NOW() " +
           "WHERE user_id = :userId AND model_id = :modelId AND token_used + :tokens <= token_limit " +
           "AND expires_at > NOW()")
    Mono<Boolean> incrementTokenUsage(String userId, String modelId, Long tokens);
    
    @Query("UPDATE quotas SET request_used = request_used + 1, updated_at = NOW() " +
           "WHERE user_id = :userId AND model_id = :modelId AND request_used + 1 <= request_limit " +
           "AND expires_at > NOW()")
    Mono<Boolean> incrementRequestUsage(String userId, String modelId);
} 