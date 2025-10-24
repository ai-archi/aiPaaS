# Aixone Permission SDK

基于 `aixone-tech-auth` 项目调整完善的权限管理SDK，提供完整的RBAC和ABAC权限控制功能。

## 功能特性

### 1. 权限模型
- **Permission**: 权限实体，支持多租户、权限级别等
- **Role**: 角色实体，支持多租户、权限关联等
- **User**: 用户实体，支持多租户、角色关联、属性扩展等
- **Resource**: 资源实体，支持多租户、属性扩展等
- **Policy**: ABAC策略实体，支持复杂的属性访问控制

### 2. 权限验证器
- **RbacValidator**: 基于角色的访问控制验证器
- **AbacValidator**: 基于属性的访问控制验证器
- **PermissionValidator**: 权限验证器接口

### 3. 权限服务
- **PermissionService**: 权限管理服务接口
- **DefaultPermissionService**: 默认权限服务实现
- **UserPermissionProvider**: 用户权限提供者接口

### 4. 缓存支持
- **PermissionCache**: 权限缓存接口
- **DefaultPermissionCache**: 基于内存的默认缓存实现
- **RedisPermissionCache**: 基于Redis的分布式缓存实现
- **InMemoryPermissionCache**: 简单内存缓存实现
- **DistributedPermissionCache**: 分布式缓存实现（占位）

### 5. 注解支持
- **@RequirePermission**: 权限校验注解
- **@RequireRole**: 角色校验注解
- **@RequireAbac**: ABAC策略校验注解
- **@DataPermission**: 数据权限注解
- **@EnablePermission**: 启用权限功能注解

### 6. 数据权限
- **DataPermissionHandler**: 数据权限处理器接口
- **AllDataPermissionHandler**: 全部数据权限处理器
- **SelfDataPermissionHandler**: 本人数据权限处理器
- **DeptDataPermissionHandler**: 部门数据权限处理器

### 7. SQL条件构建
- **SqlConditionBuilder**: SQL条件构建器接口
- **SelfSqlConditionBuilder**: 本人数据SQL条件构建器
- **DeptSqlConditionBuilder**: 部门数据SQL条件构建器

### 8. ABAC表达式工具
- **AbacExpressionUtil**: ABAC表达式解析和评估工具

## 技术栈

- **Java 21**: 使用最新的Java特性
- **Spring Boot 3.2.5**: 提供自动配置和依赖注入
- **Spring Security**: 安全框架集成
- **Spring Data Redis**: Redis集成支持
- **Lombok**: 减少样板代码
- **Jackson**: JSON序列化支持

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.aixone</groupId>
    <artifactId>aixone-permission-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 启用权限功能

```java
@SpringBootApplication
@EnablePermission
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 3. 使用权限注解

```java
@RestController
public class UserController {
    
    @RequirePermission("user:read")
    @GetMapping("/users")
    public List<User> getUsers() {
        // 需要 user:read 权限
    }
    
    @RequireRole("ADMIN")
    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        // 需要 ADMIN 角色
    }
    
    @RequireAbac(resource = "user", action = "update")
    @PutMapping("/users/{id}")
    public User updateUser(@PathVariable String id, @RequestBody User user) {
        // 需要ABAC策略验证
    }
}
```

### 4. 实现用户权限提供者

```java
@Component
public class CustomUserPermissionProvider implements UserPermissionProvider {
    
    @Override
    public List<Permission> getPermissions(User user) {
        // 实现获取用户权限的逻辑
        return userPermissions;
    }
    
    @Override
    public User getUser(String userId) {
        // 实现获取用户信息的逻辑
        return user;
    }
    
    // 实现其他必需的方法...
}
```

## 配置

### 缓存配置

```yaml
permission:
  cache:
    ttl:
      minutes: 60
    maxSize: 10000
```

### Redis配置

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: 
      database: 0
```

## 构建

```bash
mvn clean compile
mvn test
mvn package
```

## 版本历史

### 1.0.0
- 初始版本
- 基于 `aixone-tech-auth` 项目调整完善
- 支持RBAC和ABAC权限控制
- 提供完整的权限管理功能
- 支持多租户架构
- 提供多种缓存实现
- 支持注解式权限控制
- 支持数据权限控制

## 许可证

本项目采用 MIT 许可证。
