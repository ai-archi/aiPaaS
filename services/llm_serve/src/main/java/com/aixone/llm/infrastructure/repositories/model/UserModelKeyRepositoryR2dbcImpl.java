package com.aixone.llm.infrastructure.repositories.model;

import java.util.UUID;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import com.aixone.llm.domain.models.model.UserModelKey;
import com.aixone.llm.domain.repositories.model.UserModelKeyRepository;
import com.aixone.llm.infrastructure.entity.UserModelKeyEntity;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class UserModelKeyRepositoryR2dbcImpl implements UserModelKeyRepository {
    private final R2dbcEntityTemplate template;

    @Override
    public Mono<UserModelKey> save(UserModelKey key) {
        UserModelKeyEntity entity = UserModelKeyEntity.fromDomain(key);
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
            return template.insert(UserModelKeyEntity.class).using(entity)
                    .map(UserModelKeyEntity::toDomain);
        } else {
            return template.update(UserModelKeyEntity.class)
                    .matching(Query.query(Criteria.where("id").is(entity.getId())))
                    .apply(org.springframework.data.relational.core.query.Update.update("owner_id", entity.getOwnerId())
                            .set("model_name", entity.getModelName())
                            .set("api_key", entity.getApiKey())
                            .set("updated_at", entity.getUpdatedAt())
                            .set("description", entity.getDescription())
                    )
                    .then(findById(entity.getId()));
        }
    }

    @Override
    public Mono<UserModelKey> findById(String id) {
        return template.selectOne(Query.query(Criteria.where("id").is(id)), UserModelKeyEntity.class)
                .map(UserModelKeyEntity::toDomain);
    }

    @Override
    public Flux<UserModelKey> findByOwnerId(String ownerId) {
        return template.select(Query.query(Criteria.where("owner_id").is(ownerId)), UserModelKeyEntity.class)
                .map(UserModelKeyEntity::toDomain);
    }

    @Override
    public Flux<UserModelKey> findByModelName(String modelName) {
        return template.select(Query.query(Criteria.where("model_name").is(modelName)), UserModelKeyEntity.class)
                .map(UserModelKeyEntity::toDomain);
    }

    @Override
    public Mono<UserModelKey> findByOwnerIdAndModelName(String ownerId, String modelName) {
        return template.selectOne(Query.query(Criteria.where("owner_id").is(ownerId).and("model_name").is(modelName)), UserModelKeyEntity.class)
                .map(UserModelKeyEntity::toDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return template.delete(Query.query(Criteria.where("id").is(id)), UserModelKeyEntity.class).then();
    }
} 