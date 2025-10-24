package com.aixone.tech.auth.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * JWT 工具类
 * 负责 JWT Token 的生成和解析
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
        
        return Jwts.builder()
                .subject(userId)
                .issuer(jwtIssuer)
                .issuedAt(now)
                .expiration(expiryDate)
                .claim("tenantId", tenantId)
                .claim("clientId", clientId)
                .claim("tokenType", tokenType)
                .claim("roles", roles)
                .claim("permissions", permissions)
                .claims(abacAttributes)
                .signWith(getSigningKey())
                .compact();
    }
    
    /**
     * 验证令牌
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .requireIssuer(jwtIssuer)
                .build()
                .parseSignedClaims(token);
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
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .requireIssuer(jwtIssuer)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            return null;
        }
    }
}
