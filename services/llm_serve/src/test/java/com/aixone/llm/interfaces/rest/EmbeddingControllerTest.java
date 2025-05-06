package com.aixone.llm.interfaces.rest;

import com.aixone.llm.application.command.embedding.EmbeddingCommandHandler;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class EmbeddingControllerTest {
    @Test
    public void testCreateEmbedding() {
        EmbeddingCommandHandler mockHandler = mock(EmbeddingCommandHandler.class);
        EmbeddingController controller = new EmbeddingController(mockHandler);
        when(mockHandler.handle(any())).thenReturn(Mono.just(Map.of(
                "id", "embd-123456",
                "object", "embedding",
                "data", List.of(Map.of(
                        "index", 0,
                        "embedding", List.of(0.1, 0.2, 0.3),
                        "object", "embedding"
                ))
        )));
        Map<String, Object> request = Map.of("input", "test text");
        Map<String, Object> response = controller.createEmbedding(request).block();
        assertNotNull(response);
        assertEquals("embd-123456", response.get("id"));
        assertEquals("embedding", response.get("object"));
        List<?> data = (List<?>) response.get("data");
        assertNotNull(data);
        Map<?, ?> item = (Map<?, ?>) data.get(0);
        assertEquals(0, item.get("index"));
        assertEquals(List.of(0.1, 0.2, 0.3), item.get("embedding"));
        assertEquals("embedding", item.get("object"));
    }
} 