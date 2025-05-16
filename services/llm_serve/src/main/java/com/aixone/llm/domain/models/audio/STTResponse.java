package com.aixone.llm.domain.models.audio;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class STTResponse implements Serializable {
    private String request_id;
    private Output output;

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Output implements Serializable {
        private String text;
        private String task_id;
        private String task_status;
    }
} 