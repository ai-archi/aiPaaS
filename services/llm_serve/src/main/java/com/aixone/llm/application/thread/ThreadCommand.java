package com.aixone.llm.application.thread;

import com.aixone.llm.domain.models.thread.ThreadStatus;

import lombok.Data;

@Data
public class ThreadCommand {
    private String title;
    private ThreadStatus status;
    private String userId;
} 