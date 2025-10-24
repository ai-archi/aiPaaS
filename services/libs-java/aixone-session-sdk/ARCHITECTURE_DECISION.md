# 架构决策：Session SDK 与 Permission SDK 职责分离

## 决策背景

在 `aixone-session-sdk` 的初始实现中，我们包含了权限相关的功能（如 `@RequirePermission`、`@RequireTenant` 注解和权限检查逻辑）。经过深入分析，我们决定将这些功能从 session-sdk 中移除，保持与 `aixone-permission-sdk` 的职责分离。

## 决策原因

### 1. 职责分离原则

**Session SDK 的职责**：
- 管理用户身份信息（userId, tenantId, clientId）
- 维护会话状态（token 信息、过期时间等）
- 提供用户属性（ABAC 属性）
- 管理请求级别的上下文信息

**Permission SDK 的职责**：
- 定义权限模型（Permission, Role, Policy）
- 执行权限检查逻辑
- 管理角色和权限的映射关系
- 提供权限决策服务

### 2. 架构设计问题

在 session-sdk 中包含权限功能存在以下问题：

1. **职责混乱**：Session 不应该包含业务逻辑
2. **重复实现**：与 permission-sdk 功能重复
3. **维护困难**：权限逻辑分散在两个地方
4. **扩展性差**：权限规则变更需要修改两个 SDK
5. **违反单一职责原则**：一个 SDK 承担了过多职责

### 3. 正确的架构设计

```
┌─────────────────┐    ┌─────────────────┐
│  Session SDK    │    │ Permission SDK  │
│                 │    │                 │
│ • 用户身份信息   │    │ • 权限模型定义   │
│ • 会话状态管理   │    │ • 权限检查逻辑   │
│ • ABAC 属性     │    │ • 权限提供者     │
│ • 简单角色检查   │    │ • 数据权限处理   │
│ • JWT 解析      │    │ • 复杂权限决策   │
└─────────────────┘    └─────────────────┘
         │                       │
         └───────────┬───────────┘
                     │
            ┌─────────────────┐
            │  业务应用层      │
            │                 │
            │ • 使用 Session  │
            │   获取用户信息   │
            │ • 使用 Permission│
            │   进行权限检查   │
            └─────────────────┘
```

## 实现方案

### 1. Session SDK 保留的功能

- **用户身份信息**：userId, tenantId, clientId
- **会话状态**：token 信息、过期时间、token 类型
- **ABAC 属性**：用户属性、环境属性、自定义属性
- **JWT 解析**：从 JWT 中提取用户信息

### 2. 移除的功能

- **所有权限检查**：`@RequirePermission` 注解
- **所有角色检查**：`@RequireRole` 注解
- **租户权限控制**：`@RequireTenant` 注解
- **权限相关方法**：`hasPermission()`, `getPermissions()` 等
- **角色相关方法**：`hasRole()`, `getRoles()` 等
- **权限和角色解析**：从 JWT 中解析权限和角色信息

### 3. 使用方式

#### 获取用户信息（使用 Session SDK）
```java
// 获取基本用户信息
String userId = SessionContext.getUserId();
String tenantId = SessionContext.getTenantId();
String clientId = SessionContext.getClientId();

// 获取 ABAC 属性
Object department = SessionContext.getAbacAttribute("department");
```

#### 权限检查（使用 Permission SDK）
```java
@RequirePermission("user:read")
public void getUser() { ... }

@DataPermission(dataType = "user")
public List<User> getUsers() { ... }
```

## 优势

### 1. 职责清晰
- Session SDK 专注于上下文管理
- Permission SDK 专注于权限控制
- 每个 SDK 职责单一，易于理解和维护

### 2. 易于扩展
- 权限规则变更只需修改 Permission SDK
- 会话管理变更只需修改 Session SDK
- 两个 SDK 可以独立演进

### 3. 降低耦合
- 业务应用可以选择性使用两个 SDK
- 简单的身份验证场景只需 Session SDK
- 复杂的权限控制场景使用两个 SDK 组合

### 4. 提高可测试性
- 每个 SDK 可以独立测试
- 权限逻辑和会话逻辑分离，测试更简单
- 可以单独模拟和测试权限功能

## 迁移指南

### 对于现有代码

1. **移除权限相关导入**：
```java
// 移除这些导入（已删除）
import com.aixone.session.annotation.RequirePermission;
import com.aixone.session.annotation.RequireTenant;
import com.aixone.session.annotation.RequireRole;

// 保留这些导入
import com.aixone.session.SessionContext;
```

2. **权限检查迁移**：
```java
// 原来的方式（已移除）
@RequireRole("ADMIN")
@RequirePermission("user:read")
public void getUser() { ... }

// 新的方式
@RequirePermission("user:read") // 使用 aixone-permission-sdk
public void getUser() { ... }
```

3. **添加 Permission SDK 依赖**：
```xml
<dependency>
    <groupId>com.aixone</groupId>
    <artifactId>aixone-permission-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 总结

通过将权限功能从 session-sdk 中移除，我们实现了：

1. **清晰的职责分离**：每个 SDK 专注于自己的核心功能
2. **更好的可维护性**：权限逻辑集中在一个地方
3. **更高的可扩展性**：两个 SDK 可以独立演进
4. **更低的耦合度**：业务应用可以灵活选择使用的功能

这种设计符合微服务架构中"单一职责"和"关注点分离"的原则，为 AixOne 技术平台提供了更加清晰和可维护的架构基础。
