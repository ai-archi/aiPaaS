package com.aixone.llm.domain.repositories.model;

import com.aixone.llm.domain.models.model.UserModelKeyGrant;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserModelKeyGrantRepository {
    Mono<UserModelKeyGrant> save(UserModelKeyGrant grant);
    Mono<UserModelKeyGrant> findById(String id);
    Flux<UserModelKeyGrant> findByGranteeId(String granteeId);
    Flux<UserModelKeyGrant> findByKeyId(String keyId);
    Mono<Void> deleteById(String id);
} 