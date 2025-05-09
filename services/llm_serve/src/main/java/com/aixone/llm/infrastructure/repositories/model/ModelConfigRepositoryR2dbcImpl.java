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
            // 只更新必要字段
            return template.update(ModelConfigEntity.class)
                    .matching(Query.query(Criteria.where("id").is(entity.getId())))
                    .apply(org.springframework.data.relational.core.query.Update.update("name", entity.getName())
                            .set("endpoint", entity.getEndpoint())
                            .set("api_key", entity.getApiKey())
                            .set("max_tokens", entity.getMaxTokens())
                            .set("min_input_price", entity.getMinInputPrice())
                            .set("min_output_price", entity.getMinOutputPrice())
                            .set("support_text_generation", entity.getSupportTextGeneration())
                            .set("support_image_generation", entity.getSupportImageGeneration())
                            .set("support_speech_generation", entity.getSupportSpeechGeneration())
                            .set("support_video_generation", entity.getSupportVideoGeneration())
                            .set("support_vector", entity.getSupportVector())
                            .set("active", entity.getActive())
                            .set("description", entity.getDescription())
                            .set("tenant_id", entity.getTenantId())
                            .set("is_system_preset", entity.getIsSystemPreset())
                            .set("updated_at", entity.getUpdatedAt())
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

    @Override
    public Mono<ModelConfig> findByName(String name) {
        return template.selectOne(Query.query(Criteria.where("name").is(name)), ModelConfigEntity.class)
                .map(ModelConfigEntity::toDomain);
    }
}