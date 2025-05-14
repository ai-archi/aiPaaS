package com.aixone.llm.interfaces.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.aixone.llm.application.model.CreateModelCommand;
import com.aixone.llm.application.model.ModelCommandHandler;
import com.aixone.llm.application.model.ModelQuery;
import com.aixone.llm.application.model.ModelQueryHandler;
import com.aixone.llm.application.model.UpdateModelCommand;
import com.aixone.llm.domain.models.model.ModelConfig;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 模型管理REST接口
 * 支持模型的增删改查、激活/停用等操作
 */
@RestController
@RequestMapping("/v1/{tenantId}/models")
@RequiredArgsConstructor
public class ModelController {
    private final ModelCommandHandler commandHandler;
    private final ModelQueryHandler queryHandler;
    
    /**
     * 创建模型
     * @param tenantId 租户ID（路径参数）
     * @param command  创建模型请求体
     * @return 创建后的模型配置
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ModelConfig> createModel(@PathVariable("tenantId") String tenantId, @RequestBody CreateModelCommand command) {
        command.setTenantId(tenantId);
        return commandHandler.handleCreateModel(command);
    }
    
    /**
     * 更新模型
     * @param tenantId 租户ID（路径参数）
     * @param modelId  模型ID（路径参数）
     * @param command  更新模型请求体
     * @return 更新后的模型配置
     */
    @PutMapping("/{modelId}")
    public Mono<ModelConfig> updateModel(@PathVariable("tenantId") String tenantId, @PathVariable String modelId, @RequestBody UpdateModelCommand command) {
        command.setId(modelId);
        command.setTenantId(tenantId);
        return commandHandler.handleUpdateModel(command);
    }
    
    /**
     * 删除模型
     * @param tenantId 租户ID
     * @param modelId  模型ID
     */
    @DeleteMapping("/{modelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteModel(@PathVariable("tenantId") String tenantId, @PathVariable String modelId) {
        return commandHandler.handleDeleteModel(modelId);
    }
    
    /**
     * 查询单个模型详情
     * @param tenantId 租户ID
     * @param modelId  模型ID
     * @return 模型配置详情
     */
    @GetMapping("/{modelId}")
    public Mono<ModelConfig> getModel(@PathVariable("tenantId") String tenantId, @PathVariable String modelId) {
        return queryHandler.handleGetModel(modelId);
    }
    
    /**
     * 查询模型列表
     * @param tenantId 租户ID
     * @param query    查询参数
     * @return 模型配置列表
     */
    @GetMapping
    public Flux<ModelConfig> listModels(@PathVariable("tenantId") String tenantId, @ModelAttribute ModelQuery query) {
        return queryHandler.handleQuery(query);
    }
    
    /**
     * 激活模型
     * @param tenantId 租户ID
     * @param modelId  模型ID
     * @return 激活后的模型配置
     */
    @PostMapping("/{modelId}/activate")
    public Mono<ModelConfig> activateModel(@PathVariable("tenantId") String tenantId, @PathVariable String modelId) {
        return commandHandler.handleActivateModel(modelId);
    }
    
    /**
     * 停用模型
     * @param tenantId 租户ID
     * @param modelId  模型ID
     * @return 停用后的模型配置
     */
    @PostMapping("/{modelId}/deactivate")
    public Mono<ModelConfig> deactivateModel(@PathVariable("tenantId") String tenantId, @PathVariable String modelId) {
        return commandHandler.handleDeactivateModel(modelId);
    }
} 