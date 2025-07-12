# Aixone Permission 权限公共包架构设计文档

## 1. 包定位与能力概述

Aixone Permission 是一个为微服务架构提供统一权限校验能力的二方包，聚焦于：
- 标准化 RBAC/ABAC 权限校验
- 细粒度数据权限与SQL条件生成
- 注解驱动、自动配置、易于集成
- 可插拔扩展点，支持自定义权限逻辑

## 2. 主要模型设计

### 2.1 权限核心模型

- **User（用户）**
  - userId: String
  - attributes: Map<String, Object>
- **Role（角色）**
  - roleId: String
  - name: String
  - permissions: List<Permission>
- **Permission（权限）**
  - permissionId: String
  - resource: String
  - action: String
  - level: Enum (READ/WRITE/DELETE/ADMIN)
- **Resource（资源）**
  - resourceId: String
  - type: String
- **Policy（策略）**
  - expression: String (如user.dept == resource.dept)

### 2.2 数据权限模型

- **DataScope（数据范围）**
  - SELF/DEPT/ALL/CUSTOM
- **DataPermission**
  - resource: String
  - field: String
  - strategy: String (SELF/DEPT/ALL/CUSTOM)
  - handler: String (可选自定义处理器标识)

## 3. 注解协议

### 3.1 @RequirePermission
```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    String value();      // 权限标识，如"user:read"
    String action() default ""; // 操作类型
}
```

### 3.2 @DataPermission
```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataPermission {
    String dataType();   // 数据类型，如"department"
    String handler() default ""; // 处理器bean名称
}
```

## 4. 关键接口/服务协议

### 4.1 用户权限提供者
```java
public interface UserPermissionProvider {
    List<Permission> getPermissions(User user);
}
```

### 4.2 数据权限处理器
```java
public interface DataPermissionHandler {
    String buildCondition(User user, Resource resource);
}
```

### 4.3 权限校验器
```java
public interface PermissionValidator {
    boolean hasPermission(User user, Permission permission, Resource resource);
}
```

### 4.4 SQL条件构建器
```java
public interface SqlConditionBuilder {
    String build(User user, Resource resource, String dataType);
}
```

## 5. 自动配置与扩展点

- 自动注册 PermissionFilter、UserPermissionProvider 默认实现
- 业务可通过自定义 Bean 覆盖扩展点（@ConditionalOnMissingBean）
- 默认实现未覆盖时抛出异常，避免误用

## 6. 包结构/分层架构草图

```
com.aixone.permission
├── annotation      // 注解定义
├── context         // 权限上下文
├── builder         // 上下文/SQL构建器
├── validator       // 权限校验器
├── handler         // 数据权限处理器
├── provider        // 用户权限提供者
├── model           // 领域模型
├── filter          // 权限过滤器
├── config          // 自动配置
├── abac            // ABAC表达式工具
├── cache           // 缓存接口
└── sql             // SQL生成器
```

## 7. 集成流程/调用协议

1. 引入依赖，主类加@EnablePermission
2. （可选）实现自定义UserPermissionProvider/DataPermissionHandler等扩展点
3. 业务接口/方法加@RequirePermission/@DataPermission注解
4. 权限过滤器自动拦截请求，构建上下文，依次调用校验器/数据权限处理器/SQL生成器
5. 业务查询时可通过DataPermissionHandler/SqlConditionBuilder获取SQL条件拼接到查询中

## 8. 配置项说明

- aixone.permission.enabled: 是否启用权限模块
- aixone.permission.cache.enabled: 是否启用权限缓存
- aixone.permission.cache.ttl: 缓存过期时间
- aixone.permission.timeout: 权限校验超时时间
- aixone.permission.fallback: 降级策略
- 日志、监控等配置

## 9. 典型用例

- 微服务接口权限校验（RBAC/ABAC）
- 数据权限SQL条件自动拼接（如MyBatis拦截器、JPA Specification等）
- 多租户/多部门/自定义数据权限扩展
- 与Spring Security、MyBatis/JPA等主流框架集成

## 10. 版本规划

- v1.0.0：基础RBAC/ABAC、注解、扩展点、SQL生成、缓存
- v1.1.0：增强数据权限、SQL生成器扩展、性能优化
- v1.2.0：监控、告警、安全加固、文档完善