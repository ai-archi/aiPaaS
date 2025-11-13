# Directory 服务 RESTful API 设计规范

## 一、设计原则

### 1.1 安全原则
- **租户ID从token获取**：所有业务接口的租户ID从JWT token的`SessionContext`中自动获取，不在URL或参数中暴露
- **用户ID从token获取**：用户相关接口的用户ID从token中获取，确保用户只能操作自己的数据
- **数据隔离**：普通用户只能访问自己租户的数据，通过`tenantId`自动过滤

### 1.2 资源划分
- **业务接口**：`/api/v1/{resource}` - 普通用户使用，租户ID自动过滤
- **管理接口**：`/api/v1/admin/{resource}` - 管理员使用，可以跨租户操作，需要权限验证

### 1.3 RESTful规范
- 使用标准HTTP方法：GET（查询）、POST（创建）、PUT（更新/替换）、DELETE（删除）
- 使用复数名词作为资源名：`/menus`、`/users`、`/roles`
- 使用路径参数标识资源：`/menus/{menuId}`
- 使用查询参数进行过滤和分页：`?pageNum=1&pageSize=20&name=xxx`
- **关联操作规范**（多对多关系）：
  - ✅ **查询子资源**：`GET /api/v1/users/{userId}/roles` - 合法的子资源查询
  - ✅ **单个关联操作**：`PUT /api/v1/users/{userId}/roles/{roleId}` 添加、`DELETE /api/v1/users/{userId}/roles/{roleId}` 删除
  - ✅ **批量替换集合**：`PUT /api/v1/users/{userId}/roles` 带请求体替换整个集合
  - ❌ **避免使用**：`POST /api/v1/users/{userId}/roles` 进行关联操作，应使用PUT/DELETE

### 1.4 权限控制
- **业务接口**：自动从token获取`tenantId`，无需额外权限验证
- **管理接口**：通过权限模块（Permission Module）进行动态权限验证
- **权限模块**：Directory服务包含权限模块，权限规则存储在数据库中，支持动态配置和管理
- **权限拦截器**：使用统一的权限拦截器，从权限模块查询权限规则进行验证
- **权限缓存**：权限检查结果可以缓存，提高性能

---

## 二、模块接口设计

### 2.1 菜单模块（Menu）

#### 业务接口（租户隔离）

| 接口路径 | HTTP方法 | 功能描述 | 请求参数 | 请求体 | 返回数据 | 租户ID来源 |
|---------|---------|---------|---------|--------|---------|-----------|
| `/api/v1/menus` | GET | 获取当前租户的菜单列表（分页） | `pageNum`, `pageSize`, `name`, `title`, `type`, `order` | - | `PageResult<MenuDto>` | Token |
| `/api/v1/menus/{menuId}` | GET | 获取菜单详情 | `menuId` (路径) | - | `MenuDto` | Token |
| `/api/v1/menus` | POST | 创建菜单 | - | `CreateMenuCommand` | `MenuDto` | Token |
| `/api/v1/menus/{menuId}` | PUT | 更新菜单 | `menuId` (路径) | `UpdateMenuCommand` | `MenuDto` | Token |
| `/api/v1/menus/{menuId}` | DELETE | 删除菜单 | `menuId` (路径) | - | `Void` | Token |
| `/api/v1/menus/{menuId}/children` | GET | 获取菜单的子菜单 | `menuId` (路径) | - | `List<MenuDto>` | Token |

**说明**：
- 获取根菜单：使用 `/api/v1/menus?parentId=null` 或 `/api/v1/menus?parentId=`（空字符串）
- 不需要单独的 `/api/v1/menus/roots` 接口，通过查询参数即可实现

**说明**：
- 所有接口的`tenantId`从`SessionContext.getTenantId()`自动获取
- 查询接口自动过滤当前租户的数据
- 创建/更新接口自动设置`tenantId`为当前租户

#### 管理接口（跨租户）

