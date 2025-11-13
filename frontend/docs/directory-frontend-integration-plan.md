# Directory 服务前端集成计划

## 一、概述

本文档描述 Directory 服务所有模块与前端集成的计划，包括 API 客户端、页面组件、路由配置等。

## 二、模块列表

Directory 服务包含以下模块，需要与前端集成：

1. ✅ **菜单模块（Menu）** - 已完成
2. ✅ **租户模块（Tenant）** - 已完成
3. ✅ **租户组模块（TenantGroup）** - 已完成
4. ⏳ **用户模块（User）** - 待集成
5. ⏳ **角色模块（Role）** - 待集成
6. ⏳ **组织模块（Organization）** - 待集成
7. ⏳ **部门模块（Department）** - 待集成
8. ⏳ **岗位模块（Position）** - 待集成
9. ⏳ **群组模块（Group）** - 待集成

## 三、集成计划

### 3.1 API 客户端创建

为每个模块创建对应的 API 客户端文件：

- `frontend/workspace/web/src/api/backend/user/user.ts` - 用户模块 API
- `frontend/workspace/web/src/api/backend/role/role.ts` - 角色模块 API
- `frontend/workspace/web/src/api/backend/organization/organization.ts` - 组织模块 API
- `frontend/workspace/web/src/api/backend/department/department.ts` - 部门模块 API
- `frontend/workspace/web/src/api/backend/position/position.ts` - 岗位模块 API
- `frontend/workspace/web/src/api/backend/group/group.ts` - 群组模块 API

### 3.2 页面组件创建

为每个模块创建管理页面：

- `frontend/workspace/web/src/views/backend/user/user/index.vue` - 用户列表页
- `frontend/workspace/web/src/views/backend/user/user/popupForm.vue` - 用户表单弹窗
- `frontend/workspace/web/src/views/backend/role/role/index.vue` - 角色列表页
- `frontend/workspace/web/src/views/backend/role/role/popupForm.vue` - 角色表单弹窗
- `frontend/workspace/web/src/views/backend/organization/organization/index.vue` - 组织列表页
- `frontend/workspace/web/src/views/backend/organization/organization/popupForm.vue` - 组织表单弹窗
- `frontend/workspace/web/src/views/backend/department/department/index.vue` - 部门列表页
- `frontend/workspace/web/src/views/backend/department/department/popupForm.vue` - 部门表单弹窗
- `frontend/workspace/web/src/views/backend/position/position/index.vue` - 岗位列表页
- `frontend/workspace/web/src/views/backend/position/position/popupForm.vue` - 岗位表单弹窗
- `frontend/workspace/web/src/views/backend/group/group/index.vue` - 群组列表页
- `frontend/workspace/web/src/views/backend/group/group/popupForm.vue` - 群组表单弹窗

### 3.3 路由配置

在路由配置中添加各模块的路由：

- 用户管理路由
- 角色管理路由
- 组织管理路由
- 部门管理路由
- 岗位管理路由
- 群组管理路由

### 3.4 代理配置

在 `vite.config.ts` 中添加各模块的代理配置：

```typescript
'/api/v1/users': {
    target: 'http://localhost:8081',
    changeOrigin: true,
    secure: false,
},
'/api/v1/roles': {
    target: 'http://localhost:8081',
    changeOrigin: true,
    secure: false,
},
// ... 其他模块
```

### 3.5 国际化支持

添加各模块的中英文翻译：

- `frontend/shared/backend/zh-cn/user/user.ts`
- `frontend/shared/backend/en/user/user.ts`
- `frontend/shared/backend/zh-cn/role/role.ts`
- `frontend/shared/backend/en/role/role.ts`
- // ... 其他模块

## 四、执行步骤

1. ✅ 分析 Directory 服务所有模块和 API 接口
2. ⏳ 创建前端 API 客户端（用户、角色、组织、部门、岗位、群组模块）
3. ⏳ 创建前端页面组件（各模块管理页面）
4. ⏳ 配置前端路由（添加各模块路由）
5. ⏳ 配置前端代理（更新 vite.config.ts 代理配置）
6. ⏳ 添加国际化支持（中英文翻译）
7. ⏳ 测试前端集成（验证所有接口可用）

## 五、API 接口规范

所有 API 接口遵循以下规范：

- **基础路径**：`/api/v1/{resource}`
- **租户ID**：从 token 自动获取，无需显式传递
- **分页参数**：支持 `pageNum/pageSize` 和 `page/limit`（兼容 baTable）
- **响应格式**：统一使用 `ApiResponse<T>` 格式

## 六、注意事项

1. 所有业务接口的 `tenantId` 都从 token 自动获取，不需要在请求中传递
2. 管理接口（`/api/v1/admin/*`）需要权限验证，tenantId 通过查询参数传递
3. 关联操作遵循 RESTful 规范：
   - 查询子资源：`GET /api/v1/users/{userId}/roles`
   - 单个关联操作：`PUT /api/v1/users/{userId}/roles/{roleId}` 添加，`DELETE` 删除
   - 批量替换：`PUT /api/v1/users/{userId}/roles` 带请求体

