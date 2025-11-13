package com.aixone.directory.permission.infrastructure.persistence.dbo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 权限规则方法关联表数据对象
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Entity
@Table(name = "permission_rule_methods")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PermissionRuleMethodId.class)
public class PermissionRuleMethodDbo {
    
    @Id
    @Column(name = "rule_id", columnDefinition = "UUID")
    private String ruleId;
    
    @Id
    @Column(name = "method", length = 20, nullable = false)
    private String method;
}

/**
 * 复合主键类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class PermissionRuleMethodId implements java.io.Serializable {
    private String ruleId;
    private String method;
}

