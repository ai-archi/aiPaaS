package com.aixone.llm.interfaces.rest;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.aixone.llm.application.chat.ChatCompletionCommand;
import com.aixone.llm.application.chat.ChatCompletionCommandHandler;
import com.aixone.llm.domain.models.chat.ChatResponse;
import com.aixone.llm.domain.models.chat.Message;

import reactor.core.publisher.Flux;

@ExtendWith(MockitoExtension.class)
public class ChatControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @Mock
    private ChatCompletionCommandHandler chatCommandHandler;


    @Test
    void testChatCompletions() {
        Message msg = Message.builder()
                .role("user").content("你好").build();
        ChatCompletionCommand command = ChatCompletionCommand.builder()
                .model("test-model").messages(Collections.singletonList(msg)).build();
        ChatResponse mockResp = ChatResponse.builder().id("1").object("chat.completion").model("test-model").build();
        when(chatCommandHandler.handle(any(ChatCompletionCommand.class))).thenReturn(Flux.just(mockResp));

        webTestClient.post().uri("/v1/chat/completions")
                .bodyValue(command)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.object").isEqualTo("chat.completion");
    }
} 