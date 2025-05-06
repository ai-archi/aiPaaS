package com.aixone.llm.application.query.quota;

import com.aixone.llm.domain.models.values.quota.QuotaInfo;
import com.aixone.llm.domain.services.QuotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class QuotaQueryHandler {
    private final QuotaService quotaService;

    public Mono<QuotaInfo> handleGetQuota(String userId, String modelId) {
        return quotaService.getQuota(userId, modelId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Quota not found")));
    }

    public Flux<QuotaInfo> handleListQuotasByUser(String userId) {
        return quotaService.listQuotasByUser(userId);
    }

    public Flux<QuotaInfo> handleListQuotasByModel(String modelId) {
        return quotaService.listQuotasByModel(modelId);
    }

    public Mono<Long> handleGetRemainingTokens(String userId, String modelId) {
        return quotaService.getRemainingTokens(userId, modelId);
    }

    public Mono<Long> handleGetRemainingRequests(String userId, String modelId) {
        return quotaService.getRemainingRequests(userId, modelId);
    }
} 