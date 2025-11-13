import createAxios from '/@/utils/axios'

/**
 * 群组管理 API 接口
 * 对接 aixone-app-directory 服务的群组接口
 * 
 * 注意：所有接口的 tenantId 都从 token 自动获取，不需要显式传递
 */

// 群组列表查询参数
export interface GroupListParams {
    pageNum?: number
    pageSize?: number
    page?: number  // 兼容 baTable 的 page 参数
    limit?: number // 兼容 baTable 的 limit 参数
    name?: string
}

// 创建群组请求
export interface CreateGroupRequest {
    name: string
    description?: string
}

// 更新群组请求
export interface UpdateGroupRequest {
    name?: string
    description?: string
}

/**
 * 获取群组列表（分页，支持过滤）
 * 租户ID从token自动获取
 */
export function getGroupList(params?: GroupListParams) {
    return createAxios({
        url: '/api/v1/groups',
        method: 'get',
        params,
    })
}

/**
 * 获取群组详情
 * 租户ID从token自动获取
 */
export function getGroupById(groupId: string) {
    return createAxios({
        url: `/api/v1/groups/${groupId}`,
        method: 'get',
    })
}

/**
 * 创建群组
 * tenantId 从 token 自动获取并设置
 */
export function createGroup(data: CreateGroupRequest) {
    return createAxios({
        url: '/api/v1/groups',
        method: 'post',
        data,
    })
}

/**
 * 更新群组
 * 租户ID从token自动获取
 */
export function updateGroup(groupId: string, data: UpdateGroupRequest) {
    return createAxios({
        url: `/api/v1/groups/${groupId}`,
        method: 'put',
        data,
    })
}

/**
 * 删除群组
 * 租户ID从token自动获取
 */
export function deleteGroup(groupId: string) {
    return createAxios({
        url: `/api/v1/groups/${groupId}`,
        method: 'delete',
    })
}

/**
 * 获取群组成员列表
 */
export function getGroupMembers(groupId: string) {
    return createAxios({
        url: `/api/v1/groups/${groupId}/members`,
        method: 'get',
    })
}

/**
 * 更新群组成员集合（批量替换）
 */
export function updateGroupMembers(groupId: string, userIds: string[]) {
    return createAxios({
        url: `/api/v1/groups/${groupId}/members`,
        method: 'put',
        data: { userIds },
    })
}

/**
 * 添加成员到群组
 */
export function addGroupMember(groupId: string, userId: string) {
    return createAxios({
        url: `/api/v1/groups/${groupId}/members/${userId}`,
        method: 'put',
    })
}

/**
 * 从群组移除成员
 */
export function removeGroupMember(groupId: string, userId: string) {
    return createAxios({
        url: `/api/v1/groups/${groupId}/members/${userId}`,
        method: 'delete',
    })
}

/**
 * 获取群组的角色列表
 */
export function getGroupRoles(groupId: string) {
    return createAxios({
        url: `/api/v1/groups/${groupId}/roles`,
        method: 'get',
    })
}

/**
 * 更新群组的角色集合（批量替换）
 */
export function updateGroupRoles(groupId: string, roleIds: string[]) {
    return createAxios({
        url: `/api/v1/groups/${groupId}/roles`,
        method: 'put',
        data: { roleIds },
    })
}

/**
 * 分配角色给群组
 */
export function addGroupRole(groupId: string, roleId: string) {
    return createAxios({
        url: `/api/v1/groups/${groupId}/roles/${roleId}`,
        method: 'put',
    })
}

/**
 * 移除群组的角色
 */
export function removeGroupRole(groupId: string, roleId: string) {
    return createAxios({
        url: `/api/v1/groups/${groupId}/roles/${roleId}`,
        method: 'delete',
    })
}

