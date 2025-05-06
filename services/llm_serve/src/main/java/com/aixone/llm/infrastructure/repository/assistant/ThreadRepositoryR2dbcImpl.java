package com.aixone.llm.infrastructure.repository.assistant;

import com.aixone.llm.domain.models.entities.thread.Thread;
import com.aixone.llm.domain.repositories.assistant.ThreadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class ThreadRepositoryR2dbcImpl implements ThreadRepository {
    private final R2dbcEntityTemplate template;

    @Override
    public Mono<Thread> save(Thread thread) {
        return template.insert(Thread.class).using(thread)
                .onErrorResume(e -> template.update(Thread.class).using(thread));
    }

    @Override
    public Mono<Thread> findById(String id) {
        return template.selectOne(Query.query(Criteria.where("id").is(id)), Thread.class);
    }

    @Override
    public Flux<Thread> findByAssistantId(String assistantId) {
        return template.select(Query.query(Criteria.where("assistant_id").is(assistantId)), Thread.class);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return template.delete(Query.query(Criteria.where("id").is(id)), Thread.class).then();
    }
} 