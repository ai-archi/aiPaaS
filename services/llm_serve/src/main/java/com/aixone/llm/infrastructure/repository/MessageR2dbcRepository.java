package com.aixone.llm.infrastructure.repository;

import com.aixone.llm.infrastructure.entity.MessageEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface MessageR2dbcRepository extends ReactiveCrudRepository<MessageEntity, String> {
    Flux<MessageEntity> findByThreadId(String threadId);
    
    @Query("SELECT * FROM messages WHERE thread_id = :threadId ORDER BY created_at DESC LIMIT :limit")
    Flux<MessageEntity> findLatestByThreadId(String threadId, int limit);
    
    @Query("SELECT * FROM messages WHERE thread_id = :threadId AND role = :role ORDER BY created_at DESC")
    Flux<MessageEntity> findByThreadIdAndRole(String threadId, String role);
} 