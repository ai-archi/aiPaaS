package com.aixone.llm.domain.repositories.assistant;

import com.aixone.llm.domain.models.chat.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MessageRepository {
    Mono<Message> save(Message message);
    Mono<Message> findById(String id);
    Flux<Message> findByThreadId(String threadId);
    Mono<Void> deleteById(String id);
} 