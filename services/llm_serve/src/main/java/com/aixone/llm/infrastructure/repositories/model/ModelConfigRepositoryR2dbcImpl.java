package com.aixone.llm.infrastructure.repositories.model;

import com.aixone.llm.domain.models.aggregates.model_config.ModelConfig;
import com.aixone.llm.domain.repositories.model.ModelConfigRepository;
import com.aixone.llm.infrastructure.entity.ModelConfigEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;


@Repository
@RequiredArgsConstructor
public class ModelConfigRepositoryR2dbcImpl implements ModelConfigRepository {
    private final R2dbcEntityTemplate template;

    @Override
    public Mono<ModelConfig> save(ModelConfig config) {
        ModelConfigEntity entity = ModelConfigEntity.fromDomain(config);
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
            return template.insert(ModelConfigEntity.class).using(entity)
            .map(ModelConfigEntity::toDomain);
        } else {
            // 这里只更新name和description等简单字段，复杂字段如有变动建议全量更新
            return template.update(ModelConfigEntity.class)
                    .matching(Query.query(Criteria.where("id").is(entity.getId())))
                    .apply(org.springframework.data.relational.core.query.Update.update("name", entity.getName())
                            .set("description", entity.getDescription())
                            .set("provider_info_json", entity.getProviderInfoJson())
                            .set("capability_json", entity.getCapabilityJson())
                            .set("runtime_config_json", entity.getRuntimeConfigJson())
                            .set("billing_rule_json", entity.getBillingRuleJson())
                            .set("active", entity.getActive())
                            .set("tenant_id", entity.getTenantId())
                            .set("deleted", entity.getDeleted())
                    )
                    .then(findById(entity.getId()));
        }
    }

    @Override
    public Mono<ModelConfig> findById(String id) {
        return template.selectOne(Query.query(Criteria.where("id").is(id)), ModelConfigEntity.class)
                .map(ModelConfigEntity::toDomain);
    }

    @Override
    public Flux<ModelConfig> findAll() {
        return template.select(ModelConfigEntity.class).all()
                .map(ModelConfigEntity::toDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return template.delete(Query.query(Criteria.where("id").is(id)), ModelConfigEntity.class).then();
    }

    @Override
    public Mono<Void> delete(ModelConfig entity) {
        return template.delete(Query.query(Criteria.where("id").is(entity.getId())), ModelConfigEntity.class).then();
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        return template.exists(Query.query(Criteria.where("id").is(id)), ModelConfigEntity.class);
    }
}