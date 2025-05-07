package com.aixone.llm.application.command.message;

import com.aixone.llm.domain.models.values.config.ModelRequest;
import lombok.Data;
import java.util.List;
import java.util.Map;
import com.aixone.llm.domain.models.entities.message.Message;

@Data
public class MessageCommand {
    private String role;
    private String content;
    private String model;   
    private List<Message> messages;
    private String prompt;
    private Integer maxTokens;
    private Double temperature;
    private Double topP;
    private Boolean stream;
    private Map<String, Object> extraParams;
    public ModelRequest toModelRequest() {
        return  ModelRequest.builder()
                .model(getModel())
                .messages(getMessages())
                .prompt(getPrompt())
                .maxTokens(getMaxTokens())
                .temperature(getTemperature())
                .topP(getTopP())
                .stream(getStream())
                .extraParams(getExtraParams())
                .build();
    }
} 