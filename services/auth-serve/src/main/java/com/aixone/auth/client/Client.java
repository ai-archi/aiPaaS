package com.aixone.auth.client;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 客户端实体
 * 对应clients表
 */
@Entity
@Table(name = "clients")
@Data
public class Client {
    /** 客户端ID */
    @Id
    private String clientId;
    /** 客户端密钥 */
    private String clientSecret;
    /** 回调地址 */
    private String redirectUri;
    /** 授权范围 */
    private String scopes;
    /** 授权类型 */
    private String grantTypes;
} 