import createAxios from '/@/utils/axios'

/**
 * 租户管理 API 接口
 * 对接 aixone-app-directory 服务的租户接口
 */

// 租户列表查询参数
export interface TenantListParams {
    pageNum?: number
    pageSize?: number
    name?: string
    status?: string
}

// 创建租户请求
export interface CreateTenantRequest {
    name: string
    groupId?: string
}

// 更新租户请求
export interface UpdateTenantRequest {
    name?: string
    groupId?: string
    status?: string
}

/**
 * 获取租户列表
 */
export function getTenantList(params?: TenantListParams) {
    return createAxios({
        url: '/tenants',
        method: 'get',
        params,
    })
}

/**
 * 获取租户详情
 */
export function getTenantById(tenantId: string) {
    return createAxios({
        url: `/tenants/${tenantId}`,
        method: 'get',
    })
}

/**
 * 创建租户
 */
export function createTenant(data: CreateTenantRequest) {
    return createAxios({
        url: '/tenants',
        method: 'post',
        data,
    })
}

/**
 * 更新租户
 */
export function updateTenant(tenantId: string, data: UpdateTenantRequest) {
    return createAxios({
        url: `/tenants/${tenantId}`,
        method: 'put',
        data,
    })
}

/**
 * 删除租户
 */
export function deleteTenant(tenantId: string) {
    return createAxios({
        url: `/tenants/${tenantId}`,
        method: 'delete',
    })
}

/**
 * 批量删除租户
 */
export function batchDeleteTenants(tenantIds: string[]) {
    return createAxios({
        url: '/tenants/batch-delete',
        method: 'post',
        data: tenantIds,
    })
}

