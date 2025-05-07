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
import com.aixone.llm.application.command.moderation.ModerationCommand;
@RestController
@RequestMapping("/v1/{tenantId}/moderations")
@RequiredArgsConstructor
public class ModerationController {
    private final ModerationCommandHandler moderationCommandHandler;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> moderate(@RequestBody ModerationCommand command) {
        return moderationCommandHandler.handle(command);
    }
} 