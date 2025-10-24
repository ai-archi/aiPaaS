# AixOne Session SDK - 最终架构设计

## 设计原则

**Session SDK 专注于会话上下文管理，不包含任何权限控制逻辑。**

## 核心功能

### 1. 用户身份信息管理
- **用户ID**：`SessionContext.getUserId()`
- **租户ID**：`SessionContext.getTenantId()`
- **客户端ID**：`SessionContext.getClientId()`

### 2. 会话状态管理
- **Token类型**：`SessionContext.getTokenType()`
- **Token过期时间**：`SessionContext.isExpired()`
- **Token验证**：通过 `TokenParser` 进行 JWT 解析

### 3. ABAC 属性支持
- **用户属性**：部门、职位、级别等
- **环境属性**：IP地址、用户代理、设备类型等
- **自定义属性**：业务相关的扩展属性

## 已移除的功能

### 1. 权限相关功能
- ❌ `@RequirePermission` 注解
- ❌ `hasPermission()` 方法
- ❌ `getPermissions()` 方法
- ❌ 权限解析逻辑

### 2. 角色相关功能
- ❌ `@RequireRole` 注解
- ❌ `hasRole()` 方法
- ❌ `getRoles()` 方法
- ❌ 角色解析逻辑

### 3. 租户权限控制
- ❌ `@RequireTenant` 注解
- ❌ 租户权限检查逻辑

### 4. AOP 切面
- ❌ `SessionSecurityAspect` 切面
- ❌ 所有权限和角色检查的 AOP 逻辑

## 正确的使用方式

### 1. 获取用户信息
```java
// 基本用户信息
String userId = SessionContext.getUserId();
String tenantId = SessionContext.getTenantId();
String clientId = SessionContext.getClientId();

// ABAC 属性
Object department = SessionContext.getAbacAttribute("department");
Object level = SessionContext.getAbacAttribute("level", "default");
```

### 2. 权限控制（使用 aixone-permission-sdk）
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

## 架构优势

### 1. 职责清晰
- **Session SDK**：专注于上下文管理
- **Permission SDK**：专注于权限控制
- 每个 SDK 职责单一，易于理解和维护

### 2. 高度解耦
- 业务应用可以选择性使用两个 SDK
- 简单的身份验证场景只需 Session SDK
- 复杂的权限控制场景使用两个 SDK 组合

### 3. 易于扩展
- 权限规则变更只需修改 Permission SDK
- 会话管理变更只需修改 Session SDK
- 两个 SDK 可以独立演进

### 4. 提高可测试性
- 每个 SDK 可以独立测试
- 权限逻辑和会话逻辑分离，测试更简单
- 可以单独模拟和测试权限功能

## 依赖关系

```
业务应用
    ├── aixone-session-sdk (必需)
    │   ├── 用户身份信息
    │   ├── 会话状态管理
    │   └── ABAC 属性支持
    └── aixone-permission-sdk (可选)
        ├── 权限模型定义
        ├── 权限检查逻辑
        └── 数据权限处理
```

## 总结

通过彻底移除所有权限和角色相关功能，`aixone-session-sdk` 现在完全专注于会话上下文管理，实现了：

1. **单一职责**：只负责会话上下文管理
2. **高度解耦**：与权限控制完全分离
3. **易于维护**：功能简单明确，易于理解和维护
4. **高度可扩展**：可以独立演进，不影响权限控制逻辑

这种设计完全符合微服务架构中"单一职责"和"关注点分离"的原则，为 AixOne 技术平台提供了更加清晰和可维护的架构基础。
