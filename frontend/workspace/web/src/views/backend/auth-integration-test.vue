<template>
    <div class="auth-integration-test">
        <el-card class="box-card">
            <template #header>
                <div class="card-header">
                    <span>认证功能集成测试</span>
                </div>
            </template>
            
            <el-tabs v-model="activeTab" type="border-card">
                <!-- 登录测试 -->
                <el-tab-pane label="登录测试" name="login">
                    <el-form :model="loginForm" label-width="120px" style="max-width: 600px">
                        <el-form-item label="用户名">
                            <el-input v-model="loginForm.username" placeholder="请输入用户名" />
                        </el-form-item>
                        <el-form-item label="密码">
                            <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" />
                        </el-form-item>
                        <el-form-item label="租户ID">
                            <el-input v-model="loginForm.tenantId" placeholder="请输入租户ID" />
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" @click="testLogin" :loading="loginLoading">
                                测试登录
                            </el-button>
                            <el-button @click="testLogout" :loading="logoutLoading">
                                测试登出
                            </el-button>
                        </el-form-item>
                    </el-form>
                    
                    <el-divider />
                    
                    <div v-if="loginResult">
                        <h4>登录结果:</h4>
                        <pre>{{ JSON.stringify(loginResult, null, 2) }}</pre>
                    </div>
                </el-tab-pane>

                <!-- 验证码测试 -->
                <el-tab-pane label="验证码测试" name="verification">
                    <el-form :model="verificationForm" label-width="120px" style="max-width: 600px">
                        <el-form-item label="手机号">
                            <el-input v-model="verificationForm.phone" placeholder="请输入手机号" />
                        </el-form-item>
                        <el-form-item label="邮箱">
                            <el-input v-model="verificationForm.email" placeholder="请输入邮箱" />
                        </el-form-item>
                        <el-form-item label="验证码">
                            <el-input v-model="verificationForm.code" placeholder="请输入验证码" />
                        </el-form-item>
                        <el-form-item label="租户ID">
                            <el-input v-model="verificationForm.tenantId" placeholder="请输入租户ID" />
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" @click="sendSmsCodeTest" :loading="smsLoading">
                                发送短信验证码
                            </el-button>
                            <el-button type="success" @click="sendEmailCodeTest" :loading="emailLoading">
                                发送邮箱验证码
                            </el-button>
                            <el-button @click="verifyCodeTest" :loading="verifyLoading">
                                验证验证码
                            </el-button>
                        </el-form-item>
                    </el-form>
                    
                    <el-divider />
                    
                    <div v-if="verificationResult">
                        <h4>验证码结果:</h4>
                        <pre>{{ JSON.stringify(verificationResult, null, 2) }}</pre>
                    </div>
                </el-tab-pane>

                <!-- 权限测试 -->
                <el-tab-pane label="权限测试" name="permission">
                    <el-form :model="permissionForm" label-width="120px" style="max-width: 600px">
                        <el-form-item label="用户ID">
                            <el-input v-model="permissionForm.userId" placeholder="请输入用户ID" />
                        </el-form-item>
                        <el-form-item label="资源">
                            <el-input v-model="permissionForm.resource" placeholder="请输入资源" />
                        </el-form-item>
                        <el-form-item label="操作">
                            <el-input v-model="permissionForm.action" placeholder="请输入操作" />
                        </el-form-item>
                        <el-form-item label="租户ID">
                            <el-input v-model="permissionForm.tenantId" placeholder="请输入租户ID" />
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" @click="checkPermissionTest" :loading="permissionLoading">
                                检查权限
                            </el-button>
                            <el-button @click="getPermissionsList" :loading="permissionsLoading">
                                获取权限列表
                            </el-button>
                            <el-button @click="getRolesList" :loading="rolesLoading">
                                获取角色列表
                            </el-button>
                        </el-form-item>
                    </el-form>
                    
                    <el-divider />
                    
                    <div v-if="permissionResult">
                        <h4>权限测试结果:</h4>
                        <pre>{{ JSON.stringify(permissionResult, null, 2) }}</pre>
                    </div>
                </el-tab-pane>

                <!-- 令牌测试 -->
                <el-tab-pane label="令牌测试" name="token">
                    <el-form label-width="120px" style="max-width: 600px">
                        <el-form-item>
                            <el-button type="primary" @click="refreshTokenTest" :loading="refreshLoading">
                                刷新令牌
                            </el-button>
                            <el-button @click="validateTokenTest" :loading="validateLoading">
                                验证令牌
                            </el-button>
                        </el-form-item>
                    </el-form>
                    
                    <el-divider />
                    
                    <div v-if="tokenResult">
                        <h4>令牌测试结果:</h4>
                        <pre>{{ JSON.stringify(tokenResult, null, 2) }}</pre>
                    </div>
                </el-tab-pane>
            </el-tabs>
        </el-card>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useAdminInfo } from '/@/stores/adminInfo'
import { 
    loginWithPassword, 
    sendSmsCode, 
    sendEmailCode, 
    verifyCode, 
    checkPermission, 
    getPermissions, 
    getRoles,
    refreshToken,
    validateToken,
    logout
} from '/@/api/backend/auth/auth'

const adminInfo = useAdminInfo()

const activeTab = ref('login')

// 登录表单
const loginForm = reactive({
    username: 'admin',
    password: 'admin123',
    tenantId: 'default'
})

const loginLoading = ref(false)
const logoutLoading = ref(false)
const loginResult = ref(null)

// 验证码表单
const verificationForm = reactive({
    phone: '13800138000',
    email: 'test@example.com',
    code: '',
    tenantId: 'default'
})

const smsLoading = ref(false)
const emailLoading = ref(false)
const verifyLoading = ref(false)
const verificationResult = ref(null)

