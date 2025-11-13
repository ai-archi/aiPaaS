# Directory服务权限模块研发计划

## 一、项目概述

### 1.1 项目目标
为Directory服务增加权限模块，支持RBAC（基于角色的访问控制）和ABAC（基于属性的访问控制）混合权限模型，提供统一的权限管理、权限决策、权限校验和权限过滤能力。

### 1.2 核心功能
1. **权限规则管理**：管理接口的权限验证规则（路径-权限映射）
2. **权限数据管理**：权限管理、角色-权限关系管理、权限继承
3. **权限决策引擎**：RBAC权限决策、ABAC权限决策、混合权限决策
4. **权限校验接口**：功能级权限校验、数据级权限校验、批量权限校验
5. **权限过滤能力**：菜单权限过滤、资源权限过滤、数据权限过滤

### 1.3 技术栈
- **后端框架**：Spring Boot 3.x
- **数据库**：PostgreSQL
- **缓存**：Redis
- **架构模式**：DDD（领域驱动设计）分层架构
- **权限模型**：RBAC + ABAC混合模型

## 二、研发阶段规划

### 阶段1：权限规则管理模块（预计3-5天）

#### 1.1 数据库设计和迁移脚本
- [ ] 创建 `permission_rules` 表
  - `id` (UUID, 主键)
  - `tenant_id` (VARCHAR, 租户ID)
  - `pattern` (VARCHAR, 路径模式，支持Ant路径匹配)
  - `methods` (VARCHAR[], HTTP方法数组)
  - `permission` (VARCHAR, 权限标识，如 `admin:menu:read`)
  - `description` (TEXT, 描述)
  - `enabled` (BOOLEAN, 是否启用)
  - `priority` (INTEGER, 优先级)
  - `created_at` (TIMESTAMP)
  - `updated_at` (TIMESTAMP)
- [ ] 创建索引：`tenant_id`, `pattern`, `permission`
- [ ] 编写Flyway迁移脚本

#### 1.2 领域模型和仓储层
- [ ] 创建 `PermissionRule` 领域实体
- [ ] 创建 `PermissionRuleRepository` 领域仓储接口
- [ ] 创建 `PermissionRuleDbo` 数据对象
- [ ] 创建 `JpaPermissionRuleRepository` JPA仓储实现
- [ ] 创建 `PermissionRuleMapper` (MapStruct) 映射器

#### 1.3 应用服务和REST接口
- [ ] 创建 `PermissionRuleApplicationService` 应用服务
  - `createPermissionRule(command)`
  - `updatePermissionRule(ruleId, command)`
  - `deletePermissionRule(ruleId)`
  - `findPermissionRuleByPath(path, method)`
  - `findPermissionRules(pageRequest, filters)`
- [ ] 创建 `PermissionRuleController` REST控制器
  - `GET /api/v1/permissions` - 获取权限规则列表
  - `GET /api/v1/permissions/{permissionId}` - 获取权限规则详情
  - `POST /api/v1/permissions` - 创建权限规则
  - `PUT /api/v1/permissions/{permissionId}` - 更新权限规则
  - `DELETE /api/v1/permissions/{permissionId}` - 删除权限规则

#### 1.4 权限拦截器
- [ ] 创建 `PermissionInterceptor` 拦截器
  - 从权限规则表查询匹配的权限规则
  - 支持Ant路径匹配（`**`, `*`）
  - 支持HTTP方法匹配
  - 从SessionContext获取用户信息
  - 调用权限决策引擎进行权限验证
  - 权限验证失败返回403
- [ ] 配置拦截器拦截管理接口（`/api/v1/admin/**`）

### 阶段2：权限数据管理模块（预计4-6天）

#### 2.1 数据库设计和迁移脚本
- [ ] 创建 `permissions` 表
  - `permission_id` (UUID, 主键)
  - `tenant_id` (VARCHAR, 租户ID)
  - `name` (VARCHAR, 权限名称)
  - `code` (VARCHAR, 权限编码，唯一)
  - `resource` (VARCHAR, 资源标识)
  - `action` (VARCHAR, 操作标识)
  - `type` (VARCHAR, 权限类型：FUNCTIONAL/DATA)
  - `description` (TEXT, 描述)
  - `abac_conditions` (JSONB, ABAC条件)
  - `created_at` (TIMESTAMP)
  - `updated_at` (TIMESTAMP)