| 接口路径 | HTTP方法 | 功能描述 | 请求参数 | 请求体 | 返回数据 | 权限要求 |
|---------|---------|---------|---------|--------|---------|---------|
| `/api/v1/admin/menus` | GET | 管理员查询菜单列表（可跨租户） | `tenantId` (查询参数), `pageNum`, `pageSize`, `parentId` | - | `PageResult<MenuDto>` | `admin:menu:read` |
| `/api/v1/admin/menus/{menuId}` | GET | 管理员查询菜单详情（可跨租户） | `menuId` (路径), `tenantId` (查询参数, 可选) | - | `MenuDto` | `admin:menu:read` |
| `/api/v1/admin/menus` | POST | 管理员创建菜单 | `tenantId` (查询参数或请求体) | `CreateMenuCommand` | `MenuDto` | `admin:menu:create` |
| `/api/v1/admin/menus/{menuId}` | PUT | 管理员更新菜单 | `menuId` (路径), `tenantId` (查询参数, 可选) | `UpdateMenuCommand` | `MenuDto` | `admin:menu:update` |
| `/api/v1/admin/menus/{menuId}` | DELETE | 管理员删除菜单 | `menuId` (路径), `tenantId` (查询参数, 可选) | - | `Void` | `admin:menu:delete` |

**说明**：
- 管理员接口使用 `/api/v1/admin/menus`，tenantId通过查询参数传递
- 查询根菜单：`GET /api/v1/admin/menus?tenantId={tenantId}&parentId=null`
- 权限要求通过权限模块动态检查，权限规则存储在数据库中，无需在Controller中硬编码

---

### 2.2 用户模块（User）

#### 业务接口（租户隔离）

| 接口路径 | HTTP方法 | 功能描述 | 请求参数 | 请求体 | 返回数据 | 租户ID来源 |
|---------|---------|---------|---------|--------|---------|-----------|
| `/api/v1/users` | GET | 获取当前租户的用户列表（分页） | `pageNum`, `pageSize`, `name`, `status`, `orgId`, `deptId` | - | `PageResult<UserDto>` | Token |
| `/api/v1/users/{userId}` | GET | 获取用户详情 | `userId` (路径) | - | `UserDto` | Token |
| `/api/v1/users/me` | GET | 获取当前登录用户信息 | - | - | `UserDto` | Token |
| `/api/v1/users` | POST | 创建用户 | - | `CreateUserCommand` | `UserDto` | Token |
| `/api/v1/users/{userId}` | PUT | 更新用户 | `userId` (路径) | `UpdateUserCommand` | `UserDto` | Token |
| `/api/v1/users/{userId}` | DELETE | 删除用户 | `userId` (路径) | - | `Void` | Token |
| `/api/v1/users/{userId}/departments` | GET | 获取用户所属部门 | `userId` (路径) | - | `List<DepartmentDto>` | Token |
| `/api/v1/users/{userId}/departments` | PUT | 更新用户的部门集合（批量替换） | `userId` (路径) | `{deptIds: ["uuid1", "uuid2"]}` | `Void` | Token |
| `/api/v1/users/{userId}/departments/{deptId}` | PUT | 将用户添加到部门 | `userId`, `deptId` (路径) | - | `Void` | Token |
| `/api/v1/users/{userId}/departments/{deptId}` | DELETE | 从部门移除用户 | `userId`, `deptId` (路径) | - | `Void` | Token |
| `/api/v1/users/{userId}/positions` | GET | 获取用户所属岗位 | `userId` (路径) | - | `List<PositionDto>` | Token |
| `/api/v1/users/{userId}/positions` | PUT | 更新用户的岗位集合（批量替换） | `userId` (路径) | `{posIds: ["uuid1", "uuid2"]}` | `Void` | Token |
| `/api/v1/users/{userId}/positions/{posId}` | PUT | 将用户分配到岗位 | `userId`, `posId` (路径) | - | `Void` | Token |
| `/api/v1/users/{userId}/positions/{posId}` | DELETE | 从岗位移除用户 | `userId`, `posId` (路径) | - | `Void` | Token |
| `/api/v1/users/{userId}/roles` | GET | 获取用户的角色列表 | `userId` (路径) | - | `List<RoleDto>` | Token |
| `/api/v1/users/{userId}/roles` | PUT | 更新用户的角色集合（批量替换） | `userId` (路径) | `{roleIds: ["uuid1", "uuid2"]}` | `Void` | Token |
| `/api/v1/users/{userId}/roles/{roleId}` | PUT | 为用户添加角色 | `userId`, `roleId` (路径) | - | `Void` | Token |
| `/api/v1/users/{userId}/roles/{roleId}` | DELETE | 移除用户的角色 | `userId`, `roleId` (路径) | - | `Void` | Token |

**说明**：
- `/api/v1/users/me`：获取当前登录用户（从token中获取userId）
- 所有用户操作自动限制在当前租户内
- 用户只能查看/操作自己租户的用户
- **RESTful规范**：
  - 查询接口：`GET /api/v1/users/{userId}/departments`、`GET /api/v1/users/{userId}/positions`、`GET /api/v1/users/{userId}/roles`
  - 单个关联操作：`PUT /api/v1/users/{userId}/departments/{deptId}` 添加、`DELETE /api/v1/users/{userId}/departments/{deptId}` 删除
  - 批量替换操作：`PUT /api/v1/users/{userId}/departments` 带请求体替换整个集合

