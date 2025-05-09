package com.aixone.llm.domain.services.impl;

import com.aixone.llm.domain.models.values.config.ModelRequest;
import com.aixone.llm.domain.models.values.config.ModelResponse;
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
    public Flux<ModelResponse> chatCompletion(ModelRequest request) {
        return modelInvokeService.invoke(request);
    }
} 