- [ ] 创建 `role_permissions` 表
  - `role_id` (UUID, 角色ID)
  - `permission_id` (UUID, 权限ID)
  - `tenant_id` (VARCHAR, 租户ID)
  - `created_at` (TIMESTAMP)
- [ ] 创建索引和唯一约束
- [ ] 编写Flyway迁移脚本

#### 2.2 领域模型和仓储层
- [ ] 创建 `Permission` 领域实体
- [ ] 创建 `PermissionRepository` 领域仓储接口
- [ ] 创建 `RolePermissionRepository` 领域仓储接口
- [ ] 创建 `PermissionDbo` 和 `RolePermissionDbo` 数据对象
- [ ] 创建JPA仓储实现
- [ ] 创建MapStruct映射器

#### 2.3 应用服务和REST接口
- [ ] 创建 `PermissionApplicationService` 应用服务
  - `createPermission(command)`
  - `updatePermission(permissionId, command)`
  - `deletePermission(permissionId)`
  - `findPermissions(pageRequest, filters)`
  - `assignRolePermission(roleId, permissionId)`
  - `removeRolePermission(roleId, permissionId)`
  - `getRolePermissions(roleId)`
- [ ] 创建 `PermissionController` REST控制器
  - `GET /api/v1/permissions/data` - 获取权限数据列表
  - `GET /api/v1/permissions/data/{permissionId}` - 获取权限数据详情
  - `POST /api/v1/permissions/data` - 创建权限数据
  - `PUT /api/v1/permissions/data/{permissionId}` - 更新权限数据
  - `DELETE /api/v1/permissions/data/{permissionId}` - 删除权限数据
  - `POST /api/v1/roles/{roleId}/permissions` - 分配权限给角色
  - `DELETE /api/v1/roles/{roleId}/permissions/{permissionId}` - 移除角色权限

### 阶段3：权限决策引擎模块（预计5-7天）

#### 3.1 RBAC决策引擎
- [ ] 创建 `RbacDecisionEngine` 类
  - `checkPermission(userId, resource, action)`
  - 获取用户角色列表
  - 获取角色权限列表
  - 检查用户是否拥有所需权限
  - 支持权限继承（角色权限继承）
- [ ] 创建 `UserPermissionProvider` 接口实现
  - `getUserRoles(userId, tenantId)`
  - `getUserPermissions(userId, tenantId)`
  - `getRolePermissions(roleId, tenantId)`

#### 3.2 ABAC决策引擎
- [ ] 创建 `AbacDecisionEngine` 类
  - `checkPermission(userId, resource, action, context)`
  - 获取用户ABAC属性
  - 获取资源ABAC属性
  - 获取环境ABAC属性
  - 执行ABAC条件判断（JSON条件解析和评估）
- [ ] 创建 `AbacAttributeProvider` 接口
  - `getUserAttributes(userId, tenantId)`
  - `getResourceAttributes(resource, tenantId)`
  - `getEnvironmentAttributes()`

#### 3.3 混合决策引擎
- [ ] 创建 `PermissionDecisionService` 类
  - `checkPermission(userId, resource, action, context?)`
  - 先执行RBAC权限决策
  - 如果RBAC通过，执行ABAC权限决策（如果有ABAC条件）
  - 合并权限决策结果（AND逻辑）
  - 支持权限组合（AND/OR逻辑）
  - 支持权限否定（NOT逻辑）

### 阶段4：权限校验模块（预计3-4天）

#### 4.1 权限校验接口
- [ ] 创建 `PermissionValidationService` 应用服务
  - `validatePermission(userId, resource, action, context?)`
  - `validatePermissions(userId, permissions, context?)`
  - `getUserEffectivePermissions(userId)`
- [ ] 创建 `PermissionValidationController` REST控制器
  - `POST /api/v1/permissions/check` - 单权限校验
    - 请求体：`{userId, resource, action, context?}`
    - 返回：`{hasPermission: boolean}`
  - `POST /api/v1/permissions/check-batch` - 批量权限校验
    - 请求体：`{userId, permissions: [{resource, action}], context?}`
    - 返回：`{results: [{resource, action, hasPermission}]}`

