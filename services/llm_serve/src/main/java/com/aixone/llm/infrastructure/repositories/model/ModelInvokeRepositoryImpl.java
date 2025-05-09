package com.aixone.llm.infrastructure.repositories.model;

import com.aixone.llm.domain.models.BaseModelResponse;
import com.aixone.llm.domain.models.chat.ChatResponse;
import com.aixone.llm.domain.models.completion.CompletionResponse;
import com.aixone.llm.domain.models.embedding.EmbeddingResponse;
import com.aixone.llm.domain.repositories.model.ModelInvokeRepository;
import com.aixone.llm.infrastructure.entity.ModelInvokeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class ModelInvokeRepositoryImpl implements ModelInvokeRepository {
    private final R2dbcEntityTemplate template;

    @Override
    public Mono<Void> saveInvokeRecord(String userId, String modelId, BaseModelResponse response, LocalDateTime invokeTime) {
        ModelInvokeEntity entity = new ModelInvokeEntity();
        entity.setUserId(userId);
        entity.setModelId(modelId);
        if (response instanceof ChatResponse) {
            ChatResponse chat = (ChatResponse) response;
            entity.setRequestId(chat.getId());
            entity.setResponse(chat.getResult());
            if (chat.getUsage() != null) {
                entity.setPromptTokens((long) chat.getUsage().getPrompt_tokens());
                entity.setCompletionTokens((long) chat.getUsage().getCompletion_tokens());
                entity.setUsedTokens((long) chat.getUsage().getTotal_tokens());
            }
            entity.setFinishReason(chat.getChoices() != null && !chat.getChoices().isEmpty() ? chat.getChoices().get(0).getFinish_reason() : null);
        } else if (response instanceof CompletionResponse) {
            CompletionResponse comp = (CompletionResponse) response;
            entity.setRequestId(comp.getId());
            entity.setResponse(comp.getResult());
            if (comp.getUsage() != null) {
                entity.setPromptTokens((long) comp.getUsage().getPromptTokens());
                entity.setCompletionTokens((long) comp.getUsage().getCompletionTokens());
                entity.setUsedTokens((long) comp.getUsage().getTotalTokens());
            }
            entity.setFinishReason(comp.getChoices() != null && !comp.getChoices().isEmpty() ? comp.getChoices().get(0).getFinishReason() : null);
        } else if (response instanceof EmbeddingResponse) {
            EmbeddingResponse emb = (EmbeddingResponse) response;
            entity.setRequestId(emb.getId());
            entity.setResponse(null); // 可根据 EmbeddingResponse 结构补充
            if (emb.getUsage() != null) {
                entity.setPromptTokens((long) emb.getUsage().getPromptTokens());
                entity.setUsedTokens((long) emb.getUsage().getTotalTokens());
            }
            entity.setFinishReason(null);
        }
        entity.setPrompt(null); // 可根据需要补充
        entity.setIsError(false); // 可根据业务补充
        entity.setErrorMessage(null);
        entity.setInvokeTime(invokeTime);
        entity.setCreatedAt(LocalDateTime.now());
        return template.insert(ModelInvokeEntity.class).using(entity).then();
    }

    @Override
    public Mono<Long> getUserTokenUsage(String userId, String modelId, LocalDateTime start, LocalDateTime end) {
        return template.select(Query.query(
                Criteria.where("user_id").is(userId)
                        .and("model_id").is(modelId)
                        .and("invoke_time").greaterThanOrEquals(start)
                        .and("invoke_time").lessThanOrEquals(end)
        ), ModelInvokeEntity.class)
        .map(ModelInvokeEntity::getUsedTokens)
        .reduce(0L, Long::sum);
    }

    @Override
    public Mono<Long> getRecentQPS(String modelId, int seconds) {
        LocalDateTime since = LocalDateTime.now().minusSeconds(seconds);
        return template.select(Query.query(
                Criteria.where("model_id").is(modelId)
                        .and("invoke_time").greaterThanOrEquals(since)
        ), ModelInvokeEntity.class)
        .count();
    }

    @Override
    public Mono<Long> getRecentLatency(String modelId, int seconds) {
        LocalDateTime since = LocalDateTime.now().minusSeconds(seconds);
        return template.select(Query.query(
                Criteria.where("model_id").is(modelId)
                        .and("invoke_time").greaterThanOrEquals(since)
        ), ModelInvokeEntity.class)
        .map(ModelInvokeEntity::getTotalTime)
        .reduce(0.0, Double::sum)
        .map(Double::longValue);
    }

    @Override
    public Mono<Double> getRecentErrorRate(String modelId, int seconds) {
        LocalDateTime since = LocalDateTime.now().minusSeconds(seconds);
        Mono<Long> total = template.select(Query.query(
                Criteria.where("model_id").is(modelId)
                        .and("invoke_time").greaterThanOrEquals(since)
        ), ModelInvokeEntity.class).count();
        Mono<Long> error = template.select(Query.query(
                Criteria.where("model_id").is(modelId)
                        .and("invoke_time").greaterThanOrEquals(since)
                        .and("is_error").is(true)
        ), ModelInvokeEntity.class).count();
        return Mono.zip(total, error).map(tuple -> tuple.getT1() == 0 ? 0.0 : tuple.getT2() * 1.0 / tuple.getT1());
    }

    @Override
    public Mono<Long> countTokensByModelId(String modelId, LocalDateTime start, LocalDateTime end) {
        return template.select(Query.query(
                Criteria.where("model_id").is(modelId)
                        .and("invoke_time").greaterThanOrEquals(start)
                        .and("invoke_time").lessThanOrEquals(end)
        ), ModelInvokeEntity.class)
        .map(ModelInvokeEntity::getUsedTokens)
        .reduce(0L, Long::sum);
    }

    @Override
    public Mono<Double> calculateAverageLatency(String modelId, LocalDateTime start, LocalDateTime end) {
        return template.select(Query.query(
                Criteria.where("model_id").is(modelId)
                        .and("invoke_time").greaterThanOrEquals(start)
                        .and("invoke_time").lessThanOrEquals(end)
        ), ModelInvokeEntity.class)
        .map(ModelInvokeEntity::getTotalTime)
        .collectList()
        .map(list -> list.isEmpty() ? 0.0 : list.stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
    }

    @Override
    public Mono<Double> calculateErrorRate(String modelId, LocalDateTime start, LocalDateTime end) {
        Mono<Long> total = template.select(Query.query(
                Criteria.where("model_id").is(modelId)
                        .and("invoke_time").greaterThanOrEquals(start)
                        .and("invoke_time").lessThanOrEquals(end)
        ), ModelInvokeEntity.class).count();
        Mono<Long> error = template.select(Query.query(
                Criteria.where("model_id").is(modelId)
                        .and("invoke_time").greaterThanOrEquals(start)
                        .and("invoke_time").lessThanOrEquals(end)
                        .and("is_error").is(true)
        ), ModelInvokeEntity.class).count();
        return Mono.zip(total, error).map(tuple -> tuple.getT1() == 0 ? 0.0 : tuple.getT2() * 1.0 / tuple.getT1());
    }
} 