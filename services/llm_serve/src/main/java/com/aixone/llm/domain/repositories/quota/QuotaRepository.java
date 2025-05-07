package com.aixone.llm.domain.repositories.quota;

import com.aixone.llm.domain.models.values.quota.QuotaInfo;
import com.aixone.llm.domain.models.values.quota.QuotaType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

public interface QuotaRepository {
    // 用户-模型级
    Mono<QuotaInfo> save(QuotaInfo quotaInfo);
    Mono<QuotaInfo> findByUserIdAndModelId(String userId, String modelId);
    Flux<QuotaInfo> findByUserId(String userId);
    Flux<QuotaInfo> findByModelId(String modelId);
    Mono<Void> deleteByUserIdAndModelId(String userId, String modelId);

    // 策略级
    Flux<QuotaInfo> findByPolicyId(String policyId);

    // 用量相关
    Mono<QuotaInfo> updateUsage(String userId, String modelId, long usage);
    Mono<QuotaInfo> resetUsage(String userId, String modelId);
    Mono<QuotaInfo> incrementTokenUsage(String userId, String modelId, Long tokens);
    Mono<QuotaInfo> incrementRequestUsage(String userId, String modelId);

    // 余额相关
    Mono<Boolean> checkAndReserveQuota(String userId, String modelId, Long tokens);
    Mono<Long> getBalance(String userId, String modelId, QuotaType quotaType);
    Mono<Void> deductBalance(String userId, String modelId, QuotaType quotaType, long amount);
    Mono<Void> addBalance(String userId, String modelId, QuotaType quotaType, long amount);

    // 统计与告警
    Flux<QuotaInfo> findExceededQuotas(double threshold);
    Flux<QuotaInfo> findExpiringSoon(int daysThreshold);
} 