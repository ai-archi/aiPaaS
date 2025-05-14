package com.aixone.llm.domain.repositories.model;

import com.aixone.llm.domain.models.model.UserModelKey;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserModelKeyRepository {
    Mono<UserModelKey> save(UserModelKey key);
    Mono<UserModelKey> findById(String id);
    Flux<UserModelKey> findByOwnerId(String ownerId);
    Flux<UserModelKey> findByModelName(String modelName);
    Mono<UserModelKey> findByOwnerIdAndModelName(String ownerId, String modelName);
    Mono<Void> deleteById(String id);
} 