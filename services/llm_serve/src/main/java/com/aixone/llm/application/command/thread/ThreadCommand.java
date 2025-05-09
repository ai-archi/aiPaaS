package com.aixone.llm.application.command.thread;

import com.aixone.llm.domain.models.thread.ThreadStatus;
import lombok.Data;

@Data
public class ThreadCommand {
    private String title;
    private ThreadStatus status;
    private String userId;
} 