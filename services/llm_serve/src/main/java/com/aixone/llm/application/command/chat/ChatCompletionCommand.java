package com.aixone.llm.application.command.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatCompletionCommand {
    private String model;
    private List<Message> messages;
    private boolean stream;
    private Double temperature;
    private Integer maxTokens;
    private Double topP;
    private Integer n;
    private List<String> stop;
    private Double presencePenalty;
    private Double frequencyPenalty;
    private String user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role; // system, user, assistant
        private String content;
        private String name;
    }
} 