package com.aixone.llm.infrastructure.repository.impl;

import com.aixone.llm.domain.models.values.quota.QuotaInfo;
import com.aixone.llm.domain.repositories.quota.QuotaRepository;
import com.aixone.llm.infrastructure.entity.QuotaEntity;
import com.aixone.llm.infrastructure.repository.QuotaR2dbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class QuotaRepositoryImpl implements QuotaRepository {
    private final QuotaR2dbcRepository r2dbcRepository;

    @Override
    public Mono<QuotaInfo> save(QuotaInfo quotaInfo) {
        QuotaEntity entity = toEntity(quotaInfo);
        return r2dbcRepository.save(entity)
            .map(this::toDomain);
    }

    @Override
    public Mono<QuotaInfo> findByUserIdAndModelId(String userId, String modelId) {
        return r2dbcRepository.findByUserIdAndModelId(userId, modelId)
            .map(this::toDomain);
    }

    @Override
    public Flux<QuotaInfo> findByUserId(String userId) {
        return r2dbcRepository.findByUserId(userId)
            .map(this::toDomain);
    }

    @Override
    public Flux<QuotaInfo> findByModelId(String modelId) {
        return r2dbcRepository.findByModelId(modelId)
            .map(this::toDomain);
    }

    @Override
    public Mono<Void> deleteByUserIdAndModelId(String userId, String modelId) {
        return r2dbcRepository.deleteByUserIdAndModelId(userId, modelId);
    }

    @Override
    public Mono<QuotaInfo> incrementTokenUsage(String userId, String modelId, Long tokens) {
        return r2dbcRepository.incrementTokenUsage(userId, modelId, tokens)
            .filter(updated -> updated)
            .flatMap(updated -> findByUserIdAndModelId(userId, modelId))
            .switchIfEmpty(Mono.error(new IllegalStateException("Failed to increment token usage")));
    }

    @Override
    public Mono<QuotaInfo> incrementRequestUsage(String userId, String modelId) {
        return r2dbcRepository.incrementRequestUsage(userId, modelId)
            .filter(updated -> updated)
            .flatMap(updated -> findByUserIdAndModelId(userId, modelId))
            .switchIfEmpty(Mono.error(new IllegalStateException("Failed to increment request usage")));
    }

    @Override
    public Mono<Boolean> checkAndReserveQuota(String userId, String modelId, Long tokens) {
        return findByUserIdAndModelId(userId, modelId)
            .filter(quota -> quota.isValid())
            .filter(quota -> quota.hasTokenQuota())
            .filter(quota -> quota.hasRequestQuota())
            .map(quota -> true)
            .defaultIfEmpty(false);
    }

    @Override
    public Mono<Long> getBalance(String userId, String modelId, com.aixone.llm.domain.models.values.quota.QuotaType quotaType) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Mono<Void> deductBalance(String userId, String modelId, com.aixone.llm.domain.models.values.quota.QuotaType quotaType, long amount) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Mono<Void> addBalance(String userId, String modelId, com.aixone.llm.domain.models.values.quota.QuotaType quotaType, long amount) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private QuotaEntity toEntity(QuotaInfo domain) {
        QuotaEntity entity = new QuotaEntity();
        entity.setUserId(domain.getUserId());
        entity.setModelId(domain.getModelId());
        entity.setTokenLimit(domain.getTokenLimit());
        entity.setTokenUsed(domain.getTokenUsed());
        entity.setRequestLimit(domain.getRequestLimit());
        entity.setRequestUsed(domain.getRequestUsed());
        entity.setExpiresAt(domain.getExpiresAt());
        entity.setQuotaType(domain.getQuotaType());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private QuotaInfo toDomain(QuotaEntity entity) {
        return QuotaInfo.builder()
            .userId(entity.getUserId())
            .modelId(entity.getModelId())
            .tokenLimit(entity.getTokenLimit())
            .tokenUsed(entity.getTokenUsed())
            .requestLimit(entity.getRequestLimit())
            .requestUsed(entity.getRequestUsed())
            .expiresAt(entity.getExpiresAt())
            .quotaType(entity.getQuotaType())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
} 