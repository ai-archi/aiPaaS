import { useAdminInfo } from '/@/stores/adminInfo'
import { useBaAccount } from '/@/stores/baAccount'
import { useSiteConfig } from '/@/stores/siteConfig'
import createAxios from '/@/utils/axios'
import { getMenus } from '/@/api/backend/menu/menu'

// 导出Directory服务各模块API
export * from '/@/api/backend/user/user'
export * from '/@/api/backend/role/role'
export * from '/@/api/backend/organization/organization'
export * from '/@/api/backend/department/department'
export * from '/@/api/backend/position/position'
export * from '/@/api/backend/group/group'
export * from '/@/api/backend/permission/permission'
export * from '/@/api/backend/permission/permissionRule'

export const url = '/admin/Index/'

export function index() {
    return createAxios({
        url: url + 'index',
        method: 'get',
    })
}

export function login(method: 'get' | 'post', params: object = {}) {
    return createAxios({
        url: url + 'login',
        data: params,
        method: method,
    })
}

export function logout() {
    const adminInfo = useAdminInfo()
    // 使用认证服务的登出接口
    // 后端需要 tenantId 作为请求参数，Authorization 头会自动携带
    return createAxios({
        url: '/api/v1/auth/logout',
        method: 'POST',
        params: {
            tenantId: adminInfo.tenantId || 'default',
        },
    })
}

export function baAccountCheckIn(params: object = {}) {
    return createAxios(
        {
            url: '/api/v1/auth/login',
            data: params,
            method: 'post',
        },
        {
            showSuccessMessage: true,
        }
    )
}

export function baAccountGetUserInfo() {
    const baAccount = useBaAccount()
    return createAxios(
        {
            url: '/api/v1/auth/validate',
            method: 'post',
        },
        {
            anotherToken: baAccount.getToken('auth'),
        }
    )
}

export function baAccountLogout() {
    const baAccount = useBaAccount()
    return createAxios({
        url: '/api/v1/auth/logout',
        method: 'POST',
        data: {
            refreshToken: baAccount.getToken('refresh'),
        },
    })
}

/**
 * 构建菜单树形结构
 * @param menus 平铺的菜单列表
 * @returns 树形结构的菜单列表
 */
function buildMenuTree(menus: any[]): any[] {
    if (!menus || !Array.isArray(menus)) {
        return []
    }
    
    // 创建菜单映射表，key为菜单ID
    const menuMap = new Map<string, any>()
    menus.forEach(menu => {
        menuMap.set(menu.id, { ...menu, children: [] })
    })
    
    // 构建树形结构
    const rootMenus: any[] = []
    menus.forEach(menu => {
        const menuNode = menuMap.get(menu.id)!
        if (!menu.parentId) {
            // 根菜单
            rootMenus.push(menuNode)
        } else {
            // 子菜单，添加到父菜单的children中
            const parent = menuMap.get(menu.parentId)
            if (parent) {
                if (!parent.children) {
                    parent.children = []
                }
                parent.children.push(menuNode)
            } else {
                // 父菜单不存在，作为根菜单处理
                rootMenus.push(menuNode)
            }
        }
    })
    
    // 对每个节点的children按displayOrder排序
    const sortChildren = (nodes: any[]) => {
        nodes.forEach(node => {
            if (node.children && node.children.length > 0) {
                node.children.sort((a: any, b: any) => {
                    const orderA = a.displayOrder ?? 0
                    const orderB = b.displayOrder ?? 0
                    return orderA - orderB
                })
                sortChildren(node.children)
            }
        })
    }
    
    // 对根菜单按displayOrder排序
    rootMenus.sort((a, b) => {
        const orderA = a.displayOrder ?? 0
        const orderB = b.displayOrder ?? 0
        return orderA - orderB
    })
    
    sortChildren(rootMenus)
    
    return rootMenus
}

/**
 * 获取菜单（直接调用 Directory 服务）
 * 前端直接调用 Directory 服务获取菜单，tenantId 从 token 自动获取
 * 
 * @returns 菜单列表（树形结构）
 */
export function getWorkbenchMenus() {
    // 获取所有菜单（请求树形结构数据）
    // tenantId 从 token 自动获取，不需要显式传递
    // isTree=true 表示请求树形结构数据，后端默认返回树形结构
    return getMenus({
        isTree: true,  // 明确请求树形结构
        pageNum: 1,
        pageSize: 1000,  // 使用大分页获取所有菜单（虽然树形结构不分页，但保留参数以兼容）
    }).then((response: any) => {
        // 后端返回树形结构数据：ApiResponse<{ list: MenuView[], remark: string }>
        if (response.data && response.data.list && Array.isArray(response.data.list)) {
            // 后端已经返回树形结构，直接使用，不需要重新构建
            return { data: response.data.list }
        }
        return { data: [] }
    }).catch((error: any) => {
        console.warn('获取菜单失败，返回空数组:', error)
        return Promise.resolve({ data: [] })
    })
}
