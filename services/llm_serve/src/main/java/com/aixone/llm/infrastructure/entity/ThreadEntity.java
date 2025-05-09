package com.aixone.llm.infrastructure.entity;

import com.aixone.llm.domain.models.thread.ThreadStatus;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table("threads")
public class ThreadEntity {
    @Id
    private String id;
    
    @Version
    private Long version;
    
    private String assistantId;
    private String userId;
    private String title;
    private ThreadStatus status;
    private long createdAt;
    private long updatedAt;
} 