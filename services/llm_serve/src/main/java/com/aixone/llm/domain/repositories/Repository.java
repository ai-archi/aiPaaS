package com.aixone.llm.domain.repositories;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 仓储基础接口
 */
public interface Repository<T, ID> {
    /**
     * 保存实体
     */
    Mono<T> save(T entity);

    /**
     * 根据ID查找实体
     */
    Mono<T> findById(ID id);

    /**
     * 查找所有实体
     */
    Flux<T> findAll();

    /**
     * 删除实体
     */
    Mono<Void> delete(T entity);

    /**
     * 根据ID删除实体
     */
    Mono<Void> deleteById(ID id);

    /**
     * 检查实体是否存在
     */
    Mono<Boolean> existsById(ID id);
} 