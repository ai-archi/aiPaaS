package com.aixone.llm.application.command.invoke;

import com.aixone.llm.domain.models.values.config.ModelRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelInvokeCommand extends ModelRequest {
    private String userId;
    private String modelId;
    private boolean stream;

    public ModelRequest toModelRequest() {
        return ModelRequest.builder()
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