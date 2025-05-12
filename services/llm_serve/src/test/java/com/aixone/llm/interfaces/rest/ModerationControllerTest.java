package com.aixone.llm.interfaces.rest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.aixone.llm.application.moderation.ModerationCommand;
import com.aixone.llm.application.moderation.ModerationCommandHandler;

import reactor.core.publisher.Mono;
public class ModerationControllerTest {
    @Test
    public void testModerate() {
        ModerationCommandHandler mockHandler = mock(ModerationCommandHandler.class);
        ModerationController controller = new ModerationController(mockHandler);
        when(mockHandler.handle(any())).thenReturn(Mono.just(Map.of(
                "id", "modr-123456",
                "model", "moderation-test",
                "results", List.of(Map.of(
                        "flagged", false,
                        "categories", Map.of(
                                "hate", false,
                                "violence", false
                        ),
                        "category_scores", Map.of(
                                "hate", 0.01,
                                "violence", 0.02
                        )
                ))
        )));
        ModerationCommand command = new ModerationCommand();
        command.setInput("test text");
        Map<String, Object> response = controller.moderate(command).block();
        assertNotNull(response);
        assertEquals("modr-123456", response.get("id"));
        assertEquals("moderation-test", response.get("model"));
        List<?> results = (List<?>) response.get("results");
        assertNotNull(results);
        Map<?, ?> result = (Map<?, ?>) results.get(0);
        assertEquals(false, result.get("flagged"));
        Map<?, ?> categories = (Map<?, ?>) result.get("categories");
        assertEquals(false, categories.get("hate"));
        Map<?, ?> categoryScores = (Map<?, ?>) result.get("category_scores");
        assertEquals(0.01, (Double) categoryScores.get("hate"), 1e-6);
    }
} 