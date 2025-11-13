import createAxios from '/@/utils/axios'

/**
 * 组织管理 API 接口
 * 对接 aixone-app-directory 服务的组织接口
 * 
 * 注意：所有接口的 tenantId 都从 token 自动获取，不需要显式传递
 */

// 组织列表查询参数
export interface OrganizationListParams {
    pageNum?: number
    pageSize?: number
    page?: number  // 兼容 baTable 的 page 参数
    limit?: number // 兼容 baTable 的 limit 参数
    name?: string
}

// 创建组织请求
export interface CreateOrganizationRequest {
    name: string
    description?: string
}

// 更新组织请求
export interface UpdateOrganizationRequest {
    name?: string
    description?: string
}

/**
 * 获取组织列表（分页，支持过滤）
 * 租户ID从token自动获取
 */
export function getOrganizationList(params?: OrganizationListParams) {
    return createAxios({
        url: '/api/v1/organizations',
        method: 'get',
        params,
    })
}

/**
 * 获取组织详情
 * 租户ID从token自动获取
 */
export function getOrganizationById(orgId: string) {
    return createAxios({
        url: `/api/v1/organizations/${orgId}`,
        method: 'get',
    })
}

/**
 * 创建组织
 * tenantId 从 token 自动获取并设置
 */
export function createOrganization(data: CreateOrganizationRequest) {
    return createAxios({
        url: '/api/v1/organizations',
        method: 'post',
        data,
    })
}

/**
 * 更新组织
 * 租户ID从token自动获取
 */
export function updateOrganization(orgId: string, data: UpdateOrganizationRequest) {
    return createAxios({
        url: `/api/v1/organizations/${orgId}`,
        method: 'put',
        data,
    })
}

/**
 * 删除组织
 * 租户ID从token自动获取
 */
export function deleteOrganization(orgId: string) {
    return createAxios({
        url: `/api/v1/organizations/${orgId}`,
        method: 'delete',
    })
}

/**
 * 获取组织的部门列表
 */
export function getOrganizationDepartments(orgId: string) {
    return createAxios({
        url: `/api/v1/organizations/${orgId}/departments`,
        method: 'get',
    })
}

/**
 * 获取组织的岗位列表
 */
export function getOrganizationPositions(orgId: string) {
    return createAxios({
        url: `/api/v1/organizations/${orgId}/positions`,
        method: 'get',
    })
}

