package com.aixone.common.api;

import java.util.List;

/**
 * 分页结果对象
 * @param <T> 数据类型
 */
public class PageResult<T> {
    private long total;      // 总条数
    private int pageNum;     // 当前页码
    private int pageSize;    // 每页条数
    private List<T> list;    // 当前页数据

    public PageResult() {}
    public PageResult(long total, int pageNum, int pageSize, List<T> list) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.list = list;
    }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public int getPageNum() { return pageNum; }
    public void setPageNum(int pageNum) { this.pageNum = pageNum; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    public List<T> getList() { return list; }
    public void setList(List<T> list) { this.list = list; }
} 