// 权限表单
const permissionForm = reactive({
    userId: 'fb06532a-60e1-4b44-9964-9d5686ee7675',
    resource: 'system:config',
    action: 'write',
    tenantId: 'default'
})

const permissionLoading = ref(false)
const permissionsLoading = ref(false)
const rolesLoading = ref(false)
const permissionResult = ref(null)

// 令牌测试
const refreshLoading = ref(false)
const validateLoading = ref(false)
const tokenResult = ref(null)

// 测试登录
const testLogin = async () => {
    loginLoading.value = true
    try {
        const result = await adminInfo.loginWithNewAuth(
            loginForm.username, 
            loginForm.password, 
            false // 使用真实认证服务
        )
        loginResult.value = result
        ElMessage.success('登录成功')
    } catch (error) {
        console.error('登录失败:', error)
        ElMessage.error('登录失败: ' + (error as any).message)
        loginResult.value = error
    } finally {
        loginLoading.value = false
    }
}

// 测试登出
const testLogout = async () => {
    logoutLoading.value = true
    try {
        await adminInfo.logoutWithNewAuth(false)
        loginResult.value = { success: true, message: '登出成功' }
        ElMessage.success('登出成功')
    } catch (error) {
        console.error('登出失败:', error)
        ElMessage.error('登出失败: ' + (error as any).message)
        loginResult.value = error
    } finally {
        logoutLoading.value = false
    }
}

// 发送短信验证码
const sendSmsCodeTest = async () => {
    smsLoading.value = true
    try {
        const result = await sendSmsCode(verificationForm.phone, verificationForm.tenantId)
        verificationResult.value = result
        ElMessage.success('短信验证码发送成功')
    } catch (error) {
        console.error('发送短信验证码失败:', error)
        ElMessage.error('发送短信验证码失败: ' + (error as any).message)
        verificationResult.value = error
    } finally {
        smsLoading.value = false
    }
}

// 发送邮箱验证码
const sendEmailCodeTest = async () => {
    emailLoading.value = true
    try {
        const result = await sendEmailCode(verificationForm.email, verificationForm.tenantId)
        verificationResult.value = result
        ElMessage.success('邮箱验证码发送成功')
    } catch (error) {
        console.error('发送邮箱验证码失败:', error)
        ElMessage.error('发送邮箱验证码失败: ' + (error as any).message)
        verificationResult.value = error
    } finally {
        emailLoading.value = false
    }
}

// 验证验证码
const verifyCodeTest = async () => {
    verifyLoading.value = true
    try {
        const result = await verifyCode(
            verificationForm.code,
            verificationForm.phone || undefined,
            verificationForm.email || undefined,
            verificationForm.tenantId
        )
        verificationResult.value = result
        ElMessage.success('验证码验证成功')
    } catch (error) {
        console.error('验证码验证失败:', error)
        ElMessage.error('验证码验证失败: ' + (error as any).message)
        verificationResult.value = error
    } finally {
        verifyLoading.value = false
    }
}

// 检查权限
const checkPermissionTest = async () => {
    permissionLoading.value = true
    try {
        const result = await checkPermission(
            permissionForm.userId,
            permissionForm.resource,
            permissionForm.action,
            permissionForm.tenantId
        )
        permissionResult.value = result
        ElMessage.success('权限检查完成')
    } catch (error) {
        console.error('权限检查失败:', error)
        ElMessage.error('权限检查失败: ' + (error as any).message)
        permissionResult.value = error
    } finally {
        permissionLoading.value = false
    }
}

// 获取权限列表
const getPermissionsList = async () => {
    permissionsLoading.value = true
    try {
        const result = await getPermissions(permissionForm.tenantId)
        permissionResult.value = result
        ElMessage.success('获取权限列表成功')
    } catch (error) {
        console.error('获取权限列表失败:', error)
        ElMessage.error('获取权限列表失败: ' + (error as any).message)
        permissionResult.value = error
    } finally {
        permissionsLoading.value = false
    }
}

// 获取角色列表
const getRolesList = async () => {
    rolesLoading.value = true
    try {
        const result = await getRoles(permissionForm.tenantId)
        permissionResult.value = result
        ElMessage.success('获取角色列表成功')
    } catch (error) {
        console.error('获取角色列表失败:', error)
        ElMessage.error('获取角色列表失败: ' + (error as any).message)
        permissionResult.value = error
    } finally {
        rolesLoading.value = false
    }
}

// 刷新令牌
const refreshTokenTest = async () => {
    refreshLoading.value = true
    try {
        const result = await adminInfo.refreshAuthToken(false)
        tokenResult.value = result
        ElMessage.success('令牌刷新成功')
    } catch (error) {
        console.error('令牌刷新失败:', error)
        ElMessage.error('令牌刷新失败: ' + (error as any).message)
        tokenResult.value = error
    } finally {
        refreshLoading.value = false
    }
}

// 验证令牌
const validateTokenTest = async () => {
    validateLoading.value = true
    try {
        const result = await adminInfo.validateAuthToken(false)
        tokenResult.value = result
        ElMessage.success('令牌验证成功')
    } catch (error) {
        console.error('令牌验证失败:', error)
        ElMessage.error('令牌验证失败: ' + (error as any).message)
        tokenResult.value = error
    } finally {
        validateLoading.value = false
    }
}
</script>

<style scoped lang="scss">
.auth-integration-test {
    padding: 20px;
    
    .box-card {
        max-width: 1200px;
        margin: 0 auto;
    }
    
    .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }
    
    pre {
        background-color: #f5f5f5;
        padding: 10px;
        border-radius: 4px;
        overflow-x: auto;
        font-size: 12px;
    }
}
</style>
