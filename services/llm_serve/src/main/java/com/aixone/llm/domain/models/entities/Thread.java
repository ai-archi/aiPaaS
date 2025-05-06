package com.aixone.llm.domain.models.entities;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Thread {
    private String id;
    private String assistantId;
    private String userId;
    private String title;
    private ThreadStatus status;
    // 可扩展更多字段
} 