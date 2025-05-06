package com.aixone.llm.application.command.audio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AudioTranscriptionCommand {
    private MultipartFile file;
} 