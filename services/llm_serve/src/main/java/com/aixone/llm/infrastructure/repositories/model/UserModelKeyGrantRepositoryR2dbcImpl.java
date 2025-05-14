package com.aixone.llm.infrastructure.repositories.model;

import java.util.UUID;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import com.aixone.llm.domain.models.model.UserModelKeyGrant;
import com.aixone.llm.domain.repositories.model.UserModelKeyGrantRepository;
import com.aixone.llm.infrastructure.entity.UserModelKeyGrantEntity;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class UserModelKeyGrantRepositoryR2dbcImpl implements UserModelKeyGrantRepository {
    private final R2dbcEntityTemplate template;

    @Override
    public Mono<UserModelKeyGrant> save(UserModelKeyGrant grant) {
        UserModelKeyGrantEntity entity = UserModelKeyGrantEntity.fromDomain(grant);
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
            return template.insert(UserModelKeyGrantEntity.class).using(entity)
                    .map(UserModelKeyGrantEntity::toDomain);
        } else {
            return template.update(UserModelKeyGrantEntity.class)
                    .matching(Query.query(Criteria.where("id").is(entity.getId())))
                    .apply(org.springframework.data.relational.core.query.Update.update("key_id", entity.getKeyId())
                            .set("grantee_id", entity.getGranteeId())
                            .set("charge_type", entity.getChargeType())
                            .set("price", entity.getPrice())
                            .set("price_unit", entity.getPriceUnit())
                            .set("updated_at", entity.getUpdatedAt())
                            .set("description", entity.getDescription())
                    )
                    .then(findById(entity.getId()));
        }
    }

    @Override
    public Mono<UserModelKeyGrant> findById(String id) {
        return template.selectOne(Query.query(Criteria.where("id").is(id)), UserModelKeyGrantEntity.class)
                .map(UserModelKeyGrantEntity::toDomain);
    }

    @Override
    public Flux<UserModelKeyGrant> findByGranteeId(String granteeId) {
        return template.select(Query.query(Criteria.where("grantee_id").is(granteeId)), UserModelKeyGrantEntity.class)
                .map(UserModelKeyGrantEntity::toDomain);
    }

    @Override
    public Flux<UserModelKeyGrant> findByKeyId(String keyId) {
        return template.select(Query.query(Criteria.where("key_id").is(keyId)), UserModelKeyGrantEntity.class)
                .map(UserModelKeyGrantEntity::toDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return template.delete(Query.query(Criteria.where("id").is(id)), UserModelKeyGrantEntity.class).then();
    }
} 