#### 管理接口（跨租户）

| 接口路径 | HTTP方法 | 功能描述 | 请求参数 | 请求体 | 返回数据 | 权限要求 |
|---------|---------|---------|---------|--------|---------|---------|
| `/api/v1/admin/users` | GET | 管理员查询用户列表（可跨租户） | `tenantId` (查询参数), `pageNum`, `pageSize`, `name`, `status` | - | `PageResult<UserDto>` | `admin:user:read` |
| `/api/v1/admin/users/{userId}` | GET | 管理员查询用户详情（跨租户） | `userId` (路径), `tenantId` (查询参数, 可选) | - | `UserDto` | `admin:user:read` |
| `/api/v1/admin/users` | POST | 管理员创建用户 | `tenantId` (查询参数或请求体) | `CreateUserCommand` | `UserDto` | `admin:user:create` |
| `/api/v1/admin/users/{userId}` | PUT | 管理员更新用户 | `userId` (路径), `tenantId` (查询参数, 可选) | `UpdateUserCommand` | `UserDto` | `admin:user:update` |
| `/api/v1/admin/users/{userId}` | DELETE | 管理员删除用户 | `userId` (路径), `tenantId` (查询参数, 可选) | - | `Void` | `admin:user:delete` |

**说明**：
- 管理员接口使用 `/api/v1/admin/users`，tenantId通过查询参数传递
- 权限要求通过权限模块动态检查，权限规则存储在数据库中，无需在Controller中硬编码

---

### 2.3 角色模块（Role）

#### 业务接口（租户隔离）

| 接口路径 | HTTP方法 | 功能描述 | 请求参数 | 请求体 | 返回数据 | 租户ID来源 |
|---------|---------|---------|---------|--------|---------|-----------|
| `/api/v1/roles` | GET | 获取当前租户的角色列表（分页） | `pageNum`, `pageSize`, `name` | - | `PageResult<RoleDto>` | Token |
| `/api/v1/roles/{roleId}` | GET | 获取角色详情 | `roleId` (路径) | - | `RoleDto` | Token |
| `/api/v1/roles` | POST | 创建角色 | - | `CreateRoleCommand` | `RoleDto` | Token |
| `/api/v1/roles/{roleId}` | PUT | 更新角色 | `roleId` (路径) | `UpdateRoleCommand` | `RoleDto` | Token |
| `/api/v1/roles/{roleId}` | DELETE | 删除角色 | `roleId` (路径) | - | `Void` | Token |
| `/api/v1/roles/{roleId}/users` | GET | 获取拥有该角色的用户列表 | `roleId` (路径) | - | `List<UserDto>` | Token |

**说明**：
- 角色的`tenantId`从token自动获取
- 角色分配操作自动限制在当前租户内
- **RESTful规范**：
  - 查询接口：`GET /api/v1/roles/{roleId}/users` 表示"拥有该角色的用户"（角色视角的子资源查询）
  - 关联操作：用户-角色的关联操作统一在用户模块管理，角色模块只保留查询接口
    - 添加角色关联：`PUT /api/v1/users/{userId}/roles/{roleId}`
    - 删除角色关联：`DELETE /api/v1/users/{userId}/roles/{roleId}`
    - 批量替换角色：`PUT /api/v1/users/{userId}/roles` 带请求体

#### 管理接口（跨租户）

| 接口路径 | HTTP方法 | 功能描述 | 请求参数 | 请求体 | 返回数据 | 权限要求 |
|---------|---------|---------|---------|--------|---------|---------|
| `/api/v1/admin/roles` | GET | 管理员查询角色列表（可跨租户） | `tenantId` (查询参数), `pageNum`, `pageSize`, `name` | - | `PageResult<RoleDto>` | `admin:role:read` |
| `/api/v1/admin/roles/{roleId}` | GET | 管理员查询角色详情（跨租户） | `roleId` (路径), `tenantId` (查询参数, 可选) | - | `RoleDto` | `admin:role:read` |
| `/api/v1/admin/roles` | POST | 管理员创建角色 | `tenantId` (查询参数或请求体) | `CreateRoleCommand` | `RoleDto` | `admin:role:create` |
| `/api/v1/admin/roles/{roleId}` | PUT | 管理员更新角色 | `roleId` (路径), `tenantId` (查询参数, 可选) | `UpdateRoleCommand` | `RoleDto` | `admin:role:update` |
| `/api/v1/admin/roles/{roleId}` | DELETE | 管理员删除角色 | `roleId` (路径), `tenantId` (查询参数, 可选) | - | `Void` | `admin:role:delete` |

