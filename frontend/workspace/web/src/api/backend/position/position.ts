import createAxios from '/@/utils/axios'

/**
 * 岗位管理 API 接口
 * 对接 aixone-app-directory 服务的岗位接口
 * 
 * 注意：所有接口的 tenantId 都从 token 自动获取，不需要显式传递
 */

// 岗位列表查询参数
export interface PositionListParams {
    pageNum?: number
    pageSize?: number
    page?: number  // 兼容 baTable 的 page 参数
    limit?: number // 兼容 baTable 的 limit 参数
    name?: string
    orgId?: string
}

// 创建岗位请求
export interface CreatePositionRequest {
    name: string
    description?: string
    orgId: string
}

// 更新岗位请求
export interface UpdatePositionRequest {
    name?: string
    description?: string
    orgId?: string
}

/**
 * 获取岗位列表（分页，支持过滤）
 * 租户ID从token自动获取
 */
export function getPositionList(params?: PositionListParams) {
    return createAxios({
        url: '/api/v1/positions',
        method: 'get',
        params,
    })
}

/**
 * 获取岗位详情
 * 租户ID从token自动获取
 */
export function getPositionById(posId: string) {
    return createAxios({
        url: `/api/v1/positions/${posId}`,
        method: 'get',
    })
}

/**
 * 创建岗位
 * tenantId 从 token 自动获取并设置
 */
export function createPosition(data: CreatePositionRequest) {
    return createAxios({
        url: '/api/v1/positions',
        method: 'post',
        data,
    })
}

/**
 * 更新岗位
 * 租户ID从token自动获取
 */
export function updatePosition(posId: string, data: UpdatePositionRequest) {
    return createAxios({
        url: `/api/v1/positions/${posId}`,
        method: 'put',
        data,
    })
}

/**
 * 删除岗位
 * 租户ID从token自动获取
 */
export function deletePosition(posId: string) {
    return createAxios({
        url: `/api/v1/positions/${posId}`,
        method: 'delete',
    })
}

/**
 * 获取岗位的用户列表
 */
export function getPositionUsers(posId: string) {
    return createAxios({
        url: `/api/v1/positions/${posId}/users`,
        method: 'get',
    })
}

