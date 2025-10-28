import { useAdminInfo } from '/@/stores/adminInfo'
import { useBaAccount } from '/@/stores/baAccount'
import { useSiteConfig } from '/@/stores/siteConfig'
import createAxios from '/@/utils/axios'

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
        url: '/auth/logout',
        method: 'POST',
        params: {
            tenantId: adminInfo.tenantId || 'default',
        },
    })
}

export function baAccountCheckIn(params: object = {}) {
    return createAxios(
        {
            url: '/auth/login',
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
            url: '/auth/validate',
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
        url: '/auth/logout',
        method: 'POST',
        data: {
            refreshToken: baAccount.getToken('refresh'),
        },
    })
}

/**
 * 获取Workbench菜单
 */
export function getWorkbenchMenus() {
    const adminInfo = useAdminInfo()
    
    // 使用默认的测试UUID，确保能匹配数据库中的测试数据
    // 数据库中的租户ID是 '00000000-0000-0000-0000-000000000000'
    const defaultTenantId = '00000000-0000-0000-0000-000000000000'
    const defaultUserId = '00000000-0000-0000-0000-000000000000'
    
    return createAxios({
        url: '/workbench/menus',
        method: 'get',
        params: {
            userId: adminInfo.id || defaultUserId,
            tenantId: adminInfo.tenantId || defaultTenantId,
        },
    }).then((response) => {
        // 后端现在返回 ApiResponse<List<MenuDTO>>，需要提取 data 字段
        return { data: response.data || [] }
    }).catch((error) => {
        console.warn('获取菜单失败，返回空数组:', error)
        return Promise.resolve({ data: [] })
    })
}
