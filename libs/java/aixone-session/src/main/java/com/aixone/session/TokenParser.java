package com.aixone.session;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import java.util.Set;
import java.util.HashSet;

/**
 * Token 解析工具，支持 JWT
 */
public class TokenParser {
    private final String jwtSecret;
    public TokenParser(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }
    public SessionContext.SessionInfo parse(String token) throws JwtException {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret.getBytes())
                .parseClaimsJws(token)
                .getBody();
        SessionContext.SessionInfo info = new SessionContext.SessionInfo();
        info.setUserId(claims.getSubject());
        info.setTenantId((String) claims.get("tenantId"));
        Object rolesObj = claims.get("roles");
        if (rolesObj instanceof String) {
            Set<String> roles = new HashSet<>();
            for (String r : ((String) rolesObj).split(",")) {
                roles.add(r.trim());
            }
            info.setRoles(roles);
        }
        // 可扩展解析 ABAC 属性
        AbacAttributes abac = new AbacAttributes();
        Object dept = claims.get("department");
        if (dept != null) abac.put("department", dept);
        Object position = claims.get("position");
        if (position != null) abac.put("position", position);
        info.setAbacAttributes(abac);
        return info;
    }
} 