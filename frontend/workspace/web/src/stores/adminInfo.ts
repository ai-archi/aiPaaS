import { defineStore } from 'pinia'
import { ADMIN_INFO } from '/@/stores/constant/cacheKey'
import type { AdminInfo } from '/@/stores/interface'
import { loginWithPassword, refreshToken, logout, validateToken } from '/@/api/backend/auth/auth'
import { mockLoginWithPassword, mockRefreshToken, mockValidateToken, mockLogout } from '/@/api/backend/auth/mock-auth'
import type { LoginRequest, RefreshTokenRequest, TokenResponse } from '/@/api/backend/auth/auth'

export const useAdminInfo = defineStore('adminInfo', {
    state: (): AdminInfo => {
        return {
            id: 0,
            username: '',
            nickname: '',
            avatar: '',
            last_login_time: '',
            token: '',
            refresh_token: '',
            super: false,
        tenantId: 'default', // 添加租户ID
        clientId: 'default-client', // 添加客户端ID
        }
    },
    actions: {
        /**
         * 状态批量填充
         * @param state 新状态数据
         * @param [exclude=true] 是否排除某些字段（忽略填充），默认值 true 排除 token 和 refresh_token，传递 false 则不排除，还可传递 string[] 指定排除字段列表
         */
        dataFill(state: Partial<AdminInfo>, exclude: boolean | string[] = true) {
            if (exclude === true) {
                exclude = ['token', 'refresh_token']
            } else if (exclude === false) {
                exclude = []
            }

            if (Array.isArray(exclude)) {
                exclude.forEach((item) => {
                    delete state[item as keyof AdminInfo]
                })
            }

            this.$patch(state)
        },
        removeToken() {
            this.token = ''
            this.refresh_token = ''
        },
        setToken(token: string, type: 'auth' | 'refresh') {
            const field = type == 'auth' ? 'token' : 'refresh_token'
            this[field] = token
        },
        getToken(type: 'auth' | 'refresh' = 'auth') {
            return type === 'auth' ? this.token : this.refresh_token
        },
        setSuper(val: boolean) {
            this.super = val
        },
        /**
         * 使用新认证接口登录
         */
        async loginWithNewAuth(username: string, password: string, useMock: boolean = true) {
            try {
                const loginData: LoginRequest = {
                    tenantId: this.tenantId,
                    username,
                    password,
                    clientId: this.clientId,
                    clientSecret: 'default-secret',
                }

                let response
                if (useMock) {
                    // 使用模拟认证服务
                    response = await mockLoginWithPassword(loginData)
                } else {
                    // 使用真实认证服务
                    response = await loginWithPassword(loginData)
                }

                const tokenData: TokenResponse = response.data

                // 更新用户信息和令牌
                this.dataFill(
                    {
                        username,
                        token: tokenData.accessToken,
                        refresh_token: tokenData.refreshToken,
                    },
                    false
                )

                return response
            } catch (error) {
                console.error('登录失败:', error)
                throw error
            }
        },
        /**
         * 刷新令牌
         */
        async refreshAuthToken(useMock: boolean = true) {
            try {
                const refreshData: RefreshTokenRequest = {
                    refreshToken: this.refresh_token,
                    tenantId: this.tenantId,
                    clientId: this.clientId,
                    clientSecret: 'default-secret',
                }

                let response
                if (useMock) {
                    response = await mockRefreshToken(refreshData)
                } else {
                    response = await refreshToken(refreshData)
                }

                const tokenData: TokenResponse = response.data

                // 更新令牌
                this.setToken(tokenData.accessToken, 'auth')
                this.setToken(tokenData.refreshToken, 'refresh')

                return response
            } catch (error) {
                console.error('刷新令牌失败:', error)
                throw error
            }
        },
        /**
         * 验证令牌
         */
        async validateAuthToken(useMock: boolean = true) {
            try {
                const validateData = {
                    token: this.token,
                    tenantId: this.tenantId,
                }

                let response
                if (useMock) {
                    response = await mockValidateToken(validateData)
                } else {
                    response = await validateToken(validateData)
                }

                return response.data
            } catch (error) {
                console.error('验证令牌失败:', error)
                throw error
            }
        },
        /**
         * 使用新认证接口登出
         */
        async logoutWithNewAuth(useMock: boolean = true) {
            try {
                if (useMock) {
                    await mockLogout(this.tenantId)
                } else {
                    await logout(this.tenantId)
                }
                this.removeToken()
                return true
            } catch (error) {
                console.error('登出失败:', error)
                // 即使登出失败，也清除本地令牌
                this.removeToken()
                throw error
            }
        },
    },
    persist: {
        key: ADMIN_INFO,
    },
})
