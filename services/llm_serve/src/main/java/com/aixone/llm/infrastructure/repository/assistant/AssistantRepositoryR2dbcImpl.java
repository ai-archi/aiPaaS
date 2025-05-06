package com.aixone.llm.infrastructure.repository.assistant;

import com.aixone.llm.domain.models.aggregates.assistant.Assistant;
import com.aixone.llm.domain.repositories.assistant.AssistantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class AssistantRepositoryR2dbcImpl implements AssistantRepository {
    private final R2dbcEntityTemplate template;

    @Override
    public Mono<Assistant> save(Assistant assistant) {
        return template.insert(Assistant.class).using(assistant)
                .onErrorResume(e -> template.update(Assistant.class).using(assistant));
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

    // 其余复杂方法可后续补充
} 