package com.aixone.llm.interfaces.rest;

import com.aixone.llm.application.command.chat.ChatCommandHandler;
import com.aixone.llm.application.command.chat.ChatCompletionCommand;
import com.aixone.llm.domain.models.values.config.ModelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatCommandHandler chatCommandHandler;

    @PostMapping("/completions")
    public Mono<ModelResponse> chatCompletions(@RequestBody ChatCompletionCommand command) {
        // 这里直接调用命令处理器
        return Mono.fromSupplier(() -> chatCommandHandler.handle(command));
    }

    @PostMapping(value = "/completions/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<ModelResponse>> chatCompletionsStream(@RequestBody ChatCompletionCommand command) {
        // TODO: 调用流式命令处理器，返回流式响应
        // 这里暂时返回模拟数据，后续可对接真实流式服务
        return Flux.just(
                ServerSentEvent.builder(ModelResponse.builder().id("1").object("chat.completion.chunk").build()).build(),
                ServerSentEvent.builder(ModelResponse.builder().id("2").object("chat.completion.chunk").build()).build(),
                ServerSentEvent.builder(ModelResponse.builder().id("3").object("chat.completion.chunk").build()).build()
        );
    }
} 