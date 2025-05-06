package com.aixone.llm.domain.services.impl;

import com.aixone.llm.domain.services.ModerationService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.Map;

@Service
public class ModerationServiceImpl implements ModerationService {
    @Override
    public Mono<Map<String, Object>> moderate(String input) {
        // TODO: 调用真实大模型API进行内容审核
        return Mono.just(Map.of(
                "flagged", false,
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