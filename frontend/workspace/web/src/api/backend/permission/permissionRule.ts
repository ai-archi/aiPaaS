import createAxios from '/@/utils/axios'

/**
 * 权限规则管理 API 接口
 * 对接 aixone-app-directory 服务的权限规则接口
 * 
 * 注意：所有接口的 tenantId 都从 token 自动获取，不需要显式传递
 */

// 权限规则列表查询参数
export interface PermissionRuleListParams {
    pageNum?: number
    pageSize?: number
    page?: number  // 兼容 baTable 的 page 参数
    limit?: number // 兼容 baTable 的 limit 参数
    pattern?: string
    method?: string
    order?: string
}

// 创建权限规则请求
export interface CreatePermissionRuleRequest {
    pattern: string  // 路径模式，支持Ant路径匹配（**、*）
    methods: string[]  // HTTP方法数组（GET、POST、PUT、DELETE等）
    permission: string  // 权限标识，格式：{resource}:{action} 或 admin:{resource}:{action}
    description?: string
    enabled?: boolean
    priority?: number  // 优先级，数字越大优先级越高
}

// 更新权限规则请求
export interface UpdatePermissionRuleRequest {
    pattern?: string
    methods?: string[]
    permission?: string
    description?: string
    enabled?: boolean
    priority?: number
}

// 权限规则视图
export interface PermissionRuleView {
    id: string
    tenantId: string
    pattern: string
    methods: string[]
    permission: string
    description?: string
    enabled: boolean
    priority: number
    createdAt: string
    updatedAt: string
}

/**
 * 获取权限规则列表（分页，支持过滤）
 * 租户ID从token自动获取
 */
export function getPermissionRuleList(params?: PermissionRuleListParams) {
    return createAxios({
        url: '/api/v1/permissions',
        method: 'get',
        params,
    })
}

/**
 * 获取权限规则详情
 * 租户ID从token自动获取
 */
export function getPermissionRuleById(permissionRuleId: string) {
    return createAxios({
        url: `/api/v1/permissions/${permissionRuleId}`,
        method: 'get',
    })
}

/**
 * 创建权限规则
 * tenantId 从 token 自动获取并设置
 */
export function createPermissionRule(data: CreatePermissionRuleRequest) {
    return createAxios({
        url: '/api/v1/permissions',
        method: 'post',
        data,
    })
}

/**
 * 更新权限规则
 * 租户ID从token自动获取
 */
export function updatePermissionRule(permissionRuleId: string, data: UpdatePermissionRuleRequest) {
    return createAxios({
        url: `/api/v1/permissions/${permissionRuleId}`,
        method: 'put',
        data,
    })
}

/**
 * 删除权限规则
 * 租户ID从token自动获取
 */
export function deletePermissionRule(permissionRuleId: string) {
    return createAxios({
        url: `/api/v1/permissions/${permissionRuleId}`,
        method: 'delete',
    })
}

