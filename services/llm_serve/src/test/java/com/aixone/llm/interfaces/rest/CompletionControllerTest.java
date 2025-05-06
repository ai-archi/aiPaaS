package com.aixone.llm.interfaces.rest;

import com.aixone.llm.application.command.completion.CompletionCommand;
import com.aixone.llm.domain.models.values.config.ModelResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.mockito.Mockito.*;
import reactor.core.publisher.Mono;

@WebFluxTest(CompletionController.class)
public class CompletionControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private com.aixone.llm.application.command.completion.CompletionCommandHandler completionCommandHandler;

    @Test
    void testCompletions() {
        CompletionCommand command = CompletionCommand.builder()
                .model("deepseek-chat").prompt("你好").build();
        ModelResponse mockResp = ModelResponse.builder().id("1").object("text_completion").model("test-model").build();
        when(completionCommandHandler.handle(any())).thenReturn(Mono.just(mockResp));

        webTestClient.post().uri("/v1/completions")
                .bodyValue(command)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.object").isEqualTo("text_completion");
    }
} 