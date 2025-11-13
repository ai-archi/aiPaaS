import createAxios from '/@/utils/axios'

/**
 * 部门管理 API 接口
 * 对接 aixone-app-directory 服务的部门接口
 * 
 * 注意：所有接口的 tenantId 都从 token 自动获取，不需要显式传递
 */

// 部门列表查询参数
export interface DepartmentListParams {
    pageNum?: number
    pageSize?: number
    page?: number  // 兼容 baTable 的 page 参数
    limit?: number // 兼容 baTable 的 limit 参数
    name?: string
    orgId?: string
    parentId?: string | null  // 查询根部门时传 null
}

// 创建部门请求
export interface CreateDepartmentRequest {
    name: string
    description?: string
    orgId: string
    parentId?: string
}

// 更新部门请求
export interface UpdateDepartmentRequest {
    name?: string
    description?: string
    orgId?: string
    parentId?: string
}

/**
 * 获取部门列表（分页，支持过滤）
 * 租户ID从token自动获取
 */
export function getDepartmentList(params?: DepartmentListParams) {
    return createAxios({
        url: '/api/v1/departments',
        method: 'get',
        params,
    })
}

/**
 * 获取部门详情
 * 租户ID从token自动获取
 */
export function getDepartmentById(deptId: string) {
    return createAxios({
        url: `/api/v1/departments/${deptId}`,
        method: 'get',
    })
}

/**
 * 创建部门
 * tenantId 从 token 自动获取并设置
 */
export function createDepartment(data: CreateDepartmentRequest) {
    return createAxios({
        url: '/api/v1/departments',
        method: 'post',
        data,
    })
}

/**
 * 更新部门
 * 租户ID从token自动获取
 */
export function updateDepartment(deptId: string, data: UpdateDepartmentRequest) {
    return createAxios({
        url: `/api/v1/departments/${deptId}`,
        method: 'put',
        data,
    })
}

/**
 * 删除部门
 * 租户ID从token自动获取
 */
export function deleteDepartment(deptId: string) {
    return createAxios({
        url: `/api/v1/departments/${deptId}`,
        method: 'delete',
    })
}

/**
 * 获取部门的子部门列表
 */
export function getDepartmentChildren(deptId: string) {
    return createAxios({
        url: `/api/v1/departments/${deptId}/children`,
        method: 'get',
    })
}

/**
 * 获取部门的用户列表
 */
export function getDepartmentUsers(deptId: string) {
    return createAxios({
        url: `/api/v1/departments/${deptId}/users`,
        method: 'get',
    })
}

