package com.aixone.llm.domain.services.impl;

import com.aixone.llm.domain.models.completion.CompletionRequest;
import com.aixone.llm.domain.models.completion.CompletionResponse;
import com.aixone.llm.domain.services.CompletionService;
import com.aixone.llm.domain.services.ModelInvokeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompletionServiceImpl implements CompletionService {
    private final ModelInvokeService modelInvokeService;

    @Override
    public Flux<CompletionResponse> completion(CompletionRequest request) {
        // 直接调用 ModelInvokeService 的 completion 调用链
        return modelInvokeService.invokeCompletion(request);
    }
} 