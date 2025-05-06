package com.aixone.llm.domain.services.impl;

import com.aixone.llm.domain.models.values.config.BillingRule;
import com.aixone.llm.domain.models.values.quota.QuotaType;
import com.aixone.llm.domain.models.values.quota.QuotaInfo;
import com.aixone.llm.domain.repositories.quota.QuotaRepository;
import com.aixone.llm.domain.services.QuotaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuotaServiceImpl implements QuotaService {
    private final QuotaRepository quotaRepository;
    
    @Override
    public Mono<QuotaInfo> allocateQuota(String userId, String modelId, Long tokenLimit,
                                        Long requestLimit, LocalDateTime expiresAt, String quotaType) {
        QuotaInfo quotaInfo = QuotaInfo.builder()
            .userId(userId)
            .modelId(modelId)
            .tokenLimit(tokenLimit)
            .tokenUsed(0L)
            .requestLimit(requestLimit)
            .requestUsed(0L)
            .expiresAt(expiresAt)
            .quotaType(quotaType)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        return quotaRepository.save(quotaInfo);
    }
    
    @Override
    public Mono<QuotaInfo> updateQuota(String userId, String modelId, Long tokenLimit,
                                      Long requestLimit, LocalDateTime expiresAt) {
        return quotaRepository.findByUserIdAndModelId(userId, modelId)
            .map(quota -> {
                quota.setTokenLimit(tokenLimit);
                quota.setRequestLimit(requestLimit);
                quota.setExpiresAt(expiresAt);
                quota.setUpdatedAt(LocalDateTime.now());
                return quota;
            })
            .flatMap(quotaRepository::save);
    }
    
    @Override
    public Mono<Void> revokeQuota(String userId, String modelId) {
        return quotaRepository.deleteByUserIdAndModelId(userId, modelId);
    }
    
    @Override
    public Mono<QuotaInfo> consumeQuota(String userId, String modelId, Long tokens) {
        return quotaRepository.incrementTokenUsage(userId, modelId, tokens)
            .flatMap(quota -> quotaRepository.incrementRequestUsage(userId, modelId));
    }
    
    @Override
    public Mono<Boolean> checkAndReserveQuota(String userId, String modelId) {
        return quotaRepository.findByUserIdAndModelId(userId, modelId)
            .filter(QuotaInfo::isValid)
            .filter(QuotaInfo::hasTokenQuota)
            .filter(QuotaInfo::hasRequestQuota)
            .map(quota -> true)
            .defaultIfEmpty(false);
    }
    
    @Override
    public Mono<QuotaInfo> getQuota(String userId, String modelId) {
        return quotaRepository.findByUserIdAndModelId(userId, modelId);
    }
    
    @Override
    public Flux<QuotaInfo> listQuotasByUser(String userId) {
        return quotaRepository.findByUserId(userId);
    }
    
    @Override
    public Flux<QuotaInfo> listQuotasByModel(String modelId) {
        return quotaRepository.findByModelId(modelId);
    }
    
    @Override
    public Mono<Long> getRemainingTokens(String userId, String modelId) {
        return quotaRepository.findByUserIdAndModelId(userId, modelId)
            .map(QuotaInfo::getRemainingTokens);
    }
    
    @Override
    public Mono<Long> getRemainingRequests(String userId, String modelId) {
        return quotaRepository.findByUserIdAndModelId(userId, modelId)
            .map(QuotaInfo::getRemainingRequests);
    }
} 