# Directory 模块列表与树表分类说明

## 分类原则

根据数据结构的层级关系和业务需求，将 Directory 服务的模块分为两类：

### 树表（Tree Table）🌳
**特征**：数据有父子层级关系，需要树形展示

**模块**：
1. **菜单模块（Menu）** ✅ 已实现
   - 有 `parentId` 字段
   - 支持多级菜单嵌套
   - 使用树表展示

2. **部门模块（Department）** ✅ 已实现
   - 有 `parentId` 字段
   - 支持多级部门嵌套
   - **推荐备注**：按组织层级展开，右侧可显示用户列表
   - 使用树表展示

3. **组织模块（Organization）** ✅ 已实现（已修改为树表）
   - 支持 `parentId` 字段（集团/子公司层次）
   - **推荐备注**：集团/子公司层次
   - 使用树表展示

4. **岗位模块（Position）** ✅ 已实现（已修改为树表）
   - 支持 `parentId` 字段（岗位序列制）
   - **推荐备注**：采用岗位序列制（如：总经理 -> 副总经理 -> 部门经理 -> 普通员工）
   - 使用树表展示

### 列表（Table）📋
**特征**：数据为平铺结构，无层级关系

**模块**：
1. **用户模块（User）** ✅ 已实现
   - 无层级关系
   - **推荐备注**：支持按部门过滤
   - 使用普通列表展示

2. **角色模块（Role）** ✅ 已实现
   - 无层级关系
   - **推荐备注**：支持按权限或系统模块过滤
   - 使用普通列表展示

3. **群组模块（Group）** ✅ 已实现
   - 无层级关系
   - **推荐备注**：支持成员管理
   - 使用普通列表展示

4. **租户模块（Tenant）** ✅ 已实现
   - 无层级关系（或支持多层租户体系）
   - **推荐备注**：多层租户体系才需树表
   - 使用普通列表展示（如果支持多层租户，可改为树表）

## 文件位置

### 树表组件 🌳
- `frontend/workspace/web/src/views/backend/menu/index.vue` - 菜单列表（树表）
- `frontend/workspace/web/src/views/backend/department/department/index.vue` - 部门列表（树表）
- `frontend/workspace/web/src/views/backend/organization/organization/index.vue` - 组织列表（树表）
- `frontend/workspace/web/src/views/backend/position/position/index.vue` - 岗位列表（树表）

### 列表组件 📋
- `frontend/workspace/web/src/views/backend/user/user/index.vue` - 用户列表
- `frontend/workspace/web/src/views/backend/role/role/index.vue` - 角色列表
- `frontend/workspace/web/src/views/backend/group/group/index.vue` - 群组列表
- `frontend/workspace/web/src/views/backend/tenant/tenant/index.vue` - 租户列表

## 技术要点

### 树表实现要点
1. 设置 `expandAll: false` 启用树形表格
2. 实现 `buildTree` 函数将平铺数据转换为树形结构
3. 重写 `baTable.after.getData` 方法，在获取数据后转换
4. 树形结构需要 `children` 字段来存储子节点
5. 表单中需要支持选择父节点（parentId）

### 列表实现要点
1. 不需要设置 `expandAll`
2. 直接使用后端返回的列表数据
3. 支持分页、搜索、排序等标准功能
4. 支持按关联字段过滤（如用户按部门过滤）

## 模块分类总结表

| 模块 | 类型 | 展示方式 | 推荐备注 |
|------|------|---------|---------|
| 部门 | 🌳 树表 | 树形表格 | 按组织层级展开，右侧可显示用户列表 |
| 组织 | 🌳 树表 | 树形表格 | 集团/子公司层次 |
| 用户 | 📋 列表 | 普通列表 | 支持按部门过滤 |
| 角色 | 📋 列表 | 普通列表 | 支持按权限或系统模块过滤 |
| 岗位 | 🌳 树表 | 树形表格 | 采用岗位序列制 |
| 群组 | 📋 列表 | 普通列表 | 支持成员管理 |
| 租户 | 📋 列表（或树表） | 普通列表 | 多层租户体系才需树表 |
| 菜单 | 🌳 树表 | 树形表格 | 多级菜单嵌套 |

## 注意事项

1. **数据转换**：树表需要将后端返回的平铺列表转换为树形结构，转换逻辑需要考虑：
   - 根节点（`parentId` 为 `null` 或不存在）
   - 子节点挂载到父节点的 `children` 数组
   - 处理孤儿节点（父节点不存在的情况）

2. **后端支持**：
   - 如果后端实体已有 `parentId` 字段，前端可以直接使用树表
   - 如果后端实体暂时没有 `parentId` 字段，前端可以先实现树表结构，等后端支持后再启用

3. **性能考虑**：树表在数据量大时可能影响性能，建议：
   - 使用分页加载
   - 懒加载子节点（可选）
   - 限制树形层级深度

4. **用户体验**：树表提供展开/收缩功能，用户可以：
   - 展开所有节点
   - 收缩所有节点
   - 单独展开/收缩某个节点

5. **表单设计**：
   - 树表模块的表单需要支持选择父节点
   - 选择父节点时，不能选择自己（避免循环引用）
   - 父节点选择器应该过滤掉当前编辑的节点