### 阶段5：权限过滤模块（预计3-4天）

#### 5.1 菜单权限过滤
- [ ] 在 `MenuApplicationService.findMenus` 中集成权限过滤
  - 获取用户权限列表
  - 检查菜单的可见角色和所需权限
  - 执行权限决策（RBAC/ABAC）
  - 过滤无权限的菜单
- [ ] 确保所有菜单查询接口自动应用权限过滤

#### 5.2 资源权限过滤
- [ ] 创建 `ResourcePermissionFilterService` 应用服务
  - `filterResourcesByPermission(userId, resources, context?)`
  - 支持基于组织的数据权限
  - 支持基于资源所有者的数据权限
  - 支持基于ABAC条件的数据权限
- [ ] 创建 `ResourcePermissionFilterController` REST控制器
  - `POST /api/v1/resources/filter` - 资源权限过滤
    - 请求体：`{userId, resources: [{id, type, ...}], context?}`
    - 返回：`{filteredResources: [...]}`

### 阶段6：缓存优化（预计2-3天）

#### 6.1 权限缓存实现
- [ ] 创建 `PermissionCacheService` 缓存服务
  - 用户权限缓存：`directory:user:{userId}:permissions:{tenantId}`
  - 角色权限缓存：`directory:role:{roleId}:permissions:{tenantId}`
  - 权限信息缓存：`directory:permission:{permissionId}:{tenantId}`
  - 权限规则缓存：`directory:permission:rule:{pattern}:{method}`
  - 权限校验结果缓存：`directory:permission:check:{userId}:{resource}:{action}:{tenantId}`
- [ ] 实现缓存更新策略
  - 权限变更时清除相关缓存
  - 角色权限变更时清除用户权限缓存
  - 权限规则变更时清除规则缓存

### 阶段7：单元测试和集成测试（预计3-4天）

#### 7.1 单元测试
- [ ] 权限规则管理模块单元测试
- [ ] 权限数据管理模块单元测试
- [ ] RBAC决策引擎单元测试
- [ ] ABAC决策引擎单元测试
- [ ] 混合决策引擎单元测试
- [ ] 权限校验模块单元测试
- [ ] 权限过滤模块单元测试

#### 7.2 集成测试
- [ ] 权限规则管理接口集成测试
- [ ] 权限数据管理接口集成测试
- [ ] 权限校验接口集成测试
- [ ] 权限过滤接口集成测试
- [ ] 权限拦截器集成测试
- [ ] 菜单权限过滤集成测试

## 三、技术要点

### 3.1 权限规则匹配
- 使用Ant路径匹配模式（`**`, `*`）
- 支持HTTP方法匹配（GET, POST, PUT, DELETE等）
- 支持优先级排序（priority字段）
- 支持启用/禁用（enabled字段）

### 3.2 RBAC权限决策
- 基于用户角色和权限进行验证
- 支持角色权限继承
- 支持权限组合（AND/OR逻辑）

### 3.3 ABAC权限决策
- 基于用户属性、资源属性、环境属性进行验证
- 支持JSON条件解析和评估
- 支持复杂条件组合

### 3.4 权限缓存策略
- 用户权限缓存：24小时
- 角色权限缓存：24小时
- 权限信息缓存：24小时
- 权限规则缓存：1小时
- 权限校验结果缓存：1小时

### 3.5 性能优化
- 权限校验响应时间目标：< 50ms
- 使用Redis缓存提升查询性能
- 数据库索引优化
- 批量查询优化

## 四、开发规范

