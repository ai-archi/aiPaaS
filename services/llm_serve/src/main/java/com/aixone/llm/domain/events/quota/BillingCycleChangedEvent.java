package com.aixone.llm.domain.event.quota;

import java.time.Instant;

/**
 * 计费周期变更事件
 */
public class BillingCycleChangedEvent extends QuotaEvent {
    private final String cycleId;
    private final Instant startTime;
    private final Instant endTime;
    private final CycleChangeType changeType;

    public BillingCycleChangedEvent(String userId, int version,
                                  String cycleId,
                                  Instant startTime,
                                  Instant endTime,
                                  CycleChangeType changeType) {
        super("BILLING_CYCLE_CHANGED", userId, version);
        this.cycleId = cycleId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.changeType = changeType;
    }

    public String getCycleId() {
        return cycleId;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public CycleChangeType getChangeType() {
        return changeType;
    }
}

/**
 * 周期变更类型枚举
 */
enum CycleChangeType {
    STARTED,    // 新周期开始
    ENDED,      // 当前周期结束
    RENEWED,    // 周期续期
    CANCELLED   // 周期取消
} 