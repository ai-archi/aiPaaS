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
public class TTSResponse implements Serializable {
    private String request_id;
    private Output output;

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Output implements Serializable {
        private String finish_reason;
        private Audio audio;
    }

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Audio implements Serializable {
        private Long expires_at;
        private String data;
        private String id;
        private String url;
    }
} 