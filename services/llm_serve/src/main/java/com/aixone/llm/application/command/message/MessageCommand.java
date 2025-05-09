package com.aixone.llm.application.command.message;

import lombok.Data;
import java.util.List;
import java.util.Map;
import com.aixone.llm.domain.models.chat.Message;

@Data
public class MessageCommand {
    private String role;
    private String content;
    private String model;   
    private List<Message> messages;
    private Integer maxTokens;
    private Double temperature;
    private Double topP;
    private Boolean stream;
    private Map<String, Object> extraParams;
} 