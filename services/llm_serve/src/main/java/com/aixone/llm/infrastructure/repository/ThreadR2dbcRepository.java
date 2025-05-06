package com.aixone.llm.infrastructure.repository;

import com.aixone.llm.domain.models.entities.Thread;
import com.aixone.llm.infrastructure.entity.ThreadEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ThreadR2dbcRepository extends ReactiveCrudRepository<ThreadEntity, String> {
    Flux<ThreadEntity> findByAssistantId(String assistantId);
    
    Flux<ThreadEntity> findByUserId(String userId);
    
    @Query("SELECT * FROM threads WHERE assistant_id = :assistantId AND status = :status")
    Flux<ThreadEntity> findByAssistantIdAndStatus(String assistantId, String status);
    
    @Query("SELECT COUNT(*) FROM threads WHERE assistant_id = :assistantId AND status = :status")
    Mono<Long> countByAssistantIdAndStatus(String assistantId, String status);
    
    @Query("UPDATE threads SET status = :status, updated_at = :updatedAt WHERE id = :id")
    Mono<Boolean> updateStatus(String id, String status, long updatedAt);
} 