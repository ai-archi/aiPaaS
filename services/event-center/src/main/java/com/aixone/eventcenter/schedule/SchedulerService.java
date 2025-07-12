package com.aixone.eventcenter.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 调度节点服务
 */
@Service
public class SchedulerService {
    @Autowired
    private SchedulerRepository schedulerRepository;

    /**
     * 查询所有调度节点
     */
    public List<Scheduler> getAllNodes() {
        return schedulerRepository.findAll();
    }
} 