package com.aixone.llm.domain.models.values.quota;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuotaInfo {
    private String userId;
    private String modelId;
    private Long tokenLimit;
    private Long tokenUsed;
    private Long requestLimit;
    private Long requestUsed;
    private LocalDateTime expiresAt;
    private String quotaType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String tenantId;
    private boolean deleted;
    public boolean isValid() {
        return expiresAt == null || expiresAt.isAfter(LocalDateTime.now());
    }
    public boolean hasTokenQuota() {
        return tokenLimit == null || tokenUsed < tokenLimit;
    }
    public boolean hasRequestQuota() {
        return requestLimit == null || requestUsed < requestLimit;
    }
    public long getRemainingTokens() {
        return tokenLimit == null ? Long.MAX_VALUE : tokenLimit - tokenUsed;
    }
    public long getRemainingRequests() {
        return requestLimit == null ? Long.MAX_VALUE : requestLimit - requestUsed;
    }
} 