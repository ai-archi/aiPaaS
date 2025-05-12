package com.aixone.llm.application.image;

import com.aixone.llm.domain.models.image.ImageRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 提交图片处理任务的命令。
 * 直接继承 ImageRequest，便于参数复用和扩展。
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SubmitImageTaskCommand extends ImageRequest {
    public ImageRequest toImageRequest() {
        return (ImageRequest) this;
    }
} 