package com.aixone.llm.domain.services;

import reactor.core.publisher.Mono;
import java.util.Map;

public interface ModerationService {
    Mono<Map<String, Object>> moderate(String input);
} 