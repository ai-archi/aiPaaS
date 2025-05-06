package com.aixone.llm.domain.event.quota;

import com.aixone.llm.domain.models.values.quota.QuotaType;

/**
 * 配额超限事件
 */
public class QuotaExceededEvent extends QuotaEvent {
    private final QuotaType quotaType;
    private final long currentUsage;
    private final long limit;

    public QuotaExceededEvent(String userId, int version,
                             QuotaType quotaType,
                             long currentUsage,
                             long limit) {
        super("QUOTA_EXCEEDED", userId, version);
        this.quotaType = quotaType;
        this.currentUsage = currentUsage;
        this.limit = limit;
    }

    public QuotaType getQuotaType() {
        return quotaType;
    }

    public long getCurrentUsage() {
        return currentUsage;
    }

    public long getLimit() {
        return limit;
    }

    public double getExceedPercentage() {
        return (double) (currentUsage - limit) / limit * 100;
    }
} 