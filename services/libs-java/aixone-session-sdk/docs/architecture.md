# aixone-session 公共包架构设计

## 一、定位与目标

aixone-session 是平台级统一上下文管理组件，面向多模块/多微服务，提供 Token 解析、租户隔离、ABAC 权限属性等请求上下文的统一提取、存储与访问能力。其目标是：
- 统一管理用户、租户、权限属性等上下文信息
- 支持多租户、ABAC 权限、分布式场景下的上下文一致性
- 降低各业务模块对上下文处理的重复开发和安全风险

## 二、核心功能
1. Token 解析（支持 JWT、Opaque Token 等），提取用户ID、角色、租户ID等
2. 租户ID自动注入与隔离，支持多租户场景
3. ABAC（属性基）权限相关属性的统一提取与存储
4. ThreadLocal 上下文管理，保证请求级隔离
5. Spring 拦截器统一注入上下文，自动清理
6. 提供便捷的上下文访问 API
7. 支持异常处理与安全校验

## 三、领域模型
- **SessionContext**：保存当前请求的用户ID、租户ID、角色、ABAC属性等
- **AbacAttributes**：可扩展的属性对象，支持部门、岗位、标签等
- **TokenParser**：Token 解析工具，支持多种格式
- **SessionInterceptor**：Spring 拦截器，统一注入/清理上下文

## 四、接口设计
- `SessionContext.getUserId()` 获取当前用户ID
- `SessionContext.getTenantId()` 获取当前租户ID
- `SessionContext.getRoles()` 获取当前用户角色
- `SessionContext.getAbacAttributes()` 获取ABAC属性
- `SessionContext.clear()` 清理上下文

## 五、与权限/租户/ABAC的关系
- 权限服务（如 aixone-permission）只需依赖 SessionContext 提供的上下文属性，无需关心 Token/租户/属性的解析细节
- 业务服务通过 SessionContext 统一获取租户、用户、权限属性，实现多租户和细粒度权限控制

## 六、典型调用方式
```java
String userId = SessionContext.getUserId();
String tenantId = SessionContext.getTenantId();
AbacAttributes abac = SessionContext.getAbacAttributes();
```

## 七、扩展性与最佳实践
- 支持自定义 Token 解析器、ABAC 属性扩展
- 可与 Spring Security、OAuth2、微服务网关等集成
- 统一异常处理，提升安全性和一致性
- 便于后续扩展更多上下文属性（如地理位置、设备信息等）

## 八、结论

aixone-session 作为平台级基础组件，极大提升了多租户、权限、审计等场景下的上下文一致性、安全性和开发效率，是现代分布式系统的最佳实践。
