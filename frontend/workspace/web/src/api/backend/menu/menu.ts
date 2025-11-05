import createAxios from '/@/utils/axios'

/**
 * 菜单管理 API 接口
 * 对接 aixone-app-directory 服务的菜单接口
 */

// 菜单列表查询参数
export interface MenuListParams {
    pageNum?: number
    pageSize?: number
    name?: string
    title?: string
    type?: string
}

// 创建菜单请求
export interface CreateMenuRequest {
    tenantId: string
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
 * 获取租户菜单列表
 */
export function getMenusByTenantId(tenantId: string) {
    return createAxios({
        url: `/menus/tenant/${tenantId}`,
        method: 'get',
    })
}

/**
 * 获取菜单详情
 */
export function getMenuById(menuId: string) {
    return createAxios({
        url: `/menus/${menuId}`,
        method: 'get',
    })
}

/**
 * 创建菜单
 */
export function createMenu(data: CreateMenuRequest) {
    return createAxios({
        url: '/menus',
        method: 'post',
        data,
    })
}

/**
 * 更新菜单
 */
export function updateMenu(menuId: string, data: UpdateMenuRequest) {
    return createAxios({
        url: `/menus/${menuId}`,
        method: 'put',
        data,
    })
}

/**
 * 删除菜单
 */
export function deleteMenu(menuId: string) {
    return createAxios({
        url: `/menus/${menuId}`,
        method: 'delete',
    })
}

