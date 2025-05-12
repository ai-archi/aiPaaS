package com.aixone.llm.domain.services;

import com.aixone.llm.application.thread.ThreadCommand;
import com.aixone.llm.domain.models.assistant.Assistant;
import com.aixone.llm.domain.models.chat.Message;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 助理服务接口，负责助理生命周期管理、对话线程处理、消息处理和工具调用协调。
 */
public interface AssistantService {
    // 助理生命周期管理
    Mono<Assistant> createAssistant(Assistant assistant);
    Mono<Assistant> updateAssistant(String assistantId, Assistant assistant);
    Mono<Void> deleteAssistant(String assistantId);
    Mono<Assistant> getAssistant(String assistantId);
    Flux<Assistant> listAssistants();

    // 多租户重载
    Mono<Assistant> createAssistant(Assistant assistant, String tenantId);
    Mono<Assistant> updateAssistant(String tenantId, String assistantId, Assistant assistant);
    Mono<Void> deleteAssistant(String tenantId, String assistantId);
    Mono<Assistant> getAssistant(String tenantId, String assistantId);
    Flux<Assistant> listAssistants(String tenantId);

    // 对话线程处理
    Mono<ThreadCommand> createThread(String assistantId, ThreadCommand command);
    Mono<ThreadCommand> updateThread(String assistantId, String threadId, ThreadCommand command);
    Mono<Void> deleteThread(String assistantId, String threadId);
    Mono<ThreadCommand> getThread(String assistantId, String threadId);
    Flux<ThreadCommand> listThreads(String assistantId);

    // 消息处理和路由
    Mono<Message> createMessage(String assistantId, String threadId, Message message);
    Mono<Message> updateMessage(String assistantId, String threadId, String messageId, Message message);
    Mono<Void> deleteMessage(String assistantId, String threadId, String messageId);
    Mono<Message> getMessage(String assistantId, String threadId, String messageId);
    Flux<Message> listMessages(String assistantId, String threadId);

    // 工具调用协调（预留扩展）
    Mono<Void> invokeTool(String assistantId, String threadId, String messageId, String toolName, String params);
} 