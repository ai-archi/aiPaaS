package com.aixone.llm.application.command.chat;

import com.aixone.llm.domain.services.ChatService;
import com.aixone.llm.domain.models.values.config.ModelResponse;
import com.aixone.llm.domain.models.values.config.ModelRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class ChatCompletionCommandHandler {
    private final ChatService chatService;

    public Flux<ModelResponse> handle(ChatCompletionCommand command) {
        ModelRequest request = command.toModelRequest();
        return chatService.chatCompletion(request);
    }
} 