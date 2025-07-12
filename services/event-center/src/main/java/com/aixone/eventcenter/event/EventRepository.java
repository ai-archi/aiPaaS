package com.aixone.eventcenter.event;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 事件实体仓库
 */
public interface EventRepository extends JpaRepository<Event, Long> {
    // 可扩展自定义查询
    java.util.List<Event> findByTenantId(String tenantId);
    java.util.Optional<Event> findByEventIdAndTenantId(Long eventId, String tenantId);
} 