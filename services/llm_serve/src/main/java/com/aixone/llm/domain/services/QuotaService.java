package com.aixone.llm.domain.services;

import com.aixone.llm.domain.models.values.quota.QuotaInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface QuotaService {
    // 配额分配
    Mono<QuotaInfo> allocateQuota(String userId, String modelId, Long tokenLimit, 
                                 Long requestLimit, LocalDateTime expiresAt, String quotaType);
    
    // 配额更新
    Mono<QuotaInfo> updateQuota(String userId, String modelId, Long tokenLimit, 
                               Long requestLimit, LocalDateTime expiresAt);
    
    // 配额撤销
    Mono<Void> revokeQuota(String userId, String modelId);
    
    // 配额消费
    Mono<QuotaInfo> consumeQuota(String userId, String modelId, Long tokens);
    
    // 配额检查和预留
    Mono<Boolean> checkAndReserveQuota(String userId, String modelId);
    
    // 配额查询
    Mono<QuotaInfo> getQuota(String userId, String modelId);
    Flux<QuotaInfo> listQuotasByUser(String userId);
    Flux<QuotaInfo> listQuotasByModel(String modelId);
    
    // 剩余额度查询
    Mono<Long> getRemainingTokens(String userId, String modelId);
    Mono<Long> getRemainingRequests(String userId, String modelId);
} 