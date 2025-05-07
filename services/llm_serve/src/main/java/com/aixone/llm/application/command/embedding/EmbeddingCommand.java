package com.aixone.llm.application.command.embedding;

import com.aixone.llm.domain.models.values.config.ModelRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddingCommand extends ModelRequest {
    private String user;
    private List<String> input;
    private String model;

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
