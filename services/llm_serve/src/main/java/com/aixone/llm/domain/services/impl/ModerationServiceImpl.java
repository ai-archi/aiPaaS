package com.aixone.llm.domain.services.impl;

import com.aixone.llm.domain.services.ModerationService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.Map;

@Service
public class ModerationServiceImpl implements ModerationService {
    @Override
    public Mono<Map<String, Object>> moderate(String input) {
        // 简单内容审核：包含"违规"则flagged为true
        boolean flagged = input != null && input.contains("违规");
        return Mono.just(Map.of(
                "flagged", flagged,
                "categories", Map.of(
                        "hate", false,
                        "violence", false
                ),
                "category_scores", Map.of(
                        "hate", 0.01,
                        "violence", 0.02
                )
        ));
    }
} 