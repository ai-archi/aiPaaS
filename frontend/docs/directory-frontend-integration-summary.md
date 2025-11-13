# Directory 服务前端集成总结

## 已完成的工作

### 1. ✅ API 客户端创建

已为所有模块创建了完整的 API 客户端文件：

- ✅ `frontend/workspace/web/src/api/backend/user/user.ts` - 用户模块 API（包含用户CRUD、部门/岗位/角色关联操作）
- ✅ `frontend/workspace/web/src/api/backend/role/role.ts` - 角色模块 API
- ✅ `frontend/workspace/web/src/api/backend/organization/organization.ts` - 组织模块 API
- ✅ `frontend/workspace/web/src/api/backend/department/department.ts` - 部门模块 API
- ✅ `frontend/workspace/web/src/api/backend/position/position.ts` - 岗位模块 API
- ✅ `frontend/workspace/web/src/api/backend/group/group.ts` - 群组模块 API（包含成员和角色关联操作）

所有 API 客户端都遵循以下规范：
- 租户ID从token自动获取，无需显式传递
- 支持分页参数：`pageNum/pageSize` 和 `page/limit`（兼容baTable）
- 完整的CRUD操作
- 关联操作（用户-部门、用户-岗位、用户-角色、群组-成员、群组-角色）

### 2. ✅ 代理配置

已在 `frontend/workspace/web/vite.config.ts` 中添加了所有模块的代理配置：

```typescript
'/api/v1/users': { target: 'http://localhost:8081', ... }
'/api/v1/roles': { target: 'http://localhost:8081', ... }
'/api/v1/organizations': { target: 'http://localhost:8081', ... }
'/api/v1/departments': { target: 'http://localhost:8081', ... }
'/api/v1/positions': { target: 'http://localhost:8081', ... }
'/api/v1/groups': { target: 'http://localhost:8081', ... }
```

### 3. ✅ API 导出

已在 `frontend/workspace/web/src/api/backend/index.ts` 中导出了所有模块的 API。

### 4. ✅ 国际化支持

已为所有模块创建了中英文翻译文件：

**中文翻译：**
- `frontend/shared/backend/zh-cn/user/user.ts`
- `frontend/shared/backend/zh-cn/role/role.ts`
- `frontend/shared/backend/zh-cn/organization/organization.ts`
- `frontend/shared/backend/zh-cn/department/department.ts`
- `frontend/shared/backend/zh-cn/position/position.ts`
- `frontend/shared/backend/zh-cn/group/group.ts`

**英文翻译：**
- `frontend/shared/backend/en/user/user.ts`
- `frontend/shared/backend/en/role/role.ts`
- `frontend/shared/backend/en/organization/organization.ts`
- `frontend/shared/backend/en/department/department.ts`
- `frontend/shared/backend/en/position/position.ts`
- `frontend/shared/backend/en/group/group.ts`

### 5. ✅ 前端页面组件创建

已为所有模块创建了管理页面组件，**正确区分了列表和树表**：

#### 树表（Tree Table）- 有层级关系
- ✅ **部门模块** (`department/department/index.vue`) - 树表，支持父子部门层级展示
  - 设置了 `expandAll: false`
  - 实现了 `buildDepartmentTree` 函数将平铺数据转换为树形结构
  - 重写了 `after.getData` 方法

#### 列表（Table）- 平铺结构
- ✅ **用户模块** (`user/user/index.vue`) - 普通列表
- ✅ **角色模块** (`role/role/index.vue`) - 普通列表
- ✅ **组织模块** (`organization/organization/index.vue`) - 普通列表
- ✅ **岗位模块** (`position/position/index.vue`) - 普通列表
- ✅ **群组模块** (`group/group/index.vue`) - 普通列表

#### 表单组件（PopupForm）
已为所有模块创建了表单弹窗组件：
- ✅ `department/department/popupForm.vue` - 部门表单（包含组织选择、父部门选择）
- ✅ `user/user/popupForm.vue` - 用户表单（包含组织、部门选择、状态选择）
- ✅ `role/role/popupForm.vue` - 角色表单
- ✅ `organization/organization/popupForm.vue` - 组织表单
- ✅ `position/position/popupForm.vue` - 岗位表单（包含组织选择）
- ✅ `group/group/popupForm.vue` - 群组表单

