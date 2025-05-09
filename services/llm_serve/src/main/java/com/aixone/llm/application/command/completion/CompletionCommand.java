package com.aixone.llm.application.command.completion;

import com.aixone.llm.domain.models.values.config.ModelRequest;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CompletionCommand extends ModelRequest {
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

    public ModelRequest toModelRequest() {
        return  ModelRequest.builder()
                .build();
    }
} 