package com.aixone.llm.domain.services.impl;

import com.aixone.llm.domain.models.assistant.Assistant;
import com.aixone.llm.domain.models.thread.Thread;
import com.aixone.llm.domain.models.chat.Message;
import com.aixone.llm.domain.repositories.assistant.AssistantRepository;
import com.aixone.llm.domain.repositories.assistant.ThreadRepository;
import com.aixone.llm.domain.services.AssistantService;
import com.aixone.llm.domain.repositories.assistant.MessageRepository;
import com.aixone.llm.application.command.thread.ThreadCommand;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssistantServiceImpl implements AssistantService {
    private final AssistantRepository assistantRepository;
    private final ThreadRepository threadRepository;
    private final MessageRepository messageRepository;

    // 助理生命周期管理
    @Override
    public Mono<Assistant> createAssistant(Assistant assistant) {
        assistant.setCreatedAt(LocalDateTime.now());
        assistant.setUpdatedAt(LocalDateTime.now());
        return assistantRepository.save(assistant);
    }

    @Override
    public Mono<Assistant> updateAssistant(String assistantId, Assistant assistant) {
        return assistantRepository.findById(assistantId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("助理不存在")))
                .flatMap(existing -> {
                    assistant.setId(assistantId);
                    assistant.setCreatedAt(existing.getCreatedAt());
                    assistant.setUpdatedAt(LocalDateTime.now());
                    return assistantRepository.save(assistant);
                });
    }

    @Override
    public Mono<Void> deleteAssistant(String assistantId) {
        return assistantRepository.deleteById(assistantId);
    }

    @Override
    public Mono<Assistant> getAssistant(String assistantId) {
        return assistantRepository.findById(assistantId);
    }

    @Override
    public Flux<Assistant> listAssistants() {
        return assistantRepository.findAll();
    }

    // 对话线程处理
    @Override
    public Mono<ThreadCommand> createThread(String assistantId, ThreadCommand command) {
        Thread thread = toThread(command);
        thread.setAssistantId(assistantId);
        thread.setCreatedAt(LocalDateTime.now());
        thread.setUpdatedAt(LocalDateTime.now());
        return assistantRepository.findById(assistantId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("助理不存在")))
                .flatMap(a -> threadRepository.save(thread))
                .map(this::toThreadCommand);
    }

    @Override
    public Mono<ThreadCommand> updateThread(String assistantId, String threadId, ThreadCommand command) {
        return threadRepository.findById(threadId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("线程不存在")))
                .flatMap(existing -> {
                    Thread thread = toThread(command);
                    thread.setId(threadId);
                    thread.setAssistantId(assistantId);
                    thread.setCreatedAt(existing.getCreatedAt());
                    thread.setUpdatedAt(LocalDateTime.now());
                    return threadRepository.save(thread);
                })
                .map(this::toThreadCommand);
    }

    @Override
    public Mono<Void> deleteThread(String assistantId, String threadId) {
        return threadRepository.deleteById(threadId);
    }

    @Override
    public Mono<ThreadCommand> getThread(String assistantId, String threadId) {
        return threadRepository.findById(threadId)
                .filter(thread -> assistantId.equals(thread.getAssistantId()))
                .map(this::toThreadCommand);
    }

    @Override
    public Flux<ThreadCommand> listThreads(String assistantId) {
        return threadRepository.findByAssistantId(assistantId)
                .map(this::toThreadCommand);
    }

    // DTO <-> 领域模型转换
    private Thread toThread(ThreadCommand command) {
        Thread thread = new Thread();
        thread.setTitle(command.getTitle());
        thread.setStatus(command.getStatus());
        thread.setUserId(command.getUserId());
        return thread;
    }

    private ThreadCommand toThreadCommand(Thread thread) {
        ThreadCommand command = new ThreadCommand();
        command.setTitle(thread.getTitle());
        command.setStatus(thread.getStatus());
        command.setUserId(thread.getUserId());
        return command;
    }

    // 消息处理和路由
    @Override
    public Mono<Message> createMessage(String assistantId, String threadId, Message message) {
        // 只保存Message，不再处理threadId、timestamp等
        return threadRepository.findById(threadId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("线程不存在")))
                .flatMap(t -> messageRepository.save(message));
    }

    @Override
    public Mono<Message> updateMessage(String assistantId, String threadId, String messageId, Message message) {
        return messageRepository.findById(messageId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("消息不存在")))
                .flatMap(existing -> messageRepository.save(message));
    }

    @Override
    public Mono<Void> deleteMessage(String assistantId, String threadId, String messageId) {
        return messageRepository.deleteById(messageId);
    }

    @Override
    public Mono<Message> getMessage(String assistantId, String threadId, String messageId) {
        return messageRepository.findById(messageId);
    }

    @Override
    public Flux<Message> listMessages(String assistantId, String threadId) {
        return messageRepository.findByThreadId(threadId);
    }

    // 工具调用协调（预留扩展）
    @Override
    public Mono<Void> invokeTool(String assistantId, String threadId, String messageId, String toolName, String params) {
        // 预留：可集成外部工具服务
        log.info("invokeTool: assistantId={}, threadId={}, messageId={}, toolName={}, params={}", assistantId, threadId, messageId, toolName, params);
        return Mono.empty();
    }

    @Override
    public Mono<Assistant> createAssistant(Assistant assistant, String tenantId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createAssistant'");
    }

    @Override
    public Mono<Assistant> updateAssistant(String tenantId, String assistantId, Assistant assistant) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateAssistant'");
    }

    @Override
    public Mono<Void> deleteAssistant(String tenantId, String assistantId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteAssistant'");
    }

    @Override
    public Mono<Assistant> getAssistant(String tenantId, String assistantId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAssistant'");
    }

    @Override
    public Flux<Assistant> listAssistants(String tenantId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listAssistants'");
    }
} 