**说明**：
- 管理员接口使用 `/api/v1/admin/roles`，tenantId通过查询参数传递
- 权限要求通过权限模块动态检查，权限规则存储在数据库中，无需在Controller中硬编码

---

### 2.4 组织模块（Organization）

#### 业务接口（租户隔离）

| 接口路径 | HTTP方法 | 功能描述 | 请求参数 | 请求体 | 返回数据 | 租户ID来源 |
|---------|---------|---------|---------|--------|---------|-----------|
| `/api/v1/organizations` | GET | 获取当前租户的组织列表（分页） | `pageNum`, `pageSize`, `name` | - | `PageResult<OrganizationDto>` | Token |
| `/api/v1/organizations/{orgId}` | GET | 获取组织详情 | `orgId` (路径) | - | `OrganizationDto` | Token |
| `/api/v1/organizations` | POST | 创建组织 | - | `CreateOrganizationCommand` | `OrganizationDto` | Token |
| `/api/v1/organizations/{orgId}` | PUT | 更新组织 | `orgId` (路径) | `UpdateOrganizationCommand` | `OrganizationDto` | Token |
| `/api/v1/organizations/{orgId}` | DELETE | 删除组织 | `orgId` (路径) | - | `Void` | Token |
| `/api/v1/organizations/{orgId}/departments` | GET | 获取组织的部门列表 | `orgId` (路径) | - | `List<DepartmentDto>` | Token |
| `/api/v1/organizations/{orgId}/positions` | GET | 获取组织的岗位列表 | `orgId` (路径) | - | `List<PositionDto>` | Token |

#### 管理接口（跨租户）

| 接口路径 | HTTP方法 | 功能描述 | 请求参数 | 请求体 | 返回数据 | 权限要求 |
|---------|---------|---------|---------|--------|---------|---------|
| `/api/v1/admin/organizations` | GET | 管理员查询组织列表（可跨租户） | `tenantId` (查询参数), `pageNum`, `pageSize`, `name` | - | `PageResult<OrganizationDto>` | `admin:organization:read` |
| `/api/v1/admin/organizations/{orgId}` | GET | 管理员查询组织详情（跨租户） | `orgId` (路径), `tenantId` (查询参数, 可选) | - | `OrganizationDto` | `admin:organization:read` |
| `/api/v1/admin/organizations` | POST | 管理员创建组织 | `tenantId` (查询参数或请求体) | `CreateOrganizationCommand` | `OrganizationDto` | `admin:organization:create` |
| `/api/v1/admin/organizations/{orgId}` | PUT | 管理员更新组织 | `orgId` (路径), `tenantId` (查询参数, 可选) | `UpdateOrganizationCommand` | `OrganizationDto` | `admin:organization:update` |
| `/api/v1/admin/organizations/{orgId}` | DELETE | 管理员删除组织 | `orgId` (路径), `tenantId` (查询参数, 可选) | - | `Void` | `admin:organization:delete` |

**说明**：
- 管理员接口使用 `/api/v1/admin/organizations`，tenantId通过查询参数传递

---

### 2.5 部门模块（Department）

#### 业务接口（租户隔离）

| 接口路径 | HTTP方法 | 功能描述 | 请求参数 | 请求体 | 返回数据 | 租户ID来源 |
|---------|---------|---------|---------|--------|---------|-----------|
| `/api/v1/departments` | GET | 获取当前租户的部门列表（分页） | `pageNum`, `pageSize`, `name`, `orgId`, `parentId` | - | `PageResult<DepartmentDto>` | Token |
| `/api/v1/departments/{deptId}` | GET | 获取部门详情 | `deptId` (路径) | - | `DepartmentDto` | Token |
| `/api/v1/departments` | POST | 创建部门 | - | `CreateDepartmentCommand` | `DepartmentDto` | Token |
| `/api/v1/departments/{deptId}` | PUT | 更新部门 | `deptId` (路径) | `UpdateDepartmentCommand` | `DepartmentDto` | Token |
| `/api/v1/departments/{deptId}` | DELETE | 删除部门 | `deptId` (路径) | - | `Void` | Token |
| `/api/v1/departments/{deptId}/children` | GET | 获取部门的子部门列表 | `deptId` (路径) | - | `List<DepartmentDto>` | Token |
| `/api/v1/departments/{deptId}/users` | GET | 获取部门的用户列表 | `deptId` (路径) | - | `List<UserDto>` | Token |

