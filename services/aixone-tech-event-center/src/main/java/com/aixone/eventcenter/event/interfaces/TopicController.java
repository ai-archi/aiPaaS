package com.aixone.eventcenter.event.interfaces;

import com.aixone.common.api.ApiResponse;
import com.aixone.common.exception.NotFoundException;
import com.aixone.common.util.ValidationUtils;
import com.aixone.eventcenter.event.application.TopicApplicationService;
import com.aixone.eventcenter.event.domain.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.aixone.common.session.SessionContext;

import java.util.List;
import java.util.Optional;

/**
 * Topic管理接口控制器
 * /api/v1/topics
 */
@RestController
@RequestMapping("/api/v1/topics")
public class TopicController {
    
    @Autowired
    private TopicApplicationService topicApplicationService;

    /**
     * 注册新Topic
     */
    @PostMapping("/register")
    public ApiResponse<Topic> registerTopic(@RequestBody TopicRegisterRequest request) {
        ValidationUtils.notNull(SessionContext.getTenantId(), "缺少租户ID");
        ValidationUtils.notNull(request, "请求不能为空");
        ValidationUtils.notBlank(request.getName(), "Topic名称不能为空");
        ValidationUtils.notBlank(request.getOwner(), "Owner不能为空");
        
        Topic topic = topicApplicationService.registerTopic(
                request.getName(),
                request.getOwner(),
                request.getDescription(),
                SessionContext.getTenantId()
        );
        
        return ApiResponse.success(topic);
    }

    /**
     * 查询所有Topic
     */
    @GetMapping
    public ApiResponse<List<Topic>> getAllTopics() {
        return ApiResponse.success(topicApplicationService.getAllTopics());
    }

    /**
     * 根据租户查询Topic
     */
    @GetMapping("/tenant")
    public ApiResponse<List<Topic>> getTopicsByTenant() {
        ValidationUtils.notNull(SessionContext.getTenantId(), "缺少租户ID");
        
        return ApiResponse.success(topicApplicationService.getTopicsByTenant(SessionContext.getTenantId()));
    }

    /**
     * 根据名称查询Topic
     */
    @GetMapping("/{name}")
    public ApiResponse<Topic> getTopicByName(@PathVariable String name) {
        ValidationUtils.notBlank(name, "Topic名称不能为空");
        
        return topicApplicationService.getTopicByName(name)
                .map(ApiResponse::success)
                .orElseThrow(() -> new NotFoundException("Topic不存在: " + name));
    }

    /**
     * 根据状态查询Topic
     */
    @GetMapping("/status/{status}")
    public ApiResponse<List<Topic>> getTopicsByStatus(@PathVariable Topic.TopicStatus status) {
        return ApiResponse.success(topicApplicationService.getTopicsByStatus(status));
    }

    /**
     * 激活Topic
     */
    @PostMapping("/{name}/activate")
    public ApiResponse<Void> activateTopic(@PathVariable String name) {
        topicApplicationService.activateTopic(name);
        return ApiResponse.success(null);
    }

    /**
     * 停用Topic
     */
    @PostMapping("/{name}/deactivate")
    public ApiResponse<Void> deactivateTopic(@PathVariable String name) {
        topicApplicationService.deactivateTopic(name);
        return ApiResponse.success(null);
    }

    /**
     * 更新Topic描述
     */
    @PutMapping("/{name}/description")
    public ApiResponse<Void> updateTopicDescription(@PathVariable String name, @RequestBody TopicDescriptionRequest request) {
        topicApplicationService.updateTopicDescription(name, request.getDescription());
        return ApiResponse.success(null);
    }

    /**
     * 删除Topic
     */
    @DeleteMapping("/{name}")
    public ApiResponse<Void> deleteTopic(@PathVariable String name) {
        topicApplicationService.deleteTopic(name);
        return ApiResponse.success(null);
    }

    /**
     * Topic注册请求DTO
     */
    public static class TopicRegisterRequest {
        private String name;
        private String owner;
        private String description;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getOwner() { return owner; }
        public void setOwner(String owner) { this.owner = owner; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    /**
     * Topic描述更新请求DTO
     */
    public static class TopicDescriptionRequest {
        private String description;

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
