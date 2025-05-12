package com.aixone.llm.application.completion;

import com.aixone.llm.domain.models.completion.CompletionRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CompletionCommand extends CompletionRequest {

    public CompletionRequest toCompletionRequest() {
        return (CompletionRequest) this;
    }
} 