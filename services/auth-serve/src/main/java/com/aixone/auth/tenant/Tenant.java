package com.aixone.auth.tenant;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 租户实体
 * 对应tenants表
 */
@Entity
@Table(name = "tenants")
@Data
public class Tenant {
    /** 主键 */
    @Id
    private String tenantId;
    /** 租户名称 */
    private String name;
    /** 是否允许注册 */
    private Boolean allowRegister;
} 