package com.aixone.llm.domain.repositories.assistant;

import java.util.List;

import com.aixone.llm.domain.models.assistant.Assistant;
import com.aixone.llm.domain.models.assistant.ToolConfig;
import com.aixone.llm.domain.models.thread.Thread;
import com.aixone.llm.domain.repositories.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 助理仓储接口
 */
public interface AssistantRepository extends Repository<Assistant, String> {
    /**
     * 根据用户ID查找助理
     */
    Flux<Assistant> findByUserId(String userId);

    /**
     * 根据模型ID查找助理
     */
    Flux<Assistant> findByModelId(String modelId);

    /**
     * 更新助理工具配置
     */
    Mono<Assistant> updateToolConfig(String assistantId, List<ToolConfig> toolConfigs);

    /**
     * 获取助理的所有对话线程
     */
    Flux<Thread> getThreads(String assistantId);

    /**
     * 创建新的对话线程
     */
    Mono<Thread> createThread(String assistantId, String userId, String title);

    /**
     * 获取助理的活跃线程数
     */
    Mono<Long> countActiveThreads(String assistantId);

    @Override
    Mono<Assistant> save(Assistant assistant);
    @Override
    Mono<Assistant> findById(String id);
    @Override
    Flux<Assistant> findAll();
    @Override
    Mono<Void> deleteById(String id);
} 