## 列表 vs 树表说明

### 树表（Tree Table）
**适用场景**：有层级关系的数据
- **部门模块**：部门有父子关系，需要树形展示
- **菜单模块**：菜单有父子关系，需要树形展示

**实现方式**：
```typescript
{
    expandAll: false,  // 启用树形表格
}
// 重写 after.getData 方法，将平铺数据转换为树形结构
baTable.after.getData = (res: any) => {
    const flatData = res.data?.list || []
    const treeData = buildTree(flatData)  // 转换为树形结构
    baTable.table.data = treeData
}
```

### 列表（Table）
**适用场景**：平铺结构的数据
- **用户模块**：用户列表，无层级关系
- **角色模块**：角色列表，无层级关系
- **组织模块**：组织列表，无层级关系
- **岗位模块**：岗位列表，无层级关系
- **群组模块**：群组列表，无层级关系

**实现方式**：
```typescript
// 不需要设置 expandAll，直接使用后端返回的列表数据
baTable.mount()
baTable.getData()
```

## 待完成的工作

### 1. ⏳ 路由配置

路由通过菜单动态加载，需要在 Directory 服务的菜单管理中配置各模块的路由菜单项。每个模块需要添加对应的菜单项，指向相应的页面组件。

**菜单配置示例**：
- 用户管理：`/admin/user/user`
- 角色管理：`/admin/role/role`
- 组织管理：`/admin/organization/organization`
- 部门管理：`/admin/department/department`
- 岗位管理：`/admin/position/position`
- 群组管理：`/admin/group/group`

### 2. ⏳ 国际化文件注册

需要在国际化配置文件中注册新创建的翻译文件，确保翻译能够正常加载。检查 `frontend/shared/globs-zh-cn.ts` 和 `frontend/shared/globs-en.ts` 是否需要更新。

## 使用说明

### API 调用示例

```typescript
import { getUserList, createUser, updateUser, deleteUser } from '/@/api/backend/user/user'

// 获取用户列表
const users = await getUserList({ pageNum: 1, pageSize: 20 })

// 创建用户
const newUser = await createUser({
    username: 'testuser',
    email: 'test@example.com',
    password: 'password123'
})

// 更新用户
await updateUser(userId, { username: 'newusername' })

// 删除用户
await deleteUser(userId)

// 用户关联操作
import { addUserRole, removeUserRole, getUserRoles } from '/@/api/backend/user/user'

// 获取用户角色
const roles = await getUserRoles(userId)

// 添加角色
await addUserRole(userId, roleId)

// 移除角色
await removeUserRole(userId, roleId)
```

### 页面组件使用

所有页面组件都使用 `baTable` 进行数据管理，支持：
- 分页查询
- 条件搜索
- 新增/编辑/删除
- 批量操作
- 列显示控制

**树表组件**（部门）会自动将平铺数据转换为树形结构展示。

**列表组件**（用户、角色、组织、岗位、群组）直接展示平铺的列表数据。

### 注意事项

1. **租户ID自动获取**：所有API接口的`tenantId`都从JWT token自动获取，不需要在请求中显式传递。

2. **分页参数兼容**：支持两种分页参数格式：
   - `pageNum/pageSize`（标准格式）
   - `page/limit`（兼容baTable格式）

3. **关联操作规范**：
   - 查询子资源：`GET /api/v1/users/{userId}/roles`
   - 单个关联操作：`PUT` 添加，`DELETE` 删除
   - 批量替换：`PUT /api/v1/users/{userId}/roles` 带请求体

4. **错误处理**：所有API调用都会自动处理错误，并在需要时显示错误提示。

5. **树表 vs 列表**：
   - 树表需要设置 `expandAll: false` 并实现数据转换逻辑
   - 列表直接使用后端返回的数据，无需特殊处理

## 下一步

1. 在 Directory 服务的菜单管理中配置各模块的路由菜单项
2. 检查并注册国际化文件（如果需要）
3. 测试所有接口的可用性
4. 测试页面组件的功能（CRUD、搜索、分页等）
