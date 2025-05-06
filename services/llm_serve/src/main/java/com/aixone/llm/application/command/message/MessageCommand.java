package com.aixone.llm.application.command.message;

import lombok.Data;

@Data
public class MessageCommand {
    private String role;
    private String content;
} 