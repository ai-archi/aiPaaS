import createAxios from '/@/utils/axios'

/**
 * 菜单管理 API 接口
 * 对接 aixone-app-directory 服务的菜单接口
 * 
 * 注意：所有接口的 tenantId 都从 token 自动获取，不需要显式传递
 */

// 菜单列表查询参数
export interface MenuListParams {
    isTree?: boolean  // 是否返回树形结构数据，默认 true
    pageNum?: number
    pageSize?: number
    page?: number  // 兼容 baTable 的 page 参数
    limit?: number // 兼容 baTable 的 limit 参数
    parentId?: string | null  // 查询根菜单时传 null 或 "null"
    name?: string
    title?: string
    type?: string
    order?: string  // 排序参数，格式：field,direction，例如：updatedAt,desc
    quickSearch?: string  // 快速搜索参数
}

// 创建菜单请求（tenantId 将从 token 自动获取，不需要传递）
export interface CreateMenuRequest {
    name: string
    title: string
    path: string
    icon?: string
    type?: string
    renderType?: string
    component?: string
    url?: string
    keepalive?: boolean
    displayOrder?: number
    visible?: boolean
    parentId?: string
}

// 更新菜单请求
export interface UpdateMenuRequest {
    name?: string
    title?: string
    path?: string
    icon?: string
    type?: string
    renderType?: string
    component?: string
    url?: string
    keepalive?: boolean
    displayOrder?: number
    visible?: boolean
    parentId?: string
}

/**
 * 获取菜单列表（分页，支持过滤和排序）
 * 租户ID从token自动获取
 * 
 * @param params 查询参数
 * @returns 分页菜单列表
 */
export function getMenus(params?: MenuListParams) {
    return createAxios({
        url: '/api/v1/menus',
        method: 'get',
        params,
    })
}

/**
 * 获取根菜单列表（parentId=null）
 * 租户ID从token自动获取
 * 
 * @param params 查询参数（可选）
 * @returns 根菜单列表
 */
export function getRootMenus(params?: Omit<MenuListParams, 'parentId'>) {
    return createAxios({
        url: '/api/v1/menus',
        method: 'get',
        params: {
            ...params,
            parentId: null,  // 查询根菜单
        },
    })
}

/**
 * 获取菜单详情
 * 租户ID从token自动获取，自动验证菜单是否属于当前租户
 * 
 * @param menuId 菜单ID
 * @returns 菜单详情
 */
export function getMenuById(menuId: string) {
    return createAxios({
        url: `/api/v1/menus/${menuId}`,
        method: 'get',
    })
}

/**
 * 获取菜单的子菜单
 * 租户ID从token自动获取
 * 
 * @param menuId 菜单ID
 * @returns 子菜单列表
 */
export function getMenuChildren(menuId: string) {
    return createAxios({
        url: `/api/v1/menus/${menuId}/children`,
        method: 'get',
    })
}

/**
 * 创建菜单
 * tenantId 从 token 自动获取并设置，请求体中的 tenantId 会被忽略
 * 
 * @param data 菜单数据
 * @returns 创建的菜单
 */
export function createMenu(data: CreateMenuRequest) {
    return createAxios({
        url: '/api/v1/menus',
        method: 'post',
        data,
    })
}

/**
 * 更新菜单
 * 租户ID从token自动获取，自动验证菜单是否属于当前租户
 * 
 * @param menuId 菜单ID
 * @param data 菜单数据
 * @returns 更新后的菜单
 */
export function updateMenu(menuId: string, data: UpdateMenuRequest) {
    return createAxios({
        url: `/api/v1/menus/${menuId}`,
        method: 'put',
        data,
    })
}

/**
 * 删除菜单
 * 租户ID从token自动获取，自动验证菜单是否属于当前租户
 * 
 * @param menuId 菜单ID
 * @returns 删除结果
 */
export function deleteMenu(menuId: string) {
    return createAxios({
        url: `/api/v1/menus/${menuId}`,
        method: 'delete',
    })
}

/**
 * @deprecated 已废弃，请使用 getMenus() 或 getRootMenus()
 * 获取租户菜单列表（兼容旧接口）
 */
export function getMenusByTenantId(tenantId: string) {
    console.warn('getMenusByTenantId 已废弃，请使用 getMenus() 或 getRootMenus()')
    return getRootMenus()
}

