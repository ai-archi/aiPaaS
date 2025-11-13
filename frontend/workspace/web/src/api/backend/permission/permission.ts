import createAxios from '/@/utils/axios'

/**
 * 权限管理 API 接口
 * 对接 aixone-app-directory 服务的权限接口
 * 
 * 注意：所有接口的 tenantId 都从 token 自动获取，不需要显式传递
 */

// 权限列表查询参数
export interface PermissionListParams {
    pageNum?: number
    pageSize?: number
    page?: number  // 兼容 baTable 的 page 参数
    limit?: number // 兼容 baTable 的 limit 参数
    resource?: string
    action?: string
    order?: string
}

// 权限类型
export enum PermissionType {
    FUNCTIONAL = 'FUNCTIONAL',  // 功能权限
    DATA = 'DATA'  // 数据权限
}

// 创建权限请求
export interface CreatePermissionRequest {
    name: string
    code: string  // 权限编码（唯一）
    resource: string  // 资源标识
    action: string  // 操作标识（read、write、delete等）
    type: PermissionType  // 权限类型：FUNCTIONAL/DATA
    description?: string
    abacConditions?: Record<string, any>  // ABAC条件（JSON格式）
}

// 更新权限请求
export interface UpdatePermissionRequest {
    name?: string
    code?: string
    resource?: string
    action?: string
    type?: PermissionType
    description?: string
    abacConditions?: Record<string, any>
}

// 权限视图
export interface PermissionView {
    permissionId: string
    tenantId: string
    name: string
    code: string
    resource: string
    action: string
    type: PermissionType
    description?: string
    abacConditions?: Record<string, any>
    createdAt: string
    updatedAt: string
}

/**
 * 获取权限列表（分页，支持过滤）
 * 租户ID从token自动获取
 */
export function getPermissionList(params?: PermissionListParams) {
    return createAxios({
        url: '/api/v1/permissions/data',
        method: 'get',
        params,
    })
}

/**
 * 获取权限详情
 * 租户ID从token自动获取
 */
export function getPermissionById(permissionId: string) {
    return createAxios({
        url: `/api/v1/permissions/data/${permissionId}`,
        method: 'get',
    })
}

/**
 * 创建权限
 * tenantId 从 token 自动获取并设置
 */
export function createPermission(data: CreatePermissionRequest) {
    return createAxios({
        url: '/api/v1/permissions/data',
        method: 'post',
        data,
    })
}

/**
 * 更新权限
 * 租户ID从token自动获取
 */
export function updatePermission(permissionId: string, data: UpdatePermissionRequest) {
    return createAxios({
        url: `/api/v1/permissions/data/${permissionId}`,
        method: 'put',
        data,
    })
}

/**
 * 删除权限
 * 租户ID从token自动获取
 */
export function deletePermission(permissionId: string) {
    return createAxios({
        url: `/api/v1/permissions/data/${permissionId}`,
        method: 'delete',
    })
}

/**
 * 分配权限给角色
 */
export function assignRolePermission(roleId: string, permissionId: string) {
    return createAxios({
        url: `/api/v1/permissions/data/roles/${roleId}/permissions`,
        method: 'post',
        params: { permissionId },
    })
}

/**
 * 移除角色权限
 */
export function removeRolePermission(roleId: string, permissionId: string) {
    return createAxios({
        url: `/api/v1/permissions/data/roles/${roleId}/permissions/${permissionId}`,
        method: 'delete',
    })
}

/**
 * 获取角色的权限列表
 */
export function getRolePermissions(roleId: string) {
    return createAxios({
        url: `/api/v1/permissions/data/roles/${roleId}/permissions`,
        method: 'get',
    })
}

