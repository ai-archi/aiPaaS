package com.aixone.llm.domain.services;

import com.aixone.llm.application.command.chat.ChatCompletionCommand;
import com.aixone.llm.domain.models.values.config.ModelResponse;

public interface ChatService {
    ModelResponse chatCompletion(ChatCompletionCommand command);
} 