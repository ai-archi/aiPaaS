package com.aixone.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * JWT 工具类
 * 负责 JWT Token 的生成和解析
 * 
 * 配置要求：
 * - jwt.secret: JWT签名密钥
 * - jwt.issuer: JWT发行者
 * - jwt.expiration: Access Token过期时间（毫秒）
 * - jwt.refresh-expiration: Refresh Token过期时间（毫秒）
 */
@Component
public class JwtUtils {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.issuer}")
    private String jwtIssuer;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    @Value("${jwt.refresh-expiration}")
    private long jwtRefreshExpiration;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * 生成访问令牌
     */
    public String generateAccessToken(String userId, String tenantId, String clientId, 
                                    Set<String> roles, Set<String> permissions, 
                                    Map<String, Object> abacAttributes) {
        return generateToken(userId, tenantId, clientId, roles, permissions, abacAttributes, 
                           "ACCESS", jwtExpiration);
    }
    
    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(String userId, String tenantId, String clientId) {
        return generateToken(userId, tenantId, clientId, null, null, null, 
                           "REFRESH", jwtRefreshExpiration);
    }
    
    /**
     * 生成令牌
     */
    private String generateToken(String userId, String tenantId, String clientId,
                               Set<String> roles, Set<String> permissions,
                               Map<String, Object> abacAttributes, String tokenType, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        io.jsonwebtoken.JwtBuilder builder = Jwts.builder()
                .setSubject(userId)
                .setIssuer(jwtIssuer)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("tenantId", tenantId)
                .claim("clientId", clientId)
                .claim("tokenType", tokenType)
                .claim("roles", roles)
                .claim("permissions", permissions);
        
        // 添加ABAC属性
        if (abacAttributes != null && !abacAttributes.isEmpty()) {
            for (Map.Entry<String, Object> entry : abacAttributes.entrySet()) {
                builder.claim(entry.getKey(), entry.getValue());
            }
        }
        
        return builder.signWith(getSigningKey())
                .compact();
    }
    
    /**
     * 验证令牌
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .requireIssuer(jwtIssuer)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 从令牌中获取用户ID
     */
    public String getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.getSubject() : null;
    }
    
    /**
     * 从令牌中获取租户ID
     */
    public String getTenantIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? (String) claims.get("tenantId") : null;
    }
    
    /**
     * 从令牌中获取客户端ID
     */
    public String getClientIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? (String) claims.get("clientId") : null;
    }
    
    /**
     * 从令牌中获取令牌类型
     */
    public String getTokenTypeFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? (String) claims.get("tokenType") : null;
    }
    
    /**
     * 从令牌中获取角色
     */
    @SuppressWarnings("unchecked")
    public Set<String> getRolesFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims != null) {
            Object roles = claims.get("roles");
            if (roles instanceof Set) {
                return (Set<String>) roles;
            }
        }
        return null;
    }
    
    /**
     * 从令牌中获取权限
     */
    @SuppressWarnings("unchecked")
    public Set<String> getPermissionsFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims != null) {
            Object permissions = claims.get("permissions");
            if (permissions instanceof Set) {
                return (Set<String>) permissions;
            }
        }
        return null;
    }
    
    /**
     * 从令牌中获取ABAC属性
     */
    public Map<String, Object> getAbacAttributesFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims == null) {
            return null;
        }
        // 过滤掉JWT标准字段和自定义的非ABAC字段
        Map<String, Object> abacAttributes = new java.util.HashMap<>(claims);
        abacAttributes.remove("sub");
        abacAttributes.remove("iss");
        abacAttributes.remove("iat");
        abacAttributes.remove("exp");
        abacAttributes.remove("tenantId");
        abacAttributes.remove("clientId");
        abacAttributes.remove("tokenType");
        abacAttributes.remove("roles");
        abacAttributes.remove("permissions");
        return abacAttributes;
    }
    
    /**
     * 检查令牌是否过期
     */
    public boolean isTokenExpired(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims != null) {
            return claims.getExpiration().before(new Date());
        }
        return true;
    }
    
    /**
     * 从令牌中获取 Claims
     */
    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .requireIssuer(jwtIssuer)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }
}
