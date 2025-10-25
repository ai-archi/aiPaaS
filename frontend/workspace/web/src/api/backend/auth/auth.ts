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
        url: '/auth/login',
        method: 'post',
        data,
    })
}

/**
 * 短信验证码登录
 */
export function loginWithSms(data: LoginRequest) {
    return createAxios({
        url: '/auth/sms/login',
        method: 'post',
        data,
    })
}

/**
 * 邮箱验证码登录
 */
export function loginWithEmail(data: LoginRequest) {
    return createAxios({
        url: '/auth/email/login',
        method: 'post',
        data,
    })
}

/**
 * 刷新令牌
 */
export function refreshToken(data: RefreshTokenRequest) {
    return createAxios({
        url: '/auth/refresh',
        method: 'post',
        data,
    })
}

/**
 * 用户登出
 */
export function logout(tenantId: string) {
    return createAxios({
        url: '/auth/logout',
        method: 'post',
        params: { tenantId },
    })
}

/**
 * 验证令牌
 */
export function validateToken(data: ValidateTokenRequest) {
    return createAxios({
        url: '/auth/validate',
        method: 'post',
        data,
    })
}

/**
 * 发送短信验证码
 */
export function sendSmsCode(phone: string, tenantId: string) {
    return createAxios({
        url: '/auth/sms/send',
        method: 'post',
        data: {
            phone,
            tenantId,
        },
    })
}

/**
 * 发送邮箱验证码
 */
export function sendEmailCode(email: string, tenantId: string) {
    return createAxios({
        url: '/auth/email/send',
        method: 'post',
        data: {
            email,
            tenantId,
        },
    })
}
