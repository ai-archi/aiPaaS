package com.aixone.llm.domain.services.impl;

import com.aixone.llm.domain.services.AudioService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@Service
public class AudioServiceImpl implements AudioService {
    @Override
    public Mono<String> transcribe(MultipartFile file) {
        // TODO: 实现音频转写逻辑
        return Mono.just("{\"text\":\"(mock) 音频转写结果\"}");
    }

    @Override
    public Mono<String> translate(MultipartFile file) {
        // TODO: 实现音频翻译逻辑
        return Mono.just("{\"translation\":\"(mock) 音频翻译结果\"}");
    }
} 