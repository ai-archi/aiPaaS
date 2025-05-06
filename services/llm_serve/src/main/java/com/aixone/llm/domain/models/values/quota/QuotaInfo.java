package com.aixone.llm.domain.models.values.quota;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

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
        return tokenLimit == null ? Long.MAX_VALUE : tokenLimit - (tokenUsed == null ? 0 : tokenUsed);
    }
    public long getRemainingRequests() {
        return requestLimit == null ? Long.MAX_VALUE : requestLimit - (requestUsed == null ? 0 : requestUsed);
    }
} 