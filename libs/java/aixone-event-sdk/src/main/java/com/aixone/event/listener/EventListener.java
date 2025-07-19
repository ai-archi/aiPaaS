package com.aixone.event.listener;

import com.aixone.event.dto.EventDTO;

/**
 * 事件监听器接口
 */
public interface EventListener {
    void onEvent(EventDTO event);
} 