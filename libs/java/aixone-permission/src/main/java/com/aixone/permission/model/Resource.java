package com.aixone.permission.model;

/**
 * 资源模型
 * 代表被保护的业务对象
 */
public class Resource {
    /** 资源唯一标识 */
    private String resourceId;
    /** 资源类型（如user/order/product等） */
    private String type;

    public Resource() {}
    public Resource(String resourceId, String type) {
        this.resourceId = resourceId;
        this.type = type;
    }
    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
} 