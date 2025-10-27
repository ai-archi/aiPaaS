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
    return createAxios({
        url: url + 'logout',
        method: 'POST',
        data: {
            refreshToken: adminInfo.getToken('refresh'),
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
