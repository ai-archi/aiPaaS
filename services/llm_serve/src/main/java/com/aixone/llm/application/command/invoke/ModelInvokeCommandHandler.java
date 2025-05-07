package com.aixone.llm.application.command.invoke;

import com.aixone.llm.domain.models.values.config.ModelRequest;
import com.aixone.llm.domain.models.values.config.ModelResponse;
import com.aixone.llm.domain.services.ModelInvokeService;
import com.aixone.llm.domain.services.QuotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ModelInvokeCommandHandler {
    private final ModelInvokeService modelInvokeService;
    private final QuotaService quotaService;

    public Mono<ModelResponse> handleInvoke(ModelInvokeCommand command) {
        ModelRequest request = command.toModelRequest();
        return quotaService.checkAndReserveQuota(command.getUserId(), command.getModelId())
            .then(modelInvokeService.invoke(request))
            .doOnSuccess(response -> {
                Long usedTokens = response.getUsage() != null ? (long) response.getUsage().getTotalTokens() : 0L;
                quotaService.consumeQuota(
                    command.getUserId(),
                    command.getModelId(),
                    usedTokens
                ).subscribe();
            });
    }

    public Flux<ModelResponse> handleStreamInvoke(ModelInvokeCommand command) {
        ModelRequest request = command.toModelRequest();
        return quotaService.checkAndReserveQuota(command.getUserId(), command.getModelId())
            .thenMany(modelInvokeService.streamInvoke(request))
            .doOnComplete(() ->
                modelInvokeService.getUsedTokens(command.getUserId(), command.getModelId())
                    .flatMap(tokens ->
                        quotaService.consumeQuota(
                            command.getUserId(),
                            command.getModelId(),
                            tokens
                        )
                    )
                    .subscribe()
            );
    }
} 