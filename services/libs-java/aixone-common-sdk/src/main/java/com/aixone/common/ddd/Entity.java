package com.aixone.common.ddd;

import com.aixone.common.session.SessionContext;
import java.util.Objects;

/**
 * 实体基类
 * 实体通过唯一标识来区分，即使属性相同也可能是不同的实体
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 * @param <ID> 实体标识类型
 */
public abstract class Entity<ID> {
    
    protected ID id;
    protected String tenantId;
    
    protected Entity(ID id) {
        this.id = Objects.requireNonNull(id, "Entity ID cannot be null");
        this.tenantId = getCurrentTenantId();
    }
    
    protected Entity(ID id, String tenantId) {
        this.id = Objects.requireNonNull(id, "Entity ID cannot be null");
        this.tenantId = tenantId;
    }
    
    public ID getId() {
        return id;
    }
    
    public String getTenantId() {
        return tenantId;
    }
    
    protected void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    
    /**
     * 获取当前租户ID
     * 
     * @return 租户ID
     */
    private String getCurrentTenantId() {
        try {
            return SessionContext.getTenantId();
        } catch (Exception e) {
            return null; // 如果无法获取租户ID，返回null
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Entity<?> entity = (Entity<?>) obj;
        return Objects.equals(id, entity.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + ", tenantId=" + tenantId + "}";
    }
}
