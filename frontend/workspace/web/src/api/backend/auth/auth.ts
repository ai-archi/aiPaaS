import createAxios from '/@/utils/axios'

/**
 * 认证相关 API 接口
 * 适配 aixone-tech-auth 服务的认证接口
 */

// 登录请求接口
export interface LoginRequest {
    tenantId: string
    username: string
    password: string
    clientId: string
    clientSecret: string
    verificationCode?: string
}

// 刷新令牌请求接口
export interface RefreshTokenRequest {
    refreshToken: string
    tenantId: string
    clientId: string
    clientSecret: string
}

// 令牌响应接口
export interface TokenResponse {
    accessToken: string
    refreshToken: string
    tokenType: string
    expiresIn: number
    scope?: string
}

// 验证令牌请求接口
export interface ValidateTokenRequest {
    token: string
    tenantId: string
}

/**
 * 用户名密码登录
 */
export function loginWithPassword(data: LoginRequest) {
    return createAxios({
        url: '/api/v1/auth/login',
        method: 'post',
        data,
    })
}

/**
 * 短信验证码登录
 */
export function loginWithSms(data: LoginRequest) {
    return createAxios({
        url: '/api/v1/auth/sms/login',
        method: 'post',
        data,
    })
}

/**
 * 邮箱验证码登录
 */
export function loginWithEmail(data: LoginRequest) {
    return createAxios({
        url: '/api/v1/auth/email/login',
        method: 'post',
        data,
    })
}

/**
 * 刷新令牌
 */
export function refreshToken(data: RefreshTokenRequest) {
    return createAxios({
        url: '/api/v1/auth/refresh',
        method: 'post',
        data,
    })
}

/**
 * 用户登出
 */
export function logout(tenantId: string) {
    return createAxios({
        url: '/api/v1/auth/logout',
        method: 'post',
        params: { tenantId },
    })
}

/**
 * 验证令牌
 */
export function validateToken(data: ValidateTokenRequest) {
    return createAxios({
        url: '/api/v1/auth/validate',
        method: 'post',
        data,
    })
}

/**
 * 发送短信验证码
 */
export function sendSmsCode(phone: string, tenantId: string) {
    return createAxios({
        url: '/api/v1/verification-codes/send',
        method: 'post',
        data: {
            phone,
            tenantId,
            type: 'SMS',
        },
    })
}

/**
 * 发送邮箱验证码
 */
export function sendEmailCode(email: string, tenantId: string) {
    return createAxios({
        url: '/api/v1/verification-codes/send',
        method: 'post',
        data: {
            email,
            tenantId,
            type: 'EMAIL',
        },
    })
}

/**
 * 验证验证码
 */
export function verifyCode(code: string, phone?: string, email?: string, tenantId: string = 'default') {
    return createAxios({
        url: '/api/v1/verification-codes/verify',
        method: 'post',
        data: {
            code,
            phone,
            email,
            tenantId,
        },
    })
}

/**
 * 检查权限
 */
export function checkPermission(userId: string, resource: string, action: string, tenantId: string = 'default') {
    return createAxios({
        url: '/api/v1/auth/check-permission',
        method: 'post',
        data: {
            userId,
            resource,
            action,
            tenantId,
        },
    })
}

/**
 * 获取权限列表
 */
export function getPermissions(tenantId: string = 'default') {
    return createAxios({
        url: `/admin/permissions/${tenantId}`,
        method: 'get',
    })
}

/**
 * 获取角色列表
 */
export function getRoles(tenantId: string = 'default') {
    return createAxios({
        url: '/admin/roles',
        method: 'get',
        params: { tenantId },
    })
}

/**
 * 获取用户角色
 */
export function getUserRoles(userId: string, tenantId: string = 'default') {
    return createAxios({
        url: `/admin/users/${userId}/roles`,
        method: 'get',
        params: { tenantId },
    })
}
