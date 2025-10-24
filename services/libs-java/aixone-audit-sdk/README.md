# aixone-audit-sdk

## 概述

审计功能SDK，提供审计日志记录、查询、分析等功能。支持多租户环境下的审计日志管理。

## 功能模块

### 1. 领域模型 (`com.aixone.audit.domain`)
- `AuditLog` - 审计日志聚合根
- `AuditEvent` - 审计事件
- `AuditContext` - 审计上下文

### 2. 应用服务 (`com.aixone.audit.application`)
- `AuditService` - 审计服务
- `AuditQueryService` - 审计查询服务

### 3. 基础设施 (`com.aixone.audit.infrastructure`)
- `AuditLogRepository` - 审计日志仓储接口
- `JpaAuditLogRepository` - JPA审计日志仓储实现
- `AuditEventPublisher` - 审计事件发布器

### 4. 接口层 (`com.aixone.audit.interfaces`)
- `AuditController` - 审计REST API控制器

## 多租户支持

本SDK通过依赖 `aixone-session-sdk` 来获取租户上下文，支持多租户环境下的审计日志隔离。

## 使用方式

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.aixone</groupId>
    <artifactId>aixone-audit-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置审计服务

```java
@Configuration
@EnableJpaRepositories(basePackages = "com.aixone.audit.infrastructure")
public class AuditConfig {
    
    @Bean
    public AuditService auditService(AuditLogRepository auditLogRepository) {
        return new AuditService(auditLogRepository);
    }
}
```

### 3. 记录审计日志

```java
@Service
public class UserService {
    
    @Autowired
    private AuditService auditService;
    
    public void createUser(CreateUserRequest request) {
        // 业务逻辑
        User user = new User(request.getName(), request.getEmail());
        userRepository.save(user);
        
        // 记录审计日志
        auditService.logAction("USER_CREATE", "用户创建", 
            Map.of("userId", user.getId(), "userName", user.getName()));
    }
}
```

### 4. 查询审计日志

```java
@RestController
public class AuditController {
    
    @Autowired
    private AuditQueryService auditQueryService;
    
    @GetMapping("/audit-logs")
    public ApiResponse<PageResult<AuditLogDTO>> getAuditLogs(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        
        PageRequest pageRequest = new PageRequest(pageNum, pageSize);
        PageResult<AuditLogDTO> result = auditQueryService.getAuditLogs(pageRequest);
        return ApiResponse.success(result);
    }
}
```

## 版本历史

- 1.0.0 - 初始版本，提供审计日志记录、查询、分析等功能
