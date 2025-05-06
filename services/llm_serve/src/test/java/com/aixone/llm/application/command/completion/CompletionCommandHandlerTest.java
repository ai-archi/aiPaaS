package com.aixone.llm.application.command.completion;

import com.aixone.llm.domain.models.values.config.ModelResponse;
import com.aixone.llm.domain.services.CompletionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CompletionCommandHandlerTest {
    private CompletionService completionService;
    private CompletionCommandHandler handler;

    @BeforeEach
    public void setUp() {
        completionService = mock(CompletionService.class);
        handler = new CompletionCommandHandler(completionService);
    }

    @Test
    public void testHandle_ReturnsModelResponse() {
        CompletionCommand command = mock(CompletionCommand.class);
        ModelResponse expected = mock(ModelResponse.class);
        when(completionService.completion(command)).thenReturn(Mono.just(expected));

        Mono<ModelResponse> result = handler.handle(command);
        StepVerifier.create(result)
            .expectNext(expected)
            .verifyComplete();
        verify(completionService, times(1)).completion(command);
    }
}