**说明**：
- **接口归属**：`/api/v1/departments/{deptId}/users` 放在**部门模块**，表示"部门的用户"（部门视角）
- **用户模块接口**：`/api/v1/users/{userId}/departments` 放在**用户模块**，表示"用户所属的部门"（用户视角）
- **两个接口同时存在**：满足不同的使用场景
  - 部门管理页面查看部门成员 → 使用 `/api/v1/departments/{deptId}/users`
  - 用户管理页面查看用户所属部门 → 使用 `/api/v1/users/{userId}/departments`
- **关联操作**：用户与部门的关联操作建议在用户模块统一管理：
  - `PUT /api/v1/users/{userId}/departments/{deptId}` - 将用户添加到部门
  - `DELETE /api/v1/users/{userId}/departments/{deptId}` - 从部门移除用户

#### 管理接口（跨租户）

| 接口路径 | HTTP方法 | 功能描述 | 请求参数 | 请求体 | 返回数据 | 权限要求 |
|---------|---------|---------|---------|--------|---------|---------|
| `/api/v1/admin/departments` | GET | 管理员查询部门列表（可跨租户） | `tenantId` (查询参数), `pageNum`, `pageSize`, `name`, `orgId` | - | `PageResult<DepartmentDto>` | `admin:department:read` |
| `/api/v1/admin/departments/{deptId}` | GET | 管理员查询部门详情（跨租户） | `deptId` (路径), `tenantId` (查询参数, 可选) | - | `DepartmentDto` | `admin:department:read` |
| `/api/v1/admin/departments` | POST | 管理员创建部门 | `tenantId` (查询参数或请求体) | `CreateDepartmentCommand` | `DepartmentDto` | `admin:department:create` |
| `/api/v1/admin/departments/{deptId}` | PUT | 管理员更新部门 | `deptId` (路径), `tenantId` (查询参数, 可选) | `UpdateDepartmentCommand` | `DepartmentDto` | `admin:department:update` |
| `/api/v1/admin/departments/{deptId}` | DELETE | 管理员删除部门 | `deptId` (路径), `tenantId` (查询参数, 可选) | - | `Void` | `admin:department:delete` |

**说明**：
- 管理员接口使用 `/api/v1/admin/departments`，tenantId通过查询参数传递

---

### 2.6 岗位模块（Position）

#### 业务接口（租户隔离）

| 接口路径 | HTTP方法 | 功能描述 | 请求参数 | 请求体 | 返回数据 | 租户ID来源 |
|---------|---------|---------|---------|--------|---------|-----------|
| `/api/v1/positions` | GET | 获取当前租户的岗位列表（分页） | `pageNum`, `pageSize`, `name`, `orgId` | - | `PageResult<PositionDto>` | Token |
| `/api/v1/positions/{posId}` | GET | 获取岗位详情 | `posId` (路径) | - | `PositionDto` | Token |
| `/api/v1/positions` | POST | 创建岗位 | - | `CreatePositionCommand` | `PositionDto` | Token |
| `/api/v1/positions/{posId}` | PUT | 更新岗位 | `posId` (路径) | `UpdatePositionCommand` | `PositionDto` | Token |
| `/api/v1/positions/{posId}` | DELETE | 删除岗位 | `posId` (路径) | - | `Void` | Token |
| `/api/v1/positions/{posId}/users` | GET | 获取岗位的用户列表 | `posId` (路径) | - | `List<UserDto>` | Token |

**说明**：
- **接口归属**：`/api/v1/positions/{posId}/users` 放在**岗位模块**，表示"岗位的用户"（岗位视角）
- **用户模块接口**：`/api/v1/users/{userId}/positions` 放在**用户模块**，表示"用户所属的岗位"（用户视角）
- **关联操作**：用户与岗位的关联操作在用户模块统一管理（见用户模块接口）

#### 管理接口（跨租户）

