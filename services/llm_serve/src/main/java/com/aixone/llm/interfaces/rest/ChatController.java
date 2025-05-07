package com.aixone.llm.interfaces.rest;

import com.aixone.llm.application.command.chat.ChatCompletionCommandHandler;
import com.aixone.llm.application.command.chat.ChatCompletionCommand;
import com.aixone.llm.domain.models.values.config.ModelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/{tenantId}/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatCompletionCommandHandler chatCommandHandler;

    @PostMapping("/completions")
    public Mono<ModelResponse> chatCompletions(@RequestBody ChatCompletionCommand command) {
        // 这里直接调用命令处理器
        // TODO: 调用流式命令处理器，返回流式响应
        return Mono.fromSupplier(() -> chatCommandHandler.handle(command));
    }
} 