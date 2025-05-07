package com.aixone.llm.domain.models.entities.thread;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Thread {
    private String id;
    private String assistantId;
    private String userId;
    private String title;
    private ThreadStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String tenantId;
    private boolean deleted;
} 