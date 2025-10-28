import { useAdminInfo } from '/@/stores/adminInfo'
import createAxios from '/@/utils/axios'

/**
 * Workbench API
 * 调用后端 workbench 服务
 */

/**
 * 获取用户可见菜单
 * 对应后端：GET /workbench/menus
 */
export function getMenus(params: { userId: string; tenantId: string; roles?: string[] }) {
    return createAxios({
        url: '/workbench/menus',
        method: 'get',
        params: params,
    })
}

/**
 * 获取用户菜单个性化配置
 * 对应后端：GET /workbench/menus/custom
 */
export function getUserMenuCustom(params: { userId: string; tenantId: string }) {
    return createAxios({
        url: '/workbench/menus/custom',
        method: 'get',
        params: params,
    })
}

/**
 * 保存用户菜单个性化配置
 * 对应后端：PUT /workbench/menus/custom
 */
export function saveUserMenuCustom(
    config: string,
    params: { userId: string; tenantId: string; menuId: string }
) {
    return createAxios({
        url: '/workbench/menus/custom',
        method: 'put',
        data: config,
        params: params,
    })
}

