package com.aixone.auth.user;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 用户实体
 * 对应users表
 */
@Entity
@Table(name = "users")
@Data
public class User {
    /** 主键 */
    @Id
    private String userId;
    /** 所属租户 */
    private String tenantId;
    /** 用户名 */
    private String username;
    /** 加密密码 */
    private String password;
    /** 状态（启用/禁用） */
    private String status;
    /** 邮箱 */
    private String email;
    /** 手机号 */
    private String phone;
} 