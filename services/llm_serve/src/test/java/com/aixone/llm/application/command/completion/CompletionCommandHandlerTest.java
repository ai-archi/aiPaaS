package com.aixone.llm.application.command.completion;

import com.aixone.llm.domain.models.completion.CompletionResponse;
import com.aixone.llm.domain.services.CompletionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

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
        CompletionResponse expected = mock(CompletionResponse.class);
        when(completionService.completion(command.toCompletionRequest())).thenReturn(Flux.just(expected));

        Flux<CompletionResponse> result = handler.handle(command);
        StepVerifier.create(result)
            .expectNext(expected)
            .verifyComplete();
        verify(completionService, times(1)).completion(command.toCompletionRequest());
    }
}
