import createAxios from '/@/utils/axios'

/**
 * 用户管理 API 接口
 * 对接 aixone-app-directory 服务的用户接口
 * 
 * 注意：所有接口的 tenantId 都从 token 自动获取，不需要显式传递
 */

// 用户列表查询参数
export interface UserListParams {
    pageNum?: number
    pageSize?: number
    page?: number  // 兼容 baTable 的 page 参数
    limit?: number // 兼容 baTable 的 limit 参数
    name?: string
    status?: string
    orgId?: string
    deptId?: string
}

// 创建用户请求
export interface CreateUserRequest {
    username: string
    email: string
    password?: string
    orgId?: string
    deptId?: string
    positionIds?: string[]
    roleIds?: string[]
}

// 更新用户请求
export interface UpdateUserRequest {
    username?: string
    email?: string
    orgId?: string
    deptId?: string
    status?: string
}

/**
 * 获取用户列表（分页，支持过滤）
 * 租户ID从token自动获取
 */
export function getUserList(params?: UserListParams) {
    return createAxios({
        url: '/api/v1/users',
        method: 'get',
        params,
    })
}

/**
 * 获取用户详情
 * 租户ID从token自动获取
 */
export function getUserById(userId: string) {
    return createAxios({
        url: `/api/v1/users/${userId}`,
        method: 'get',
    })
}

/**
 * 获取当前登录用户信息
 * 租户ID从token自动获取
 */
export function getCurrentUser() {
    return createAxios({
        url: '/api/v1/users/me',
        method: 'get',
    })
}

/**
 * 创建用户
 * tenantId 从 token 自动获取并设置
 */
export function createUser(data: CreateUserRequest) {
    return createAxios({
        url: '/api/v1/users',
        method: 'post',
        data,
    })
}

/**
 * 更新用户
 * 租户ID从token自动获取
 */
export function updateUser(userId: string, data: UpdateUserRequest) {
    return createAxios({
        url: `/api/v1/users/${userId}`,
        method: 'put',
        data,
    })
}

/**
 * 删除用户
 * 租户ID从token自动获取
 */
export function deleteUser(userId: string) {
    return createAxios({
        url: `/api/v1/users/${userId}`,
        method: 'delete',
    })
}

/**
 * 获取用户所属部门
 */
export function getUserDepartments(userId: string) {
    return createAxios({
        url: `/api/v1/users/${userId}/departments`,
        method: 'get',
    })
}

/**
 * 更新用户的部门集合（批量替换）
 */
export function updateUserDepartments(userId: string, deptIds: string[]) {
    return createAxios({
        url: `/api/v1/users/${userId}/departments`,
        method: 'put',
        data: { deptIds },
    })
}

/**
 * 将用户添加到部门
 */
export function addUserToDepartment(userId: string, deptId: string) {
    return createAxios({
        url: `/api/v1/users/${userId}/departments/${deptId}`,
        method: 'put',
    })
}

/**
 * 从部门移除用户
 */
export function removeUserFromDepartment(userId: string, deptId: string) {
    return createAxios({
        url: `/api/v1/users/${userId}/departments/${deptId}`,
        method: 'delete',
    })
}

/**
 * 获取用户所属岗位
 */
export function getUserPositions(userId: string) {
    return createAxios({
        url: `/api/v1/users/${userId}/positions`,
        method: 'get',
    })
}

/**
 * 更新用户的岗位集合（批量替换）
 */
export function updateUserPositions(userId: string, posIds: string[]) {
    return createAxios({
        url: `/api/v1/users/${userId}/positions`,
        method: 'put',
        data: { posIds },
    })
}

/**
 * 将用户分配到岗位
 */
export function addUserToPosition(userId: string, posId: string) {
    return createAxios({
        url: `/api/v1/users/${userId}/positions/${posId}`,
        method: 'put',
    })
}

/**
 * 从岗位移除用户
 */
export function removeUserFromPosition(userId: string, posId: string) {
    return createAxios({
        url: `/api/v1/users/${userId}/positions/${posId}`,
        method: 'delete',
    })
}

/**
 * 获取用户的角色列表
 */
export function getUserRoles(userId: string) {
    return createAxios({
        url: `/api/v1/users/${userId}/roles`,
        method: 'get',
    })
}

/**
 * 更新用户的角色集合（批量替换）
 */
export function updateUserRoles(userId: string, roleIds: string[]) {
    return createAxios({
        url: `/api/v1/users/${userId}/roles`,
        method: 'put',
        data: { roleIds },
    })
}

/**
 * 为用户添加角色
 */
export function addUserRole(userId: string, roleId: string) {
    return createAxios({
        url: `/api/v1/users/${userId}/roles/${roleId}`,
        method: 'put',
    })
}

/**
 * 移除用户的角色
 */
export function removeUserRole(userId: string, roleId: string) {
    return createAxios({
        url: `/api/v1/users/${userId}/roles/${roleId}`,
        method: 'delete',
    })
}

