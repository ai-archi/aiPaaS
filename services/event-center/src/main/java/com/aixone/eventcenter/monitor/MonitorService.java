package com.aixone.eventcenter.monitor;

import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 监控服务，统计运行时核心指标
 */
@Service
public class MonitorService {
    private final AtomicLong eventCount = new AtomicLong();
    private final AtomicLong eventErrorCount = new AtomicLong();
    private final AtomicLong taskCount = new AtomicLong();
    private final AtomicLong taskErrorCount = new AtomicLong();

    public void incEvent() { eventCount.incrementAndGet(); }
    public void incEventError() { eventErrorCount.incrementAndGet(); }
    public void incTask() { taskCount.incrementAndGet(); }
    public void incTaskError() { taskErrorCount.incrementAndGet(); }

    public long getEventCount() { return eventCount.get(); }
    public long getEventErrorCount() { return eventErrorCount.get(); }
    public long getTaskCount() { return taskCount.get(); }
    public long getTaskErrorCount() { return taskErrorCount.get(); }
} 