package com.aixone.event.api;

import com.aixone.event.dto.TopicDTO;
import com.aixone.common.api.ApiResponse;
import java.util.List;

/**
 * Topic相关接口协议
 * 基于事件中心的TopicController设计，提供Topic管理的核心接口
 */
public interface TopicApi {
    
    /**
     * 注册Topic
     * 对应事件中心的 POST /api/topics/register
     */
    ApiResponse<TopicDTO> registerTopic(TopicDTO topicDTO);

    /**
     * 查询所有Topic
     * 对应事件中心的 GET /api/topics
     */
    ApiResponse<List<TopicDTO>> getAllTopics();

    /**
     * 根据名称查询Topic
     * 对应事件中心的 GET /api/topics/{topicName}
     */
    ApiResponse<TopicDTO> getTopicByName(String topicName);

    /**
     * 更新Topic描述
     * 对应事件中心的 PUT /api/topics/{topicName}
     */
    ApiResponse<TopicDTO> updateTopic(String topicName, String description);

    /**
     * 激活Topic
     * 对应事件中心的 POST /api/topics/{topicName}/activate
     */
    ApiResponse<Boolean> activateTopic(String topicName);

    /**
     * 停用Topic
     * 对应事件中心的 POST /api/topics/{topicName}/deactivate
     */
    ApiResponse<Boolean> deactivateTopic(String topicName);

    /**
     * 删除Topic
     * 对应事件中心的 DELETE /api/topics/{topicName}
     */
    ApiResponse<Boolean> deleteTopic(String topicName);
}
