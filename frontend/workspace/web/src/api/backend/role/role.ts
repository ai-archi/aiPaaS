import createAxios from '/@/utils/axios'

/**
 * 角色管理 API 接口
 * 对接 aixone-app-directory 服务的角色接口
 * 
 * 注意：所有接口的 tenantId 都从 token 自动获取，不需要显式传递
 */

// 角色列表查询参数
export interface RoleListParams {
    pageNum?: number
    pageSize?: number
    page?: number  // 兼容 baTable 的 page 参数
    limit?: number // 兼容 baTable 的 limit 参数
    name?: string
}

// 创建角色请求
export interface CreateRoleRequest {
    name: string
    description?: string
}

// 更新角色请求
export interface UpdateRoleRequest {
    name?: string
    description?: string
}

/**
 * 获取角色列表（分页，支持过滤）
 * 租户ID从token自动获取
 */
export function getRoleList(params?: RoleListParams) {
    return createAxios({
        url: '/api/v1/roles',
        method: 'get',
        params,
    })
}

/**
 * 获取角色详情
 * 租户ID从token自动获取
 */
export function getRoleById(roleId: string) {
    return createAxios({
        url: `/api/v1/roles/${roleId}`,
        method: 'get',
    })
}

/**
 * 创建角色
 * tenantId 从 token 自动获取并设置
 */
export function createRole(data: CreateRoleRequest) {
    return createAxios({
        url: '/api/v1/roles',
        method: 'post',
        data,
    })
}

/**
 * 更新角色
 * 租户ID从token自动获取
 */
export function updateRole(roleId: string, data: UpdateRoleRequest) {
    return createAxios({
        url: `/api/v1/roles/${roleId}`,
        method: 'put',
        data,
    })
}

/**
 * 删除角色
 * 租户ID从token自动获取
 */
export function deleteRole(roleId: string) {
    return createAxios({
        url: `/api/v1/roles/${roleId}`,
        method: 'delete',
    })
}

/**
 * 获取拥有该角色的用户列表
 */
export function getRoleUsers(roleId: string) {
    return createAxios({
        url: `/api/v1/roles/${roleId}/users`,
        method: 'get',
    })
}

