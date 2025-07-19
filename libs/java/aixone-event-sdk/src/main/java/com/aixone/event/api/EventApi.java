package com.aixone.event.api;

import com.aixone.event.dto.EventDTO;
import com.aixone.common.api.ApiResponse;
import com.aixone.common.api.PageRequest;
import com.aixone.common.api.PageResult;
import java.util.List;

/**
 * 事件相关接口协议
 */
public interface EventApi {
    /** 发布新事件 */
    ApiResponse<String> publishEvent(EventDTO eventDTO);

    /** 分页/条件查询事件 */
    ApiResponse<PageResult<EventDTO>> listEvents(PageRequest pageRequest, String eventType, String tenantId);

    /** 查询单个事件详情 */
    ApiResponse<EventDTO> getEventById(String eventId);

    /** 批量查询事件 */
    ApiResponse<List<EventDTO>> getEventsByIds(List<String> eventIds);
} 