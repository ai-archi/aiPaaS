package com.aixone.llm.application.command.completion;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompletionCommand {
    private String model;
    private String prompt;
    private boolean stream;
    private Double temperature;
    private Integer maxTokens;
    private Double topP;
    private Integer n;
    private List<String> stop;
    private Double presencePenalty;
    private Double frequencyPenalty;
    private String user;
    private String provider;

    public String getProvider() {
        return provider;
    }
} 