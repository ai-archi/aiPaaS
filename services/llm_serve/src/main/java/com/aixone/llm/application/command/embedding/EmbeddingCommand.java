package com.aixone.llm.application.command.embedding;

import com.aixone.llm.domain.models.embedding.EmbeddingRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 向量生成命令对象，作为接口层DTO，组合 EmbeddingRequest
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddingCommand {
    private String user;
    private List<String> input;
    private String model;

    public EmbeddingRequest toEmbeddingRequest() {
        return EmbeddingRequest.builder()
                .user(user)
                .input(input)
                .model(model)
                .build();
    }
}
