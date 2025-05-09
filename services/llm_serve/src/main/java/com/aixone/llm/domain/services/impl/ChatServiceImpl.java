package com.aixone.llm.domain.services.impl;

import com.aixone.llm.domain.models.chat.ChatRequest;
import com.aixone.llm.domain.models.chat.ChatResponse;
import com.aixone.llm.domain.services.ChatService;
import com.aixone.llm.domain.services.ModelInvokeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    
    private final ModelInvokeService modelInvokeService;

    @Override
    public Flux<ChatResponse> chatCompletion(ChatRequest request) {
        return modelInvokeService.invokeChat(request);
    }
} 