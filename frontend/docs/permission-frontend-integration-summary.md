# 权限模块前端集成总结

## 已完成的工作

### 1. ✅ API 客户端创建

已为权限模块创建了完整的 API 客户端文件：

- ✅ `frontend/workspace/web/src/api/backend/permission/permission.ts` - 权限管理 API（包含权限CRUD、角色权限关联操作）
- ✅ `frontend/workspace/web/src/api/backend/permission/permissionRule.ts` - 权限规则管理 API

所有 API 客户端都遵循以下规范：
- 租户ID从token自动获取，无需显式传递
- 支持分页参数：`pageNum/pageSize` 和 `page/limit`（兼容baTable）
- 完整的CRUD操作
- 关联操作（角色-权限）

### 2. ✅ 代理配置

已在 `frontend/workspace/web/vite.config.ts` 中添加了权限模块的代理配置：

```typescript
'/api/v1/permissions': { target: 'http://localhost:8081', ... }
'/api/v1/permissions/data': { target: 'http://localhost:8081', ... }
```

### 3. ✅ API 导出

已在 `frontend/workspace/web/src/api/backend/index.ts` 中导出了权限模块的 API。

### 4. ✅ 国际化支持

已为权限模块创建了中英文翻译文件：

**中文翻译：**
- `frontend/shared/backend/zh-cn/permission/permission.ts`
- `frontend/shared/backend/zh-cn/permission/permissionRule.ts`

**英文翻译：**
- `frontend/shared/backend/en/permission/permission.ts`
- `frontend/shared/backend/en/permission/permissionRule.ts`

### 5. ✅ 前端页面组件创建

已为权限模块创建了管理页面组件：

#### 权限管理（Permission）
- ✅ `permission/permission/index.vue` - 权限列表页（普通列表）
- ✅ `permission/permission/popupForm.vue` - 权限表单弹窗（包含权限类型、ABAC条件等）

#### 权限规则管理（PermissionRule）
- ✅ `permission/permissionRule/index.vue` - 权限规则列表页（普通列表）
- ✅ `permission/permissionRule/popupForm.vue` - 权限规则表单弹窗（包含路径模式、HTTP方法、优先级等）

## API 接口说明

### 权限管理 API (`/api/v1/permissions/data`)

- `GET /api/v1/permissions/data` - 获取权限列表（分页，支持resource、action过滤）
- `GET /api/v1/permissions/data/{permissionId}` - 获取权限详情
- `POST /api/v1/permissions/data` - 创建权限
- `PUT /api/v1/permissions/data/{permissionId}` - 更新权限
- `DELETE /api/v1/permissions/data/{permissionId}` - 删除权限
- `POST /api/v1/permissions/data/roles/{roleId}/permissions` - 分配权限给角色
- `DELETE /api/v1/permissions/data/roles/{roleId}/permissions/{permissionId}` - 移除角色权限
- `GET /api/v1/permissions/data/roles/{roleId}/permissions` - 获取角色的权限列表

### 权限规则管理 API (`/api/v1/permissions`)

- `GET /api/v1/permissions` - 获取权限规则列表（分页，支持pattern、method过滤）
- `GET /api/v1/permissions/{permissionRuleId}` - 获取权限规则详情
- `POST /api/v1/permissions` - 创建权限规则
- `PUT /api/v1/permissions/{permissionRuleId}` - 更新权限规则
- `DELETE /api/v1/permissions/{permissionRuleId}` - 删除权限规则

## 权限数据模型

### Permission（权限）

- `permissionId`: 权限ID
- `tenantId`: 租户ID
- `name`: 权限名称
- `code`: 权限编码（唯一）
- `resource`: 资源标识
- `action`: 操作标识（read、write、delete、execute、manage）
- `type`: 权限类型（FUNCTIONAL功能权限、DATA数据权限）
- `description`: 描述
- `abacConditions`: ABAC条件（JSON格式，可选）
- `createdAt`: 创建时间
- `updatedAt`: 更新时间

