package com.aixone.llm.application.chat;

import org.springframework.stereotype.Component;

import com.aixone.llm.domain.models.chat.ChatRequest;
import com.aixone.llm.domain.models.chat.ChatResponse;
import com.aixone.llm.domain.services.ChatService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class ChatCompletionCommandHandler {
    private final ChatService chatService;

    public Flux<ChatResponse> handle(ChatCompletionCommand command) {
        ChatRequest request = command.toChatRequest();
        return chatService.chatCompletion(request);
    }
} 