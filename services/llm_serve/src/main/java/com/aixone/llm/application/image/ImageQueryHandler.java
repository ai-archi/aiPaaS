package com.aixone.llm.application.image;

import com.aixone.llm.domain.models.image.ImageResponse;
import com.aixone.llm.domain.services.ImageService;

import reactor.core.publisher.Mono;

/**
 * 图片处理查询处理器。
 * 负责调用领域服务获取图片任务结果。
 */
public class ImageQueryHandler {
    private final ImageService imageService;

    public ImageQueryHandler(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * 获取图片处理任务结果。
     */
    public Mono<ImageResponse> handle(GetImageTaskResultQuery query) {
        return imageService.getImageTaskResult(query.getTaskId());
    }
} 