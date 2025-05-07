package com.aixone.llm.infrastructure.repositories.assistant;

import com.aixone.llm.domain.models.aggregates.assistant.Assistant;
import com.aixone.llm.domain.repositories.assistant.AssistantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import com.aixone.llm.domain.models.values.config.ToolConfig;
import com.aixone.llm.domain.models.entities.thread.Thread;

@Repository
@RequiredArgsConstructor
public class AssistantRepositoryR2dbcImpl implements AssistantRepository {
    private final R2dbcEntityTemplate template;
    
    
    @Override
    public Mono<Assistant> save(Assistant assistant) {
        if (assistant.getId() == null) {
            return template.insert(Assistant.class).using(assistant);
        } else {
            return template.update(Assistant.class)
                .matching(Query.query(Criteria.where("id").is(assistant.getId())))
                .apply(Update.update("name", assistant.getName())) // 这里只更新name，实际应补全所有字段
                .then(findById(assistant.getId()));
        }
    }

    @Override
    public Mono<Assistant> findById(String id) {
        return template.selectOne(Query.query(Criteria.where("id").is(id)), Assistant.class);
    }

    @Override
    public Flux<Assistant> findAll() {
        return template.select(Assistant.class).all();
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return template.delete(Query.query(Criteria.where("id").is(id)), Assistant.class).then();
    }

    @Override
    public Mono<Void> delete(Assistant assistant) {
        // 假设Assistant有id字段
        return deleteById(assistant.getId());
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        return template.selectOne(Query.query(Criteria.where("id").is(id)), Assistant.class)
                .map(a -> true).defaultIfEmpty(false);
    }

    @Override
    public Flux<Assistant> findByUserId(String userId) {
        return template.select(Query.query(Criteria.where("user_id").is(userId)), Assistant.class);
    }

    @Override
    public Flux<Assistant> findByModelId(String modelId) {
        return template.select(Query.query(Criteria.where("model_id").is(modelId)), Assistant.class);
    }

    @Override
    public Flux<Thread> getThreads(String assistantId) {
        // TODO: 需注入ThreadRepository或用template查thread表
        return Flux.empty();
    }

    @Override
    public Mono<Thread> createThread(String assistantId, String userId, String title) {
        // TODO: 需注入ThreadRepository或用template插入thread表
        return Mono.empty();
    }

    @Override
    public Mono<Long> countActiveThreads(String assistantId) {
        // TODO: 需查thread表status=active
        return Mono.just(0L);
    }

    @Override
    public Mono<Assistant> updateToolConfig(String assistantId, java.util.List<ToolConfig> toolConfigs) {
        return template.update(Assistant.class)
            .matching(Query.query(Criteria.where("id").is(assistantId)))
            .apply(Update.update("toolConfigs", toolConfigs))
            .flatMap(rows -> findById(assistantId));
    }

    // 其余复杂方法可后续补充
} 