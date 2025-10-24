package com.aixone.common.api;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 分页请求对象
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Data
@NoArgsConstructor
public class PageRequest {
    
    /** 当前页码，从1开始 */
    private int pageNum = 1;
    
    /** 每页条数，默认20 */
    private int pageSize = 20;
    
    /** 排序字段 */
    private String sortBy;
    
    /** 排序方向：asc/desc */
    private String sortDirection = "desc";
    
    /**
     * 构造函数
     * 
     * @param pageNum 页码
     * @param pageSize 每页大小
     */
    public PageRequest(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
    
    /**
     * 构造函数
     * 
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param sortBy 排序字段
     * @param sortDirection 排序方向
     */
    public PageRequest(int pageNum, int pageSize, String sortBy, String sortDirection) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }
    
    /**
     * 获取偏移量（用于数据库查询）
     * 
     * @return 偏移量
     */
    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }
    
    /**
     * 检查分页参数是否有效
     * 
     * @return 是否有效
     */
    public boolean isValid() {
        return pageNum > 0 && pageSize > 0 && pageSize <= 1000;
    }
    
    // 手动添加getter方法，确保编译通过
    public int getPageNum() {
        return pageNum;
    }
    
    public int getPageSize() {
        return pageSize;
    }
}
