package com.aixone.llm.application.command.completion;

import com.aixone.llm.domain.models.completion.CompletionRequest;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.EqualsAndHashCode;
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CompletionCommand extends CompletionRequest {

    public CompletionRequest toCompletionRequest() {
        return (CompletionRequest) this;
    }
} 