package com.aixone.llm.application.command.chat;

import com.aixone.llm.domain.models.values.config.ModelRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatCompletionCommand extends ModelRequest {
    private Integer n;
    private List<String> stop;
    private Double presencePenalty;
    private Double frequencyPenalty;
    private String user;

    public ModelRequest toModelRequest() {
        return ((ModelRequest.ModelRequestBuilder) ModelRequest.builder())
                .model(getModel())
                .messages((List) getMessages())
                .prompt(getPrompt())
                .maxTokens(getMaxTokens())
                .temperature(getTemperature())
                .topP(getTopP())
                .stream(getStream())
                .extraParams(getExtraParams())
                .build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role; // system, user, assistant
        private String content;
        private String name;
    }
} 