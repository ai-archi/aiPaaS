package com.aixone.llm.domain.services;

import com.aixone.llm.domain.models.values.config.ModelRequest;
import com.aixone.llm.domain.models.values.config.ModelResponse;

public interface ChatService {
    ModelResponse chatCompletion(ModelRequest request);
} 