# AixOne Session SDK

统一上下文管理与多租户属性支持的基础包，为所有微服务提供统一的会话上下文管理。

## 功能特性

- **统一会话管理**：基于 ThreadLocal 的会话上下文管理
- **JWT 支持**：完整的 JWT Token 解析和验证
- **多租户支持**：内置多租户隔离和验证
- **ABAC 属性**：支持基于属性的上下文信息
- **Spring 集成**：与 Spring Boot 无缝集成

## 重要说明

**本 SDK 专注于会话上下文管理，不包含复杂的权限控制逻辑。**

- 如需完整的权限控制（RBAC/ABAC），请使用 `aixone-permission-sdk`
- 本 SDK 只提供基础的身份信息管理，适合会话上下文管理场景
- 复杂的权限决策、数据权限等请使用专门的权限 SDK

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.aixone</groupId>
    <artifactId>aixone-session-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置属性

```yaml
# JWT 配置
jwt:
  secret: your-jwt-secret-key
  issuer: aixone-tech-auth

# 多租户配置
multitenant:
  tenant-header: X-Tenant-ID

# 会话配置
session:
  require-auth: true
  interceptor-patterns: ["/**"]
  exclude-patterns: ["/public/**"]
```

### 3. 自动配置

SDK 会自动配置 `TokenParser` 和 `SessionInterceptor`，无需额外配置。

## 核心组件

### SessionContext

统一会话上下文管理，提供静态方法访问当前会话信息。

```java
// 获取当前用户信息
String userId = SessionContext.getUserId();
String tenantId = SessionContext.getTenantId();
String clientId = SessionContext.getClientId();

// ABAC 属性
Object department = SessionContext.getAbacAttribute("department");
Object level = SessionContext.getAbacAttribute("level", "default");
```

### TokenParser

JWT Token 解析器，支持从 JWT 中提取用户信息。

```java
@Autowired
private TokenParser tokenParser;

// 解析 Token
SessionContext.SessionInfo sessionInfo = tokenParser.parse(token);

// 验证 Token
boolean isValid = tokenParser.isValid(token);

// 快速获取信息
String userId = tokenParser.getUserId(token);
String tenantId = tokenParser.getTenantId(token);
```

### SessionInterceptor

Spring 拦截器，自动处理 Token 解析和会话上下文设置。

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private TokenParser tokenParser;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionInterceptor(tokenParser))
                .addPathPatterns("/**")
                .excludePathPatterns("/public/**");
    }
}
```

## 权限控制

**本 SDK 不提供权限控制功能。**

如需权限控制，请使用 `aixone-permission-sdk`：

```java
@RestController
public class UserController {
    
    @GetMapping("/admin-only")
    @RequirePermission("admin:access")  // 使用 aixone-permission-sdk
    public ResponseEntity<String> adminOnly() {
        return ResponseEntity.ok("Admin access granted");
    }
    
    @GetMapping("/user-data")
    @DataPermission(dataType = "user")  // 使用 aixone-permission-sdk
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }
}
```

## ABAC 属性支持

支持基于属性的访问控制（ABAC），可以基于用户属性、资源属性、环境属性等进行权限控制。

```java
// 获取 ABAC 属性
AbacAttributes abac = SessionContext.getAbacAttributes();
Object department = abac.get("department");
Object position = abac.get("position");
Object level = abac.get("level");

// 便捷方法
Object department = SessionContext.getAbacAttribute("department");
Object level = SessionContext.getAbacAttribute("level", "default");
```

## 配置选项

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `jwt.secret` | - | JWT 密钥（必需） |
| `jwt.issuer` | `aixone-tech-auth` | JWT 发行者 |
| `multitenant.tenant-header` | `X-Tenant-ID` | 租户头名称 |
| `session.require-auth` | `true` | 是否要求认证 |
| `session.interceptor-patterns` | `["/**"]` | 拦截器路径模式 |
| `session.exclude-patterns` | `[]` | 排除路径模式 |

## 与 aixone-tech-auth 集成

SDK 与 `aixone-tech-auth` 服务完全兼容，支持从认证服务生成的 JWT Token 中提取用户信息。

### JWT Claims 格式

```json
{
  "sub": "user123",
  "iss": "aixone-tech-auth",
  "iat": 1640995200,
  "exp": 1640998800,
  "tenantId": "tenant1",
  "clientId": "client1",
  "tokenType": "ACCESS",
  "roles": ["ADMIN", "USER"],
  "permissions": ["user:read", "user:write"],
  "department": "IT",
  "position": "Manager",
  "level": "Senior",
  "customAttributes": {
    "region": "US",
    "timezone": "UTC"
  }
}
```

## 最佳实践

1. **统一配置**：在应用启动时配置 JWT 密钥和发行者
2. **权限粒度**：合理设计角色和权限的粒度
3. **ABAC 属性**：充分利用 ABAC 属性进行细粒度权限控制
4. **异常处理**：妥善处理权限不足和会话过期的情况
5. **性能优化**：避免在每次请求中重复解析 Token

## 示例项目

参考 `aixone-tech-auth` 项目中的 `SessionTestController` 了解完整的使用示例。

## 许可证

Copyright (c) 2024 AixOne. All rights reserved.