| 接口路径 | HTTP方法 | 功能描述 | 请求参数 | 请求体 | 返回数据 | 权限要求 |
|---------|---------|---------|---------|--------|---------|---------|
| `/api/v1/admin/positions` | GET | 管理员查询岗位列表（可跨租户） | `tenantId` (查询参数), `pageNum`, `pageSize`, `name`, `orgId` | - | `PageResult<PositionDto>` | `admin:position:read` |
| `/api/v1/admin/positions/{posId}` | GET | 管理员查询岗位详情（跨租户） | `posId` (路径), `tenantId` (查询参数, 可选) | - | `PositionDto` | `admin:position:read` |
| `/api/v1/admin/positions` | POST | 管理员创建岗位 | `tenantId` (查询参数或请求体) | `CreatePositionCommand` | `PositionDto` | `admin:position:create` |
| `/api/v1/admin/positions/{posId}` | PUT | 管理员更新岗位 | `posId` (路径), `tenantId` (查询参数, 可选) | `UpdatePositionCommand` | `PositionDto` | `admin:position:update` |
| `/api/v1/admin/positions/{posId}` | DELETE | 管理员删除岗位 | `posId` (路径), `tenantId` (查询参数, 可选) | - | `Void` | `admin:position:delete` |

**说明**：
- 管理员接口使用 `/api/v1/admin/positions`，tenantId通过查询参数传递

---

### 2.7 群组模块（Group）

#### 业务接口（租户隔离）

| 接口路径 | HTTP方法 | 功能描述 | 请求参数 | 请求体 | 返回数据 | 租户ID来源 |
|---------|---------|---------|---------|--------|---------|-----------|
| `/api/v1/groups` | GET | 获取当前租户的群组列表（分页） | `pageNum`, `pageSize`, `name` | - | `PageResult<GroupDto>` | Token |
| `/api/v1/groups/{groupId}` | GET | 获取群组详情 | `groupId` (路径) | - | `GroupDto` | Token |
| `/api/v1/groups` | POST | 创建群组 | - | `CreateGroupCommand` | `GroupDto` | Token |
| `/api/v1/groups/{groupId}` | PUT | 更新群组 | `groupId` (路径) | `UpdateGroupCommand` | `GroupDto` | Token |
| `/api/v1/groups/{groupId}` | DELETE | 删除群组 | `groupId` (路径) | - | `Void` | Token |
| `/api/v1/groups/{groupId}/members` | GET | 获取群组成员列表 | `groupId` (路径) | - | `List<UserDto>` | Token |
| `/api/v1/groups/{groupId}/members` | PUT | 更新群组成员集合（批量替换） | `groupId` (路径) | `{userIds: ["uuid1", "uuid2"]}` | `Void` | Token |
| `/api/v1/groups/{groupId}/members/{userId}` | PUT | 添加成员到群组 | `groupId`, `userId` (路径) | - | `Void` | Token |
| `/api/v1/groups/{groupId}/members/{userId}` | DELETE | 从群组移除成员 | `groupId`, `userId` (路径) | - | `Void` | Token |
| `/api/v1/groups/{groupId}/roles` | GET | 获取群组的角色列表 | `groupId` (路径) | - | `List<RoleDto>` | Token |
| `/api/v1/groups/{groupId}/roles` | PUT | 更新群组的角色集合（批量替换） | `groupId` (路径) | `{roleIds: ["uuid1", "uuid2"]}` | `Void` | Token |
| `/api/v1/groups/{groupId}/roles/{roleId}` | PUT | 分配角色给群组 | `groupId`, `roleId` (路径) | - | `Void` | Token |
| `/api/v1/groups/{groupId}/roles/{roleId}` | DELETE | 移除群组的角色 | `groupId`, `roleId` (路径) | - | `Void` | Token |

**说明**：
- **RESTful规范**：
  - 查询接口：`GET /api/v1/groups/{groupId}/members`、`GET /api/v1/groups/{groupId}/roles` 表示子资源查询
  - 单个关联操作：`PUT /api/v1/groups/{groupId}/members/{userId}` 添加、`DELETE /api/v1/groups/{groupId}/members/{userId}` 删除
  - 批量替换操作：`PUT /api/v1/groups/{groupId}/members` 带请求体替换整个集合

#### 管理接口（跨租户）

| 接口路径 | HTTP方法 | 功能描述 | 请求参数 | 请求体 | 返回数据 | 权限要求 |
|---------|---------|---------|---------|--------|---------|---------|
| `/api/v1/admin/groups` | GET | 管理员查询群组列表（可跨租户） | `tenantId` (查询参数), `pageNum`, `pageSize`, `name` | - | `PageResult<GroupDto>` | `admin:group:read` |
| `/api/v1/admin/groups/{groupId}` | GET | 管理员查询群组详情（跨租户） | `groupId` (路径), `tenantId` (查询参数, 可选) | - | `GroupDto` | `admin:group:read` |
| `/api/v1/admin/groups` | POST | 管理员创建群组 | `tenantId` (查询参数或请求体) | `CreateGroupCommand` | `GroupDto` | `admin:group:create` |
| `/api/v1/admin/groups/{groupId}` | PUT | 管理员更新群组 | `groupId` (路径), `tenantId` (查询参数, 可选) | `UpdateGroupCommand` | `GroupDto` | `admin:group:update` |
| `/api/v1/admin/groups/{groupId}` | DELETE | 管理员删除群组 | `groupId` (路径), `tenantId` (查询参数, 可选) | - | `Void` | `admin:group:delete` |

