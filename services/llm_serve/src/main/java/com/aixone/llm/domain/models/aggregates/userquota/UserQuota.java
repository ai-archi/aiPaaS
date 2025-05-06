package com.aixone.llm.domain.models.aggregates.userquota;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UserQuota {
    private String userId;
    private String policyId;
    private long usage;
    private long limit;
    private LocalDateTime expiresAt;
    // 可扩展更多字段
} 