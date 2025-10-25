import createAxios from '/@/utils/axios'

/**
 * 模拟认证服务 - 用于测试前端集成
 * 当真实认证服务不可用时使用
 */

// 模拟登录响应
const mockLoginResponse = {
    accessToken: 'mock-access-token-' + Date.now(),
    refreshToken: 'mock-refresh-token-' + Date.now(),
    tokenType: 'Bearer',
    expiresIn: 3600,
    scope: 'read write',
}

// 模拟用户信息
const mockUserInfo = {
    id: 1,
    username: 'admin',
    nickname: '管理员',
    avatar: '/static/images/avatar.png',
    last_login_time: new Date().toISOString(),
    super: true,
}

/**
 * 模拟用户名密码登录
 */
export function mockLoginWithPassword(data: any) {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve({
                data: {
                    ...mockLoginResponse,
                    userInfo: mockUserInfo,
                },
            })
        }, 500) // 模拟网络延迟
    })
}

/**
 * 模拟刷新令牌
 */
export function mockRefreshToken(data: any) {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve({
                data: {
                    accessToken: 'mock-refreshed-access-token-' + Date.now(),
                    refreshToken: 'mock-refreshed-refresh-token-' + Date.now(),
                    tokenType: 'Bearer',
                    expiresIn: 3600,
                },
            })
        }, 300)
    })
}

/**
 * 模拟验证令牌
 */
export function mockValidateToken(data: any) {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve({
                data: true,
            })
        }, 200)
    })
}

/**
 * 模拟登出
 */
export function mockLogout(tenantId: string) {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve({
                data: { success: true },
            })
        }, 200)
    })
}
