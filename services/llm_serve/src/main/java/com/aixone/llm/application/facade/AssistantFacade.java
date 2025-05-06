package com.aixone.llm.application.facade;

import com.aixone.llm.application.command.assistant.AssistantCommand;
import com.aixone.llm.application.command.thread.ThreadCommand;
import com.aixone.llm.application.command.message.MessageCommand;
import com.aixone.llm.domain.models.aggregates.assistant.Assistant;
import com.aixone.llm.domain.models.entities.thread.Thread;
import com.aixone.llm.domain.models.entities.message.Message;
import com.aixone.llm.domain.services.AssistantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AssistantFacade {
    private final AssistantService assistantService;

    public Mono<Assistant> createAssistant(AssistantCommand command) {
        Assistant assistant = new Assistant();
        assistant.setName(command.getName());
        assistant.setUserId(command.getUserId());
        assistant.setModelId(command.getModelId());
        assistant.setToolConfigs(command.getToolConfigs());
        assistant.setCapability(command.getCapability());
        assistant.setCreatedAt(LocalDateTime.now());
        assistant.setUpdatedAt(LocalDateTime.now());
        return assistantService.createAssistant(assistant);
    }

    public Mono<Assistant> updateAssistant(String assistantId, AssistantCommand command) {
        Assistant assistant = new Assistant();
        assistant.setId(assistantId);
        assistant.setName(command.getName());
        assistant.setUserId(command.getUserId());
        assistant.setModelId(command.getModelId());
        assistant.setToolConfigs(command.getToolConfigs());
        assistant.setCapability(command.getCapability());
        assistant.setUpdatedAt(LocalDateTime.now());
        return assistantService.updateAssistant(assistantId, assistant);
    }

    public Mono<Assistant> getAssistant(String assistantId) {
        return assistantService.getAssistant(assistantId);
    }

    public Flux<Assistant> listAssistants() {
        return assistantService.listAssistants();
    }

    public Mono<Void> deleteAssistant(String assistantId) {
        return assistantService.deleteAssistant(assistantId);
    }

    public Mono<Thread> createThread(String assistantId, ThreadCommand command) {
        Thread thread = new Thread();
        thread.setTitle(command.getTitle());
        thread.setStatus(command.getStatus());
        thread.setUserId(command.getUserId());
        thread.setCreatedAt(LocalDateTime.now());
        thread.setUpdatedAt(LocalDateTime.now());
        return assistantService.createThread(assistantId, thread);
    }

    public Mono<Thread> updateThread(String assistantId, String threadId, ThreadCommand command) {
        Thread thread = new Thread();
        thread.setId(threadId);
        thread.setAssistantId(assistantId);
        thread.setTitle(command.getTitle());
        thread.setStatus(command.getStatus());
        thread.setUserId(command.getUserId());
        thread.setUpdatedAt(LocalDateTime.now());
        return assistantService.updateThread(assistantId, threadId, thread);
    }

    public Mono<Thread> getThread(String assistantId, String threadId) {
        return assistantService.getThread(assistantId, threadId);
    }

    public Flux<Thread> listThreads(String assistantId) {
        return assistantService.listThreads(assistantId);
    }

    public Mono<Void> deleteThread(String assistantId, String threadId) {
        return assistantService.deleteThread(assistantId, threadId);
    }

    public Mono<Message> createMessage(String assistantId, String threadId, MessageCommand command) {
        Message message = new Message();
        message.setRole(command.getRole());
        message.setContent(command.getContent());
        message.setTimestamp(LocalDateTime.now());
        return assistantService.createMessage(assistantId, threadId, message);
    }

    public Mono<Message> updateMessage(String assistantId, String threadId, String messageId, MessageCommand command) {
        Message message = new Message();
        message.setId(messageId);
        message.setThreadId(threadId);
        message.setRole(command.getRole());
        message.setContent(command.getContent());
        message.setTimestamp(LocalDateTime.now());
        return assistantService.updateMessage(assistantId, threadId, messageId, message);
    }

    public Mono<Message> getMessage(String assistantId, String threadId, String messageId) {
        return assistantService.getMessage(assistantId, threadId, messageId);
    }

    public Flux<Message> listMessages(String assistantId, String threadId) {
        return assistantService.listMessages(assistantId, threadId);
    }

    public Mono<Void> deleteMessage(String assistantId, String threadId, String messageId) {
        return assistantService.deleteMessage(assistantId, threadId, messageId);
    }
} 