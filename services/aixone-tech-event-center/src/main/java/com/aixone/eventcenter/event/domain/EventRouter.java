package com.aixone.eventcenter.event.domain;

import java.util.List;

/**
 * 事件路由领域服务
 * 负责根据事件类型和订阅配置进行事件路由决策
 */
public interface EventRouter {
    
    /**
     * 根据事件类型获取所有需要接收该事件的订阅
     * @param eventType 事件类型
     * @return 订阅列表
     */
    List<Subscription> routeSubscriptions(String eventType);
    
    /**
     * 判断事件是否匹配订阅的过滤条件
     * @param event 事件
     * @param subscription 订阅
     * @return 是否匹配
     */
    boolean matchesFilter(Event event, Subscription subscription);
}

