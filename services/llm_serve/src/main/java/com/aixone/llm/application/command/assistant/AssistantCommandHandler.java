package com.aixone.llm.application.command.assistant;

import com.aixone.llm.domain.models.aggregates.assistant.Assistant;
import com.aixone.llm.domain.services.AssistantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 助理相关命令处理器
 */
@Component
@RequiredArgsConstructor
public class AssistantCommandHandler {
    private final AssistantService assistantService;

    /**
     * 创建助理
     */
    public Mono<Assistant> create(Assistant assistant) {
        return assistantService.createAssistant(assistant);
    }

    /**
     * 更新助理
     */
    public Mono<Assistant> update(String assistantId, Assistant assistant) {
        return assistantService.updateAssistant(assistantId, assistant);
    }

    /**
     * 删除助理
     */
    public Mono<Void> delete(String assistantId) {
        return assistantService.deleteAssistant(assistantId);
    }

    /**
     * 查询单个助理
     */
    public Mono<Assistant> get(String assistantId) {
        return assistantService.getAssistant(assistantId);
    }

    /**
     * 查询所有助理
     */
    public Flux<Assistant> list() {
        return assistantService.listAssistants();
    }
} 