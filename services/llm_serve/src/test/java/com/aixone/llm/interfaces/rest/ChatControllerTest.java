package com.aixone.llm.interfaces.rest;

import com.aixone.llm.application.command.chat.ChatCompletionCommand;
import com.aixone.llm.domain.models.values.config.ModelResponse;
import com.aixone.llm.domain.models.entities.message.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.mockito.Mockito.*;
import java.util.Collections;
import reactor.core.publisher.Flux;
@WebFluxTest(ChatController.class)
public class ChatControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private com.aixone.llm.application.command.chat.ChatCompletionCommandHandler chatCommandHandler;

    @Test
    void testChatCompletions() {
        Message msg = Message.builder()
                .role("user").content("你好").build();
        ChatCompletionCommand command = ChatCompletionCommand.builder()
                .model("test-model").messages(Collections.singletonList(msg)).build();
        ModelResponse mockResp = ModelResponse.builder().id("1").object("chat.completion").model("test-model").build();
        when(chatCommandHandler.handle(any(ChatCompletionCommand.class))).thenReturn(Flux.just(mockResp));

        webTestClient.post().uri("/v1/chat/completions")
                .bodyValue(command)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.object").isEqualTo("chat.completion");
    }
} 