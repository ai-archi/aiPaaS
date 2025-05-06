package com.aixone.llm.infrastructure.repository;

import com.aixone.llm.infrastructure.entity.AssistantEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AssistantR2dbcRepository extends ReactiveCrudRepository<AssistantEntity, String> {
    Flux<AssistantEntity> findByModelId(String modelId);
    
    @Query("SELECT * FROM assistants WHERE active = true")
    Flux<AssistantEntity> findAllActive();
    
    @Query("UPDATE assistants SET active = :active, updated_at = :updatedAt WHERE id = :id")
    Mono<Boolean> updateActive(String id, boolean active, long updatedAt);
    
    @Query("UPDATE assistants SET tools = :tools::jsonb, updated_at = :updatedAt WHERE id = :id")
    Mono<Boolean> updateTools(String id, String tools, long updatedAt);
} 