### 4.1 代码结构
```
services/aixone-app-directory/
├── src/main/java/com/aixone/directory/
│   ├── permission/
│   │   ├── domain/
│   │   │   ├── aggregate/
│   │   │   │   ├── PermissionRule.java
│   │   │   │   └── Permission.java
│   │   │   ├── repository/
│   │   │   │   ├── PermissionRuleRepository.java
│   │   │   │   └── PermissionRepository.java
│   │   │   └── service/
│   │   │       ├── RbacDecisionEngine.java
│   │   │       ├── AbacDecisionEngine.java
│   │   │       └── PermissionDecisionService.java
│   │   ├── application/
│   │   │   ├── service/
│   │   │   │   ├── PermissionRuleApplicationService.java
│   │   │   │   ├── PermissionApplicationService.java
│   │   │   │   ├── PermissionValidationService.java
│   │   │   │   └── ResourcePermissionFilterService.java
│   │   │   └── dto/
│   │   │       ├── PermissionRuleDto.java
│   │   │       └── PermissionDto.java
│   │   ├── infrastructure/
│   │   │   ├── persistence/
│   │   │   │   ├── dbo/
│   │   │   │   │   ├── PermissionRuleDbo.java
│   │   │   │   │   └── PermissionDbo.java
│   │   │   │   └── repository/
│   │   │   │       ├── JpaPermissionRuleRepository.java
│   │   │   │       └── JpaPermissionRepository.java
│   │   │   ├── cache/
│   │   │   │   └── PermissionCacheService.java
│   │   │   └── provider/
│   │   │       ├── UserPermissionProviderImpl.java
│   │   │       └── AbacAttributeProviderImpl.java
│   │   └── interfaces/
│   │       ├── rest/
│   │       │   ├── PermissionRuleController.java
│   │       │   ├── PermissionController.java
│   │       │   ├── PermissionValidationController.java
│   │       │   └── ResourcePermissionFilterController.java
│   │       └── interceptor/
│   │           └── PermissionInterceptor.java
│   └── ...
└── src/main/resources/
    └── db/migration/
        ├── V9__Create_permission_rules_table.sql
        └── V10__Create_permissions_table.sql
```

### 4.2 命名规范
- 领域实体：`PermissionRule`, `Permission`
- 应用服务：`PermissionRuleApplicationService`, `PermissionApplicationService`
- REST控制器：`PermissionRuleController`, `PermissionController`
- DTO：`PermissionRuleDto`, `PermissionDto`
- 命令：`CreatePermissionRuleCommand`, `UpdatePermissionRuleCommand`

## 五、验收标准

### 5.1 功能验收
- [ ] 权限规则管理功能完整，支持CRUD操作
- [ ] 权限数据管理功能完整，支持权限和角色-权限关系管理
- [ ] RBAC权限决策功能正常
- [ ] ABAC权限决策功能正常
- [ ] 混合权限决策功能正常
- [ ] 权限校验接口功能正常
- [ ] 菜单权限过滤功能正常
- [ ] 资源权限过滤功能正常

### 5.2 性能验收
- [ ] 权限校验响应时间 < 50ms（缓存命中时 < 10ms）
- [ ] 支持高并发访问（1000+ QPS）
- [ ] 缓存命中率 > 80%

### 5.3 质量验收
- [ ] 单元测试覆盖率 > 80%
- [ ] 集成测试通过率 100%
- [ ] 代码审查通过
- [ ] 文档完整

## 六、风险与应对

### 6.1 技术风险
- **风险**：ABAC条件解析和评估复杂度高
- **应对**：使用成熟的表达式引擎（如SpEL）或JSON条件解析库

### 6.2 性能风险
- **风险**：权限校验性能不达标
- **应对**：优化缓存策略，使用Redis缓存，优化数据库查询

### 6.3 兼容性风险
- **风险**：与现有系统集成问题
- **应对**：充分测试，提供向后兼容的接口

## 七、时间估算

| 阶段 | 预计时间 | 累计时间 |
|------|---------|---------|
| 阶段1：权限规则管理模块 | 3-5天 | 3-5天 |
| 阶段2：权限数据管理模块 | 4-6天 | 7-11天 |
| 阶段3：权限决策引擎模块 | 5-7天 | 12-18天 |
| 阶段4：权限校验模块 | 3-4天 | 15-22天 |
| 阶段5：权限过滤模块 | 3-4天 | 18-26天 |
| 阶段6：缓存优化 | 2-3天 | 20-29天 |
| 阶段7：单元测试和集成测试 | 3-4天 | 23-33天 |

**总计**：预计23-33个工作日（约1.5-2个月）

## 八、下一步行动

1. **立即开始阶段1**：权限规则管理模块开发
2. **并行准备**：数据库设计评审、技术方案评审
3. **持续集成**：每个阶段完成后进行代码审查和测试

