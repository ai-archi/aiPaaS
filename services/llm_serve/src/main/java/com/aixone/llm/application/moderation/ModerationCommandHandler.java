package com.aixone.llm.application.moderation;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.aixone.llm.domain.services.ModerationService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
@Component
@RequiredArgsConstructor
public class ModerationCommandHandler {
    private final ModerationService moderationService;

    public Mono<Map<String, Object>> handle(ModerationCommand command   ) {
        String input = command.getInput();
        return moderationService.moderate(input)
                .map(result -> Map.of(
                        "id", "modr-" + LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                        "model", command.getModel(),
                        "results", List.of(result)
                ));
    }
} 