package com.aixone.llm.domain.services;

import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

public interface AudioService {
    Mono<String> transcribe(MultipartFile file);
    Mono<String> translate(MultipartFile file);
} 