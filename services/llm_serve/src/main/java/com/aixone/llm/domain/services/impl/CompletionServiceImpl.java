package com.aixone.llm.domain.services.impl;

import com.aixone.llm.application.command.completion.CompletionCommand;
import com.aixone.llm.domain.models.values.config.ModelResponse;
import com.aixone.llm.domain.services.CompletionService;
import com.aixone.llm.domain.services.ModelAdapterFactory;
import com.aixone.llm.domain.services.ModelAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompletionServiceImpl implements CompletionService {
    private final ModelAdapterFactory modelAdapterFactory;

    @Override
    public Mono<ModelResponse> completion(CompletionCommand command) {
        if (command.getPrompt() == null || command.getPrompt().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Prompt不能为空"));
        }
        if (command.getModel() == null || command.getModel().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Model不能为空"));
        }
        String providerName = command.getProvider() != null ? command.getProvider() : "deepseek";
        ModelAdapter adapter = modelAdapterFactory.getAdapter(providerName);
        if (adapter == null) {
            return Mono.error(new RuntimeException("未找到对应的模型适配器: " + providerName));
        }
        return adapter.invoke(command.getPrompt(), command.getModel())
                .map(result -> {
                    ModelResponse.Message message = ModelResponse.Message.builder()
                            .role("assistant")
                            .content(result)
                            .build();
                    ModelResponse.Choice modelChoice = ModelResponse.Choice.builder()
                            .index(0)
                            .message(message)
                            .finishReason("stop")
                            .build();
                    ModelResponse.Usage usage = ModelResponse.Usage.builder()
                            .promptTokens(0)
                            .completionTokens(0)
                            .totalTokens(0)
                            .build();
                    return ModelResponse.builder()
                            .id(UUID.randomUUID().toString())
                            .object("text_completion")
                            .created(Instant.now().getEpochSecond())
                            .model(command.getModel())
                            .choices(Collections.singletonList(modelChoice))
                            .usage(usage)
                            .build();
                });
    }
} 