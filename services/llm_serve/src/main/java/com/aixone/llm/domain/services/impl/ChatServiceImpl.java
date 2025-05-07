package com.aixone.llm.domain.services.impl;

import com.aixone.llm.application.command.chat.ChatCompletionCommand;
import com.aixone.llm.domain.models.values.config.ModelRequest;
import com.aixone.llm.domain.models.values.config.ModelResponse;
import com.aixone.llm.domain.services.ChatService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;
import java.util.Map;

@Service
public class ChatServiceImpl implements ChatService {
    @Override
    public ModelResponse chatCompletion(ModelRequest request) {
        // 假设ModelRequest有getMessages()和getModel()方法
        // 如需更具体类型，可加类型判断
        String reply = "模拟回复：";
        if (request.getMessages() != null && !request.getMessages().isEmpty()) {
            Object lastMsg = request.getMessages().get(request.getMessages().size() - 1);
            // 假设消息为Map<String, Object>或有getContent方法
            if (lastMsg instanceof Map) {
                Object content = ((Map) lastMsg).get("content");
                if (content != null) reply += content.toString();
            } else if (lastMsg != null) {
                try {
                    java.lang.reflect.Method m = lastMsg.getClass().getMethod("getContent");
                    Object content = m.invoke(lastMsg);
                    if (content != null) reply += content.toString();
                } catch (Exception ignore) {}
            }
        }
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
                .model(request.getModel())
                .choices(Collections.singletonList(choice))
                .usage(usage)
                .build();
    }
} 