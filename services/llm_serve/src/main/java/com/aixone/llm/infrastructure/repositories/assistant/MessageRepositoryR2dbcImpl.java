package com.aixone.llm.infrastructure.repositories.assistant;

import com.aixone.llm.domain.models.entities.message.Message;
import com.aixone.llm.domain.repositories.assistant.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class MessageRepositoryR2dbcImpl implements MessageRepository {
    private final R2dbcEntityTemplate template;

    @Override
    public Mono<Message> save(Message message) {
        if (message.getId() == null) {
            return template.insert(Message.class).using(message);
        } else {
            return template.update(Message.class)
                .matching(Query.query(Criteria.where("id").is(message.getId())))
                .apply(org.springframework.data.relational.core.query.Update.update("content", message.getContent()))
                .then(findById(message.getId()));
        }
    }

    @Override
    public Mono<Message> findById(String id) {
        return template.selectOne(Query.query(Criteria.where("id").is(id)), Message.class);
    }

    @Override
    public Flux<Message> findByThreadId(String threadId) {
        return template.select(Query.query(Criteria.where("thread_id").is(threadId)), Message.class);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return template.delete(Query.query(Criteria.where("id").is(id)), Message.class).then();
    }
}