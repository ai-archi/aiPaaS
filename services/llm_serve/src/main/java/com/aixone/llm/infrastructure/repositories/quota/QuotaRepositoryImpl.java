package com.aixone.llm.infrastructure.repositories.quota;

import com.aixone.llm.domain.models.values.quota.QuotaInfo;
import com.aixone.llm.domain.models.values.quota.QuotaType;
import com.aixone.llm.domain.repositories.quota.QuotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class QuotaRepositoryImpl implements QuotaRepository {
    private final R2dbcEntityTemplate template;

    @Override
    public Mono<QuotaInfo> save(QuotaInfo quotaInfo) {
        // 保存或更新配额信息
        if (quotaInfo.getUserId() == null || quotaInfo.getModelId() == null) {
            return template.insert(QuotaInfo.class).using(quotaInfo);
        } else {
            return template.update(QuotaInfo.class)
                .matching(Query.query(Criteria.where("user_id").is(quotaInfo.getUserId()).and("model_id").is(quotaInfo.getModelId())))
                .apply(org.springframework.data.relational.core.query.Update.update("token_used", quotaInfo.getTokenUsed()))
                .then(findByUserIdAndModelId(quotaInfo.getUserId(), quotaInfo.getModelId()));
        }
    }

    @Override
    public Mono<QuotaInfo> findByUserIdAndModelId(String userId, String modelId) {
        return template.selectOne(Query.query(Criteria.where("user_id").is(userId).and("model_id").is(modelId)), QuotaInfo.class);
    }

    @Override
    public Flux<QuotaInfo> findByUserId(String userId) {
        return template.select(Query.query(Criteria.where("user_id").is(userId)), QuotaInfo.class);
    }

    @Override
    public Flux<QuotaInfo> findByModelId(String modelId) {
        return template.select(Query.query(Criteria.where("model_id").is(modelId)), QuotaInfo.class);
    }

    @Override
    public Mono<Void> deleteByUserIdAndModelId(String userId, String modelId) {
        return template.delete(Query.query(Criteria.where("user_id").is(userId).and("model_id").is(modelId)), QuotaInfo.class).then();
    }

    @Override
    public Flux<QuotaInfo> findByPolicyId(String policyId) {
        return template.select(Query.query(Criteria.where("policy_id").is(policyId)), QuotaInfo.class);
    }

    @Override
    public Mono<QuotaInfo> updateUsage(String userId, String modelId, long usage) {
        // 这里假设更新 tokenUsed 字段
        return template.update(QuotaInfo.class)
            .matching(Query.query(Criteria.where("user_id").is(userId).and("model_id").is(modelId)))
            .apply(org.springframework.data.relational.core.query.Update.update("token_used", usage))
            .flatMap(rows -> findByUserIdAndModelId(userId, modelId));
    }

    @Override
    public Mono<QuotaInfo> resetUsage(String userId, String modelId) {
        // 重置 tokenUsed 字段为0
        return template.update(QuotaInfo.class)
            .matching(Query.query(Criteria.where("user_id").is(userId).and("model_id").is(modelId)))
            .apply(org.springframework.data.relational.core.query.Update.update("token_used", 0L))
            .flatMap(rows -> findByUserIdAndModelId(userId, modelId));
    }

    @Override
    public Mono<QuotaInfo> incrementTokenUsage(String userId, String modelId, Long tokens) {
        return findByUserIdAndModelId(userId, modelId)
            .flatMap(info -> {
                long used = info.getTokenUsed() == null ? 0 : info.getTokenUsed();
                return updateUsage(userId, modelId, used + tokens);
            });
    }

    @Override
    public Mono<QuotaInfo> incrementRequestUsage(String userId, String modelId) {
        return findByUserIdAndModelId(userId, modelId)
            .flatMap(info -> {
                long used = info.getRequestUsed() == null ? 0 : info.getRequestUsed();
                return template.update(QuotaInfo.class)
                    .matching(Query.query(Criteria.where("user_id").is(userId).and("model_id").is(modelId)))
                    .apply(org.springframework.data.relational.core.query.Update.update("request_used", used + 1))
                    .flatMap(rows -> findByUserIdAndModelId(userId, modelId));
            });
    }

    @Override
    public Mono<Boolean> checkAndReserveQuota(String userId, String modelId, Long tokens) {
        return findByUserIdAndModelId(userId, modelId)
            .map(info -> {
                long used = info.getTokenUsed() == null ? 0 : info.getTokenUsed();
                long limit = info.getTokenLimit() == null ? Long.MAX_VALUE : info.getTokenLimit();
                return used + tokens <= limit;
            })
            .flatMap(canReserve -> {
                if (canReserve) {
                    return incrementTokenUsage(userId, modelId, tokens).thenReturn(true);
                } else {
                    return Mono.just(false);
                }
            });
    }

    @Override
    public Mono<Long> getBalance(String userId, String modelId, QuotaType quotaType) {
        // 这里只返回剩余token数
        return findByUserIdAndModelId(userId, modelId).map(QuotaInfo::getRemainingTokens);
    }

    @Override
    public Mono<Void> deductBalance(String userId, String modelId, QuotaType quotaType, long amount) {
        return findByUserIdAndModelId(userId, modelId)
            .flatMap(info -> {
                long used = info.getTokenUsed() == null ? 0 : info.getTokenUsed();
                return updateUsage(userId, modelId, used + amount).then();
            });
    }

    @Override
    public Mono<Void> addBalance(String userId, String modelId, QuotaType quotaType, long amount) {
        return findByUserIdAndModelId(userId, modelId)
            .flatMap(info -> {
                long limit = info.getTokenLimit() == null ? 0 : info.getTokenLimit();
                return template.update(QuotaInfo.class)
                    .matching(Query.query(Criteria.where("user_id").is(userId).and("model_id").is(modelId)))
                    .apply(org.springframework.data.relational.core.query.Update.update("token_limit", limit + amount))
                    .then();
            });
    }

    @Override
    public Flux<QuotaInfo> findExceededQuotas(double threshold) {
        return template.select(Query.query(Criteria.where("usage").greaterThanOrEquals(threshold)), QuotaInfo.class);
    }

    @Override
    public Flux<QuotaInfo> findExpiringSoon(int daysThreshold) {
        java.time.LocalDateTime soon = java.time.LocalDateTime.now().plusDays(daysThreshold);
        return template.select(Query.query(Criteria.where("expires_at").lessThanOrEquals(soon)), QuotaInfo.class);
    }
} 