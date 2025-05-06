package com.aixone.llm.application.command.chat;

import com.aixone.llm.domain.services.ChatService;
import com.aixone.llm.domain.models.values.config.ModelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatCommandHandler {
    private final ChatService chatService;

    public ModelResponse handle(ChatCompletionCommand command) {
        // 这里调用领域服务进行业务处理
        return chatService.chatCompletion(command);
    }
} 