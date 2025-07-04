package com.aixone.auth.token;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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