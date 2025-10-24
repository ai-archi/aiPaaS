# aixone-session-sdk 合并到 aixone-common-sdk 迁移指南

## 概述

`aixone-session-sdk` 已完全合并到 `aixone-common-sdk` 中，形成统一的基础设施 SDK。本指南将帮助您完成迁移。

## 迁移步骤

### 1. 更新依赖

#### 原来的依赖配置
```xml
<dependencies>
    <dependency>
        <groupId>com.aixone</groupId>
        <artifactId>aixone-common-sdk</artifactId>
        <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>com.aixone</groupId>
        <artifactId>aixone-session-sdk</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

#### 合并后的依赖配置
```xml
<dependencies>
    <dependency>
        <groupId>com.aixone</groupId>
        <artifactId>aixone-common-sdk</artifactId>
        <version>2.0.0</version>
    </dependency>
</dependencies>
```

### 2. 更新导入语句

#### 原来的导入
```java
import com.aixone.session.SessionContext;
import com.aixone.session.TokenParser;
import com.aixone.session.SessionInterceptor;
import com.aixone.session.AbacAttributes;
import com.aixone.session.SessionException;
```

#### 合并后的导入
```java
import com.aixone.common.session.SessionContext;
import com.aixone.common.session.TokenParser;
import com.aixone.common.session.SessionInterceptor;
import com.aixone.common.session.AbacAttributes;
import com.aixone.common.session.SessionException;
```

### 3. 更新配置类

#### 原来的配置
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private SessionInterceptor sessionInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/public/**");
    }
}
```

#### 合并后的配置（自动配置）
```java
// 无需手动配置，SDK 会自动配置 SessionInterceptor
// 如需自定义配置，可以通过 application.yml 进行配置
```

#### 配置文件示例
```yaml
# application.yml
jwt:
  secret: your-jwt-secret-key
  issuer: aixone-tech-auth

multitenant:
  tenant-header: X-Tenant-ID

session:
  require-auth: true
  interceptor-patterns: /**
  exclude-patterns: /public/**,/health
```

### 4. 更新业务代码

#### 会话上下文使用（无需更改）
```java
// 这些方法调用保持不变
String userId = SessionContext.getUserId();
String tenantId = SessionContext.getTenantId();
String clientId = SessionContext.getClientId();
Object department = SessionContext.getAbacAttribute("department");
```

#### Entity 基类使用（无需更改）
```java
// Entity 基类会自动获取租户ID，无需更改
public class User extends Entity<Long> {
    private String name;
    private String email;
    
    public User(Long id, String name, String email) {
        super(id); // 自动设置租户ID
        this.name = name;
        this.email = email;
    }
}
```

## 配置说明

### 自动配置

合并后的 SDK 提供了自动配置功能，无需手动配置 Bean：

- `TokenParser` - 自动配置
- `SessionInterceptor` - 自动配置并注册到 Spring MVC
- `SessionConfig` - 自动配置类

### 配置参数

| 参数 | 默认值 | 说明 |
|------|--------|------|
| `jwt.secret` | `aixone-tech-auth-secret-key-for-jwt-token-generation-and-validation` | JWT 密钥 |
| `jwt.issuer` | `aixone-tech-auth` | JWT 发行者 |
| `multitenant.tenant-header` | `X-Tenant-ID` | 租户ID请求头 |
| `session.require-auth` | `true` | 是否要求认证 |
| `session.interceptor-patterns` | `/**` | 拦截器路径模式 |
| `session.exclude-patterns` | 空 | 排除路径模式 |

## 优势

### 1. 简化依赖管理
- 只需引入一个 SDK
- 减少版本管理复杂度
- 避免依赖冲突

### 2. 统一基础设施
- 提供完整的基础设施能力
- 会话管理和通用功能统一管理
- 更好的架构一致性

### 3. 自动配置
- 无需手动配置 Bean
- 开箱即用的功能
- 减少配置错误

## 注意事项

### 1. 版本兼容性
- 确保使用 `aixone-common-sdk` 2.0.0 或更高版本
- 移除对 `aixone-session-sdk` 的依赖

### 2. 包名变更
- 所有 `com.aixone.session` 包名已变更为 `com.aixone.common.session`
- 需要更新所有相关的导入语句

### 3. 配置变更
- 某些配置参数名称可能发生变化
- 请参考配置说明更新配置文件

## 回滚方案

如果需要回滚到分离的 SDK 架构：

1. 恢复对 `aixone-session-sdk` 的依赖
2. 更新导入语句回到 `com.aixone.session`
3. 恢复手动配置代码
4. 降级 `aixone-common-sdk` 版本

## 支持

如果在迁移过程中遇到问题，请：

1. 检查依赖版本是否正确
2. 确认导入语句已更新
3. 验证配置文件格式
4. 查看日志中的错误信息

## 总结

合并后的 `aixone-common-sdk` 提供了更完整、更统一的基础设施能力，简化了依赖管理，提高了开发效率。迁移过程相对简单，主要是更新导入语句和依赖配置。