**说明**：
- 管理员接口使用 `/api/v1/admin/groups`，tenantId通过查询参数传递

---

### 2.8 租户模块（Tenant）

#### 业务接口（租户自身信息）

| 接口路径 | HTTP方法 | 功能描述 | 请求参数 | 请求体 | 返回数据 | 租户ID来源 |
|---------|---------|---------|---------|--------|---------|-----------|
| `/api/v1/tenant/current` | GET | 获取当前租户信息 | - | - | `TenantDto` | Token |

**说明**：
- 租户信息通常由管理员管理，普通用户只能查看自己租户的信息

#### 管理接口（跨租户，仅管理员）

| 接口路径 | HTTP方法 | 功能描述 | 请求参数 | 请求体 | 返回数据 | 权限要求 |
|---------|---------|---------|---------|--------|---------|---------|
| `/api/v1/admin/tenants` | GET | 获取所有租户列表（分页） | `pageNum`, `pageSize`, `name`, `status` | - | `PageResult<TenantDto>` | `admin:tenant:read` |
| `/api/v1/admin/tenants/{tenantId}` | GET | 获取租户详情 | `tenantId` (路径) | - | `TenantDto` | `admin:tenant:read` |
| `/api/v1/admin/tenants` | POST | 创建租户 | - | `CreateTenantCommand` | `TenantDto` | `admin:tenant:create` |
| `/api/v1/admin/tenants/{tenantId}` | PUT | 更新租户 | `tenantId` (路径) | `UpdateTenantCommand` | `TenantDto` | `admin:tenant:update` |
| `/api/v1/admin/tenants/{tenantId}` | DELETE | 删除租户 | `tenantId` (路径) | - | `Void` | `admin:tenant:delete` |
| `/api/v1/admin/tenants/{tenantId}/status` | PUT | 更新租户状态 | `tenantId` (路径) | `{status: "ACTIVE"}` | `TenantDto` | `admin:tenant:update` |

**说明**：权限要求通过配置文件或权限拦截器自动检查，无需在Controller中硬编码

---

### 2.9 租户组模块（TenantGroup）

#### 管理接口（仅管理员）

| 接口路径 | HTTP方法 | 功能描述 | 请求参数 | 请求体 | 返回数据 | 权限要求 |
|---------|---------|---------|---------|--------|---------|---------|
| `/api/v1/admin/tenant-groups` | GET | 获取租户组列表（分页） | `pageNum`, `pageSize`, `name` | - | `PageResult<TenantGroupDto>` | `admin:tenant-group:read` |
| `/api/v1/admin/tenant-groups/{groupId}` | GET | 获取租户组详情 | `groupId` (路径) | - | `TenantGroupDto` | `admin:tenant-group:read` |
| `/api/v1/admin/tenant-groups` | POST | 创建租户组 | - | `CreateTenantGroupCommand` | `TenantGroupDto` | `admin:tenant-group:create` |
| `/api/v1/admin/tenant-groups/{groupId}` | PUT | 更新租户组 | `groupId` (路径) | `UpdateTenantGroupCommand` | `TenantGroupDto` | `admin:tenant-group:update` |
| `/api/v1/admin/tenant-groups/{groupId}` | DELETE | 删除租户组 | `groupId` (路径) | - | `Void` | `admin:tenant-group:delete` |
| `/api/v1/admin/tenant-groups/{groupId}/tenants` | GET | 获取租户组的租户列表 | `groupId` (路径) | - | `List<TenantDto>` | `admin:tenant-group:read` |

**说明**：权限要求通过配置文件或权限拦截器自动检查，无需在Controller中硬编码

---

## 三、接口汇总统计

| 模块 | 业务接口数 | 管理接口数 | 总计 |
|------|-----------|-----------|------|
| 菜单 | 6 | 5 | 11 |
| 用户 | 17 | 5 | 22 |
| 角色 | 6 | 5 | 11 |
| 组织 | 7 | 5 | 12 |
| 部门 | 7 | 5 | 12 |
| 岗位 | 6 | 5 | 11 |
| 群组 | 12 | 5 | 17 |
| 租户 | 1 | 6 | 7 |
| 租户组 | 0 | 6 | 6 |
| 权限 | 0 | 5 | 5 |
| **总计** | **63** | **52** | **115** |

