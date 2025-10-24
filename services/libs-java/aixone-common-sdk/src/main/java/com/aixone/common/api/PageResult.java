package com.aixone.common.api;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 分页结果对象
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    
    /** 总条数 */
    private long total;
    
    /** 当前页码 */
    private int pageNum;
    
    /** 每页条数 */
    private int pageSize;
    
    /** 当前页数据 */
    private List<T> list;
    
    /** 总页数 */
    private int totalPages;
    
    /** 是否有下一页 */
    private boolean hasNext;
    
    /** 是否有上一页 */
    private boolean hasPrevious;
    
    /**
     * 构造函数
     * 
     * @param total 总条数
     * @param pageNum 当前页码
     * @param pageSize 每页条数
     * @param list 当前页数据
     */
    public PageResult(long total, int pageNum, int pageSize, List<T> list) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.list = list;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
        this.hasNext = pageNum < totalPages;
        this.hasPrevious = pageNum > 1;
    }
    
    /**
     * 创建空的分页结果
     * 
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param <T> 数据类型
     * @return 空的分页结果
     */
    public static <T> PageResult<T> empty(int pageNum, int pageSize) {
        return new PageResult<>(0, pageNum, pageSize, List.of());
    }
    
    /**
     * 创建分页结果
     * 
     * @param total 总条数
     * @param pageRequest 分页请求
     * @param list 数据列表
     * @param <T> 数据类型
     * @return 分页结果
     */
    public static <T> PageResult<T> of(long total, PageRequest pageRequest, List<T> list) {
        return new PageResult<>(total, pageRequest.getPageNum(), pageRequest.getPageSize(), list);
    }
}
