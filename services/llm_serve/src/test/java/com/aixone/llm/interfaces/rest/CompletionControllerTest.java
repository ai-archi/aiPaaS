package com.aixone.llm.interfaces.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aixone.llm.application.completion.CompletionCommand;
import com.aixone.llm.application.completion.CompletionCommandHandler;
import com.aixone.llm.domain.models.completion.CompletionResponse;

import reactor.core.publisher.Flux;

@ExtendWith(MockitoExtension.class)
public class CompletionControllerTest {
    @Mock
    private CompletionCommandHandler completionCommandHandler;

    @Test
    void testCompletions() {
        CompletionCommand command = CompletionCommand.builder()
                .model("deepseek-chat").prompt("你好").build();
        CompletionResponse mockResp = CompletionResponse.builder().id("1").object("text_completion").model("test-model").build();
        when(completionCommandHandler.handle(any())).thenReturn(Flux.just(mockResp));

        Flux<CompletionResponse> result = completionCommandHandler.handle(command);
        CompletionResponse response = result.blockFirst();
        assert response != null;
        assert "text_completion".equals(response.getObject());
    }
} 