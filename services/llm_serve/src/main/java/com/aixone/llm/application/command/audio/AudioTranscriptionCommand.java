package com.aixone.llm.application.command.audio;

import com.aixone.llm.domain.models.values.config.ModelRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AudioTranscriptionCommand  {
    private MultipartFile file;
    private String language;
    private String responseFormat;
    private Double temperature;
    private Integer timestampGranularities;
    public AudioTranscriptionCommand(MultipartFile file) {
        this.setFile(file);
    }
    public ModelRequest toModelRequest() {
        return  ModelRequest.builder()
                .build();
    }
} 