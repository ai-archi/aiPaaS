package com.aixone.llm.domain.repositories.quota;

import com.aixone.llm.domain.models.values.quota.QuotaInfo;
import com.aixone.llm.domain.models.values.quota.QuotaType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface QuotaRepository {
    Mono<QuotaInfo> save(QuotaInfo quotaInfo);
    Mono<QuotaInfo> findByUserIdAndModelId(String userId, String modelId);
    Flux<QuotaInfo> findByUserId(String userId);
    Flux<QuotaInfo> findByModelId(String modelId);
    Mono<Void> deleteByUserIdAndModelId(String userId, String modelId);
    Mono<QuotaInfo> incrementTokenUsage(String userId, String modelId, Long tokens);
    Mono<QuotaInfo> incrementRequestUsage(String userId, String modelId);
    Mono<Boolean> checkAndReserveQuota(String userId, String modelId, Long tokens);
    Mono<Long> getBalance(String userId, String modelId, QuotaType quotaType);
    Mono<Void> deductBalance(String userId, String modelId, QuotaType quotaType, long amount);
    Mono<Void> addBalance(String userId, String modelId, QuotaType quotaType, long amount);
} 