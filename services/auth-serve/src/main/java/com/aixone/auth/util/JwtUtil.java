package com.aixone.auth.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Base64;
import javax.crypto.SecretKey;

/**
 * JWT工具类
 * 提供Token生成与校验方法（HS256实现）
 */
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private long expirationSeconds;
    private SecretKey key;

    @PostConstruct
    public void init() {
        // Base64解码密钥
        byte[] keyBytes = Base64.getEncoder().encode(secret.getBytes());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成JWT Token
     */
    public String generateToken(String userId, String clientId) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date exp = new Date(nowMillis + expirationSeconds * 1000);
        return Jwts.builder()
                .setSubject(userId)
                .setIssuer("auth-serve")
                .setIssuedAt(now)
                .setExpiration(exp)
                .claim("clientId", clientId)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 校验并解析JWT Token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 解析Token获取userId
     */
    public String getUserId(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    /**
     * 解析Token获取clientId
     */
    public String getClientId(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.get("clientId", String.class);
    }
} 