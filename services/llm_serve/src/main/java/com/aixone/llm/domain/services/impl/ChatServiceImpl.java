package com.aixone.llm.domain.services.impl;

import com.aixone.llm.application.command.chat.ChatCompletionCommand;
import com.aixone.llm.domain.models.values.config.ModelResponse;
import com.aixone.llm.domain.services.ChatService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

@Service
public class ChatServiceImpl implements ChatService {
    @Override
    public ModelResponse chatCompletion(ChatCompletionCommand command) {
        // 构造模拟回复
        String reply = "模拟回复：" + command.getMessages().get(command.getMessages().size() - 1).getContent();
        ModelResponse.Message message = ModelResponse.Message.builder()
                .role("assistant")
                .content(reply)
                .build();
        ModelResponse.Choice choice = ModelResponse.Choice.builder()
                .index(0)
                .message(message)
                .finishReason("stop")
                .build();
        ModelResponse.Usage usage = ModelResponse.Usage.builder()
                .promptTokens(10)
                .completionTokens(10)
                .totalTokens(20)
                .build();
        return ModelResponse.builder()
                .id(UUID.randomUUID().toString())
                .object("chat.completion")
                .created(Instant.now().getEpochSecond())
                .model(command.getModel())
                .choices(Collections.singletonList(choice))
                .usage(usage)
                .build();
    }
} 