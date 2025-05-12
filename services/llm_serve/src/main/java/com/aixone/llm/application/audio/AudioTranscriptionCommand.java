package com.aixone.llm.application.audio;


import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

} 