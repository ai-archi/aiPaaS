package com.aixone.eventcenter.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * 任务执行日志服务
 */
@Service
public class JobLogService {
    @Autowired
    private JobLogRepository jobLogRepository;

    /**
     * 查询所有任务日志
     */
    public List<JobLog> getAllLogs() {
        return jobLogRepository.findAll();
    }

    /**
     * 按ID查询日志
     */
    public Optional<JobLog> getLogById(Long id) {
        return jobLogRepository.findById(id);
    }
} 