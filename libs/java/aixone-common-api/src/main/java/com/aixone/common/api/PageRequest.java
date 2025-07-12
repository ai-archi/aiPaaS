package com.aixone.common.api;

/**
 * 分页请求对象
 */
public class PageRequest {
    /** 当前页码，从1开始 */
    private int pageNum = 1;
    /** 每页条数，默认20 */
    private int pageSize = 20;

    public PageRequest() {}
    public PageRequest(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
    public int getPageNum() { return pageNum; }
    public void setPageNum(int pageNum) { this.pageNum = pageNum; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
} 