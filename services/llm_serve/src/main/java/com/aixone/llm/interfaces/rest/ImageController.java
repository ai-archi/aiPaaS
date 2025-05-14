package com.aixone.llm.interfaces.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aixone.llm.application.image.GetImageTaskResultQuery;
import com.aixone.llm.application.image.ImageCommandHandler;
import com.aixone.llm.application.image.ImageQueryHandler;
import com.aixone.llm.application.image.SubmitImageTaskCommand;
import com.aixone.llm.domain.models.image.ImageResponse;
import com.aixone.llm.domain.models.image.ImageTaskResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
/**
 * 图片处理REST接口。
 * 支持图片生成、编辑、变体等操作，参考OpenAI图片API。
 */
@RestController
@RequestMapping("/v1/{tenantId}/images")
@RequiredArgsConstructor
public class ImageController {
    private final ImageCommandHandler imageCommandHandler;
    private final ImageQueryHandler imageQueryHandler;

    /**
     * 文本生成图片接口。
     * type: generation | edit | variation
     * function: 具体编辑功能（可选）
     * prompt: 文本提示词
     * baseImageUrl: 原图URL（编辑/变体时必填）
     * parameters: 其他参数
     */
    @PostMapping(value = "/generation", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ImageResponse> createImage(@PathVariable("tenantId") String tenantId, @RequestBody SubmitImageTaskCommand command) {
        command.setType("generation");
        // 可根据需要设置tenantId到command
        return imageCommandHandler.handle(command);
    }

    /**
     * 编辑图片接口。
     * type: edit
     * function: 具体编辑功能（如inpainting、doodle等）
     * prompt: 编辑提示词
     * baseImageUrl: 原图URL（必填）
     * parameters: 其他参数
     */
    @PostMapping(value = "/edits", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ImageResponse> editImage(@PathVariable("tenantId") String tenantId, @RequestBody SubmitImageTaskCommand command) {
        command.setType("edit");
        return imageCommandHandler.handle(command);
    }

    // /**
    //  * 生成图片变体接口。
    //  * type: variation
    //  * function: 变体相关功能（可选）
    //  * prompt: 变体提示词（可选）
    //  * baseImageUrl: 原图URL（必填）
    //  * parameters: 其他参数
    //  */
    // @PostMapping(value = "/variations", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    // public Mono<ImageResponse> createVariation(@PathVariable("tenantId") String tenantId, @RequestBody SubmitImageTaskCommand command) {
    //     command.setType("variation");
    //     return imageCommandHandler.handle(command);
    // }
    /**
     * 查询图片处理任务结果。
     * @param tenantId 租户ID
     * @param taskId 任务ID
     */
    @GetMapping(value = "/task/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ImageTaskResponse> getImageResult(@PathVariable("tenantId") String tenantId,
            @PathVariable String taskId,@RequestParam String model) {
        GetImageTaskResultQuery query = new GetImageTaskResultQuery();
        query.setTaskId(taskId);
        query.setModel(model);
        return imageQueryHandler.handle(query);
    }


} 