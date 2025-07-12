package com.aixone.eventcenter.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import com.aixone.eventcenter.monitor.MonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aixone.session.SessionContext;

/**
 * 事件服务，负责事件发布与查询
 */
@Service
public class EventService {
    private static final Logger logger = LoggerFactory.getLogger(EventService.class);
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private MonitorService monitorService;

    /**
     * 发布新事件
     */
    public Event publishEvent(Event event) {
        try {
            event.setTimestamp(java.time.Instant.now());
            event.setTenantId(SessionContext.getTenantId());
            Event saved = eventRepository.save(event);
            monitorService.incEvent();
            return saved;
        } catch (Exception ex) {
            monitorService.incEventError();
            logger.error("[ALERT] 事件发布失败: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * 查询所有事件（按租户）
     */
    public List<Event> getAllEvents() {
        return eventRepository.findByTenantId(SessionContext.getTenantId());
    }

    /**
     * 按ID查询事件（按租户）
     */
    public Optional<Event> getEventById(Long id) {
        return eventRepository.findByEventIdAndTenantId(id, SessionContext.getTenantId());
    }
} 