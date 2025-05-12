package com.aixone.llm.application.image;

import com.aixone.llm.domain.models.image.ImageRequest;
import com.aixone.llm.domain.models.image.ImageResponse;
import com.aixone.llm.domain.services.ImageService;

import reactor.core.publisher.Mono;

/**
 * 图片处理命令处理器。
 * 负责调用领域服务提交任务和获取结果。
 */
public class ImageCommandHandler {
    private final ImageService imageService;

    public ImageCommandHandler(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * 提交图片处理任务。
     */
    public Mono<ImageResponse> handle(SubmitImageTaskCommand command) {
        ImageRequest request = command.toImageRequest();
        return imageService.submitImageTask(request);
    }
} 