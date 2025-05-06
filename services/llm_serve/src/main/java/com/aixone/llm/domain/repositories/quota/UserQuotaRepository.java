package com.aixone.llm.domain.repositories.quota;

import com.aixone.llm.domain.models.aggregates.userquota.UserQuota;
import com.aixone.llm.domain.repositories.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 用户配额仓储接口
 */
public interface UserQuotaRepository extends Repository<UserQuota, String> {
    /**
     * 根据用户ID查找配额
     */
    Mono<UserQuota> findByUserId(String userId);

    /**
     * 查找特定策略下的配额
     */
    Flux<UserQuota> findByPolicyId(String policyId);

    /**
     * 更新配额使用量
     */
    Mono<UserQuota> updateUsage(String userId, long usage);

    /**
     * 重置配额使用量
     */
    Mono<UserQuota> resetUsage(String userId);

    /**
     * 查找超出配额警戒线的用户
     */
    Flux<UserQuota> findExceededQuotas(double threshold);

    /**
     * 查找即将到期的计费周期
     */
    Flux<UserQuota> findExpiringSoon(int daysThreshold);
} 