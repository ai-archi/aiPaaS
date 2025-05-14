package com.aixone.llm.application.model;



import com.aixone.llm.domain.models.model.ModelConfig;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateModelCommand extends ModelConfig {
    

    public ModelConfig toModelConfig() {
        return (ModelConfig) this;
    }
} 