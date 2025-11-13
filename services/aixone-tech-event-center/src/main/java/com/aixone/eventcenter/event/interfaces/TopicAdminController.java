package com.aixone.eventcenter.event.interfaces;

import com.aixone.common.api.ApiResponse;
import com.aixone.eventcenter.event.application.TopicApplicationService;
import com.aixone.eventcenter.event.domain.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Topic管理接口控制器（管理员接口，支持跨租户操作）
 * /api/v1/admin/topics
 */
@RestController
@RequestMapping("/api/v1/admin/topics")
public class TopicAdminController {
    private static final Logger logger = LoggerFactory.getLogger(TopicAdminController.class);
    
    @Autowired
    private TopicApplicationService topicApplicationService;

    /**
     * 管理员查询Topic列表（可跨租户）
     */
    @GetMapping
    public ApiResponse<List<Topic>> getTopics(
            @RequestParam(required = false) String tenantId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Topic.TopicStatus status) {
        
        if (!StringUtils.hasText(tenantId)) {
            return ApiResponse.error(40001, "tenantId参数不能为空");
        }
        
        logger.info("管理员查询Topic列表: tenantId={}, name={}, status={}", tenantId, name, status);
        
        List<Topic> topics;
        if (status != null) {
            topics = topicApplicationService.getTopicsByStatus(status);
        } else if (StringUtils.hasText(name)) {
            Optional<Topic> topicOpt = topicApplicationService.getTopicByName(name);
            topics = topicOpt.map(List::of).orElse(List.of());
        } else {
            topics = topicApplicationService.getTopicsByTenant(tenantId);
        }
        
        return ApiResponse.success(topics);
    }

    /**
     * 管理员查询Topic详情（可跨租户）
     */
    @GetMapping("/{topicName}")
    public ApiResponse<Topic> getTopicByName(
            @PathVariable String topicName,
            @RequestParam(required = false) String tenantId) {
        
        if (!StringUtils.hasText(tenantId)) {
            return ApiResponse.error(40001, "tenantId参数不能为空");
        }
        
        logger.info("管理员查询Topic详情: topicName={}, tenantId={}", topicName, tenantId);
        
        return topicApplicationService.getTopicByName(topicName)
                .map(ApiResponse::success)
                .orElseGet(() -> ApiResponse.error(40401, "Topic不存在"));
    }

    /**
     * 管理员删除Topic
     */
    @DeleteMapping("/{topicName}")
    public ApiResponse<Void> deleteTopic(
            @PathVariable String topicName,
            @RequestParam(required = false) String tenantId) {
        
        if (!StringUtils.hasText(tenantId)) {
            return ApiResponse.error(40001, "tenantId参数不能为空");
        }
        
        logger.info("管理员删除Topic: topicName={}, tenantId={}", topicName, tenantId);
        
        topicApplicationService.deleteTopic(topicName);
        return ApiResponse.success(null);
    }
}

