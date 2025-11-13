package com.aixone.eventcenter.schedule.application;

import lombok.Data;

/**
 * 任务统计信息
 */
@Data
public class TaskStatistics {
    
    /**
     * 任务ID
     */
    private Long taskId;
    
    /**
     * 总执行次数
     */
    private long totalCount;
    
    /**
     * 成功执行次数
     */
    private long successCount;
    
    /**
     * 失败执行次数
     */
    private long failCount;
    
    /**
     * 成功率
     */
    private double successRate;
    
    /**
     * 失败率
     */
    private double failRate;
    
    public TaskStatistics(Long taskId, long totalCount, long successCount, long failCount) {
        this.taskId = taskId;
        this.totalCount = totalCount;
        this.successCount = successCount;
        this.failCount = failCount;
        
        if (totalCount > 0) {
            this.successRate = (double) successCount / totalCount * 100;
            this.failRate = (double) failCount / totalCount * 100;
        } else {
            this.successRate = 0.0;
            this.failRate = 0.0;
        }
    }
}
