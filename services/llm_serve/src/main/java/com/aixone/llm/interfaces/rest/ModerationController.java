package com.aixone.llm.interfaces.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import java.util.Map;
import com.aixone.llm.application.command.moderation.ModerationCommandHandler;

@RestController
@RequestMapping("/v1/moderations")
@RequiredArgsConstructor
public class ModerationController {
    private final ModerationCommandHandler moderationCommandHandler;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> moderate(@RequestBody Map<String, Object> request) {
        return moderationCommandHandler.handle(request);
    }
} 