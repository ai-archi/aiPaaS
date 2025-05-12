package com.aixone.llm.application.quota;

import org.springframework.stereotype.Service;

import com.aixone.llm.domain.models.values.quota.QuotaInfo;
import com.aixone.llm.domain.services.QuotaService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class QuotaCommandHandler {
    private final QuotaService quotaService;

    public Mono<QuotaInfo> handleAllocateQuota(AllocateQuotaCommand command) {
        return quotaService.allocateQuota(
            command.getUserId(),
            command.getModelId(),
            command.getTokenLimit(),
            command.getRequestLimit(),
            command.getExpiresAt(),
            command.getQuotaType()
        );
    }

    public Mono<QuotaInfo> handleUpdateQuota(String userId, String modelId, UpdateQuotaCommand command) {
        return quotaService.updateQuota(
            userId,
            modelId,
            command.getTokenLimit(),
            command.getRequestLimit(),
            command.getExpiresAt()
        );
    }

    public Mono<Void> handleRevokeQuota(String userId, String modelId) {
        return quotaService.revokeQuota(userId, modelId);
    }

    public Mono<QuotaInfo> handleConsumeQuota(String userId, String modelId, Long tokens) {
        return quotaService.consumeQuota(userId, modelId, tokens);
    }
} 