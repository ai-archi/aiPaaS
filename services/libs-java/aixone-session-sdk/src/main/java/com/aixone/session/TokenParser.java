package com.aixone.session;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Token 解析工具，支持 JWT
 * 与 aixone-tech-auth 服务生成的 JWT 格式兼容
 */
public class TokenParser {
    private final String jwtSecret;
    private final String jwtIssuer;
    
    public TokenParser(String jwtSecret) {
        this(jwtSecret, "aixone-tech-auth");
    }
    
    public TokenParser(String jwtSecret, String jwtIssuer) {
        this.jwtSecret = jwtSecret;
        this.jwtIssuer = jwtIssuer;
    }
    
    /**
     * 解析 JWT Token 并提取会话信息
     */
    public SessionContext.SessionInfo parse(String token) throws JwtException {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .requireIssuer(jwtIssuer)
                .build()
                .parseClaimsJws(token)
                .getBody();
                
        SessionContext.SessionInfo info = new SessionContext.SessionInfo();
        
        // 基本信息
        info.setUserId(claims.getSubject());
        info.setTenantId((String) claims.get("tenantId"));
        info.setClientId((String) claims.get("clientId"));
        
        // 注意：角色和权限信息不再在 session-sdk 中处理
        // 如需角色和权限检查，请使用 aixone-permission-sdk
        
        // ABAC 属性
        AbacAttributes abac = new AbacAttributes();
        
        // 用户属性
        addClaimToAbac(claims, "department", abac);
        addClaimToAbac(claims, "position", abac);
        addClaimToAbac(claims, "level", abac);
        addClaimToAbac(claims, "organization", abac);
        addClaimToAbac(claims, "region", abac);
        
        // 环境属性
        addClaimToAbac(claims, "ipAddress", abac);
        addClaimToAbac(claims, "userAgent", abac);
        addClaimToAbac(claims, "deviceType", abac);
        
        // 自定义属性
        Object customAttrs = claims.get("customAttributes");
        if (customAttrs instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> customMap = (Map<String, Object>) customAttrs;
            customMap.forEach(abac::put);
        }
        
        info.setAbacAttributes(abac);
        
        // 令牌信息
        info.setTokenType((String) claims.get("tokenType"));
        info.setIssuedAt(claims.getIssuedAt());
        info.setExpiresAt(claims.getExpiration());
        
        return info;
    }
    
    /**
     * 验证 Token 是否有效（不解析内容）
     */
    public boolean isValid(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .requireIssuer(jwtIssuer)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
    
    /**
     * 获取 Token 中的租户ID（不解析完整内容）
     */
    public String getTenantId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .requireIssuer(jwtIssuer)
                .build()
                .parseClaimsJws(token)
                .getBody();
            return (String) claims.get("tenantId");
        } catch (JwtException e) {
            return null;
        }
    }
    
    /**
     * 获取 Token 中的用户ID（不解析完整内容）
     */
    public String getUserId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .requireIssuer(jwtIssuer)
                .build()
                .parseClaimsJws(token)
                .getBody();
            return claims.getSubject();
        } catch (JwtException e) {
            return null;
        }
    }
    
    private void addClaimToAbac(Claims claims, String claimName, AbacAttributes abac) {
        Object value = claims.get(claimName);
        if (value != null) {
            abac.put(claimName, value);
        }
    }
} 