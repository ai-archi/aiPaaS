package com.aixone.auth.token;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * 令牌实体
 * 对应tokens表
 */
@Entity
@Table(name = "tokens")
@Data
public class Token {
    /** JWT Token */
    @Id
    private String token;
    /** 用户ID */
    private String userId;
    /** 客户端ID */
    private String clientId;
    /** 过期时间 */
    private LocalDateTime expiresAt;
    /** 类型（access/refresh） */
    private String type;
} 