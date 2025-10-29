import createAxios from '/@/utils/axios'

/**
 * 租户组管理 API 接口
 * 对接 aixone-app-directory 服务的租户组接口
 */

// 租户组列表查询参数
export interface TenantGroupListParams {
    pageNum?: number
    pageSize?: number
    name?: string
    status?: string
}

// 创建租户组请求
export interface CreateTenantGroupRequest {
    name: string
    description?: string
    parentId?: string
    sortOrder?: number
    status?: string
}

// 更新租户组请求
export interface UpdateTenantGroupRequest {
    name?: string
    description?: string
    parentId?: string
    sortOrder?: number
    status?: string
}

/**
 * 获取所有租户组
 */
export function getAllTenantGroups() {
    return createAxios({
        url: '/tenant-groups',
        method: 'get',
    })
}

/**
 * 获取租户组详情
 */
export function getTenantGroupById(groupId: string) {
    return createAxios({
        url: `/tenant-groups/${groupId}`,
        method: 'get',
    })
}

/**
 * 创建租户组
 */
export function createTenantGroup(data: CreateTenantGroupRequest) {
    return createAxios({
        url: '/tenant-groups',
        method: 'post',
        data,
    })
}

/**
 * 更新租户组
 */
export function updateTenantGroup(groupId: string, data: UpdateTenantGroupRequest) {
    return createAxios({
        url: `/tenant-groups/${groupId}`,
        method: 'put',
        data,
    })
}

/**
 * 删除租户组
 */
export function deleteTenantGroup(groupId: string) {
    return createAxios({
        url: `/tenant-groups/${groupId}`,
        method: 'delete',
    })
}

/**
 * 激活租户组
 */
export function activateTenantGroup(groupId: string) {
    return createAxios({
        url: `/api/v1/tenant-groups/${groupId}/activate`,
        method: 'post',
    })
}

/**
 * 停用租户组
 */
export function deactivateTenantGroup(groupId: string) {
    return createAxios({
        url: `/api/v1/tenant-groups/${groupId}/deactivate`,
        method: 'post',
    })
}

/**
 * 获取根租户组
 */
export function getRootTenantGroups() {
    return createAxios({
        url: '/api/v1/tenant-groups/roots',
        method: 'get',
    })
}

/**
 * 根据父ID获取租户组
 */
export function getTenantGroupsByParent(parentId: string) {
    return createAxios({
        url: `/api/v1/tenant-groups/parent/${parentId}`,
        method: 'get',
    })
}