**说明**：
- **菜单模块**：删除了 `/api/v1/menus/roots`，通过查询参数 `?parentId=null` 实现；管理接口使用 `/api/v1/admin/menus`，tenantId通过查询参数传递
- **用户模块**：关联操作遵循RESTful规范，使用PUT/DELETE进行单个关联操作，PUT集合进行批量替换；管理接口使用 `/api/v1/admin/users`，tenantId通过查询参数传递
- **角色模块**：删除关联操作接口，只保留查询接口 `GET /api/v1/roles/{roleId}/users`，关联操作在用户模块统一管理；管理接口使用 `/api/v1/admin/roles`，tenantId通过查询参数传递
- **组织模块**：管理接口使用 `/api/v1/admin/organizations`，tenantId通过查询参数传递
- **部门模块**：只保留查询接口，关联操作在用户模块统一管理；管理接口使用 `/api/v1/admin/departments`，tenantId通过查询参数传递
- **岗位模块**：只保留查询接口，关联操作在用户模块统一管理；管理接口使用 `/api/v1/admin/positions`，tenantId通过查询参数传递
- **群组模块**：关联操作遵循RESTful规范，使用PUT/DELETE进行单个关联操作，PUT集合进行批量替换；管理接口使用 `/api/v1/admin/groups`，tenantId通过查询参数传递
- **权限模块**：Directory服务包含权限模块，权限规则存储在数据库中，支持动态配置和管理；管理接口使用 `/api/v1/permissions`，通过权限模块API进行权限规则管理

---

## 四、注意事项

1. **SessionInterceptor配置**：确保`/api/v1/**`路径不被排除，以便从token获取tenantId
2. **数据隔离**：所有业务接口必须自动过滤tenantId，防止数据泄露
3. **权限验证**：管理接口通过权限模块进行动态权限验证，权限规则存储在数据库中
4. **权限管理**：权限规则通过权限模块的API进行动态配置和管理，支持实时更新
5. **用户ID验证**：用户相关接口需要验证userId是否匹配或是否有权限
6. **批量操作**：批量操作时，需要验证所有资源都属于当前租户

### 4.1 权限模块设计

Directory服务包含权限模块，用于动态管理权限规则和权限验证。

#### 权限模块核心功能

1. **权限规则管理**：权限规则存储在数据库中，支持动态配置和管理
2. **路径匹配**：使用Ant路径匹配模式，支持通配符（`**`, `*`）
3. **权限标识**：权限标识格式统一为 `{resource}:{action}` 或 `admin:{resource}:{action}`
4. **权限缓存**：权限检查结果可以缓存，提高性能
5. **权限日志**：记录权限检查日志，便于审计和问题排查

#### 权限模块接口

权限模块提供以下接口用于权限规则的管理：

| 接口路径 | HTTP方法 | 功能描述 | 请求参数 | 请求体 | 返回数据 | 权限要求 |
|---------|---------|---------|---------|--------|---------|---------|
| `/api/v1/permissions` | GET | 获取权限规则列表（分页） | `pageNum`, `pageSize`, `pattern`, `method` | - | `PageResult<PermissionRuleDto>` | `admin:permission:read` |
| `/api/v1/permissions/{permissionId}` | GET | 获取权限规则详情 | `permissionId` (路径) | - | `PermissionRuleDto` | `admin:permission:read` |
| `/api/v1/permissions` | POST | 创建权限规则 | - | `CreatePermissionRuleCommand` | `PermissionRuleDto` | `admin:permission:create` |
| `/api/v1/permissions/{permissionId}` | PUT | 更新权限规则 | `permissionId` (路径) | `UpdatePermissionRuleCommand` | `PermissionRuleDto` | `admin:permission:update` |
| `/api/v1/permissions/{permissionId}` | DELETE | 删除权限规则 | `permissionId` (路径) | - | `Void` | `admin:permission:delete` |
| `/api/v1/permissions/check` | POST | 检查用户权限 | - | `{userId, path, method}` | `{hasPermission: boolean}` | 内部接口 |

**说明**：
- 权限规则包含：路径模式（pattern）、HTTP方法（methods）、权限标识（permission）、描述（description）等
- 权限拦截器从权限模块查询权限规则，根据路径和方法匹配进行权限验证
- 权限规则支持动态更新，无需重启服务

