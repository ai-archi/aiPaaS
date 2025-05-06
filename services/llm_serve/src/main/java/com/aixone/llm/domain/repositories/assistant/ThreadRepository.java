package com.aixone.llm.domain.repositories.assistant;

import com.aixone.llm.domain.models.entities.thread.Thread;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ThreadRepository {
    Mono<Thread> save(Thread thread);
    Mono<Thread> findById(String id);
    Flux<Thread> findByAssistantId(String assistantId);
    Mono<Void> deleteById(String id);
} 