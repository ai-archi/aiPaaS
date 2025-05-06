package com.aixone.llm.infrastructure.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("quotas")
public class QuotaEntity {
    @Id
    private Long id;
    
    @Version
    private Long version;
    
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
    
    // 乐观锁更新token使用量
    public boolean incrementTokenUsage(Long tokens) {
        if (tokenUsed + tokens <= tokenLimit) {
            tokenUsed += tokens;
            updatedAt = LocalDateTime.now();
            return true;
        }
        return false;
    }
    
    // 乐观锁更新请求次数
    public boolean incrementRequestUsage() {
        if (requestUsed + 1 <= requestLimit) {
            requestUsed += 1;
            updatedAt = LocalDateTime.now();
            return true;
        }
        return false;
    }
} 