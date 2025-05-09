package com.aixone.llm.infrastructure.repositories.assistant;

import com.aixone.llm.domain.models.thread.Thread;
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
        if (thread.getId() == null) {
            return template.insert(Thread.class).using(thread);
        } else {
            return template.update(Thread.class)
                .matching(Query.query(Criteria.where("id").is(thread.getId())))
                .apply(org.springframework.data.relational.core.query.Update.update("title", thread.getTitle()))
                .then(findById(thread.getId()));
        }
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