### PermissionRule（权限规则）

- `id`: 规则ID
- `tenantId`: 租户ID
- `pattern`: 路径模式（支持Ant路径匹配，如 `/api/v1/users/**`）
- `methods`: HTTP方法数组（GET、POST、PUT、DELETE等）
- `permission`: 权限标识（格式：`resource:action` 或 `admin:resource:action`）
- `description`: 描述
- `enabled`: 启用状态
- `priority`: 优先级（数字越大优先级越高）
- `createdAt`: 创建时间
- `updatedAt`: 更新时间

## 使用说明

### API 调用示例

```typescript
import { 
    getPermissionList, 
    createPermission, 
    updatePermission, 
    deletePermission,
    assignRolePermission,
    removeRolePermission,
    getRolePermissions
} from '/@/api/backend/permission/permission'

// 获取权限列表
const permissions = await getPermissionList({ pageNum: 1, pageSize: 20, resource: 'user' })

// 创建权限
const newPermission = await createPermission({
    name: '用户查看',
    code: 'user:read',
    resource: 'user',
    action: 'read',
    type: 'FUNCTIONAL',
    description: '查看用户信息'
})

// 更新权限
await updatePermission(permissionId, { name: '新权限名称' })

// 删除权限
await deletePermission(permissionId)

// 分配权限给角色
await assignRolePermission(roleId, permissionId)

// 移除角色权限
await removeRolePermission(roleId, permissionId)

// 获取角色权限列表
const rolePermissions = await getRolePermissions(roleId)
```

### 权限规则 API 调用示例

```typescript
import { 
    getPermissionRuleList, 
    createPermissionRule, 
    updatePermissionRule, 
    deletePermissionRule
} from '/@/api/backend/permission/permissionRule'

// 获取权限规则列表
const rules = await getPermissionRuleList({ pageNum: 1, pageSize: 20, pattern: '/api/v1/users/**' })

// 创建权限规则
const newRule = await createPermissionRule({
    pattern: '/api/v1/users/**',
    methods: ['GET', 'POST'],
    permission: 'user:read',
    description: '用户相关接口权限规则',
    enabled: true,
    priority: 100
})

// 更新权限规则
await updatePermissionRule(ruleId, { enabled: false })

// 删除权限规则
await deletePermissionRule(ruleId)
```

### 页面组件使用

所有页面组件都使用 `baTable` 进行数据管理，支持：
- 分页查询
- 条件搜索
- 新增/编辑/删除
- 批量操作
- 列显示控制

**权限管理页面**支持：
- 按资源、操作过滤
- 权限类型显示（功能权限/数据权限）
- ABAC条件编辑（JSON格式）

**权限规则管理页面**支持：
- 按路径模式、HTTP方法过滤
- 多选HTTP方法
- 优先级设置
- 启用/禁用状态切换

## 注意事项

1. **租户ID自动获取**：所有API接口的`tenantId`都从JWT token自动获取，不需要在请求中显式传递。

2. **分页参数兼容**：支持两种分页参数格式：
   - `pageNum/pageSize`（标准格式）
   - `page/limit`（兼容baTable格式）

3. **权限编码唯一性**：权限编码（code）在同一租户内必须唯一。

4. **ABAC条件格式**：ABAC条件必须是有效的JSON格式，在表单中以文本形式编辑。

5. **权限规则优先级**：数字越大优先级越高，用于匹配多个规则时的优先级判断。

6. **路径模式匹配**：支持Ant路径匹配语法：
   - `*` 匹配单层路径
   - `**` 匹配多层路径
   - 例如：`/api/v1/users/**` 匹配所有以 `/api/v1/users/` 开头的路径

## 下一步

1. 在 Directory 服务的菜单管理中配置权限模块的路由菜单项：
   - 权限管理：`/admin/permission/permission`
   - 权限规则管理：`/admin/permission/permissionRule`

2. 测试所有接口的可用性

3. 测试页面组件的功能（CRUD、搜索、分页等）

4. 验证权限规则的路由匹配功能

