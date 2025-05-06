package com.aixone.llm.application.command.moderation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;
import com.aixone.llm.domain.services.ModerationService;

@Component
@RequiredArgsConstructor
public class ModerationCommandHandler {
    private final ModerationService moderationService;

    public Mono<Map<String, Object>> handle(Map<String, Object> request) {
        String input = (String) request.getOrDefault("input", "");
        return moderationService.moderate(input)
                .map(result -> Map.of(
                        "id", "modr-" + System.currentTimeMillis(),
                        "model", "moderation-test",
                        "results", List.of(result)
                ));
    }
} 