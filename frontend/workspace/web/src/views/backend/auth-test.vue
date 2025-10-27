<template>
    <div class="auth-test">
        <h2>认证接口测试</h2>

        <div class="test-section">
            <h3>登录测试</h3>
            <el-form :model="loginForm" label-width="100px">
                <el-form-item label="用户名">
                    <el-input v-model="loginForm.username" placeholder="请输入用户名" />
                </el-form-item>
                <el-form-item label="密码">
                    <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" />
                </el-form-item>
                <el-form-item label="租户ID">
                    <el-input v-model="loginForm.tenantId" placeholder="请输入租户ID" />
                </el-form-item>
                <el-form-item label="客户端ID">
                    <el-input v-model="loginForm.clientId" placeholder="请输入客户端ID" />
                </el-form-item>
                <el-form-item>
                    <el-checkbox v-model="useMock" label="使用模拟认证服务" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="testLogin" :loading="loading">测试登录</el-button>
                    <el-button @click="testLogout" :loading="loading">测试登出</el-button>
                    <el-button @click="testValidateToken" :loading="loading">验证令牌</el-button>
                    <el-button @click="testRefreshToken" :loading="loading">刷新令牌</el-button>
                </el-form-item>
            </el-form>
        </div>

        <div class="test-section">
            <h3>测试结果</h3>
            <el-alert
                v-if="testResult"
                :title="testResult.title"
                :type="testResult.type"
                :description="testResult.description"
                show-icon
                :closable="false"
            />
        </div>

        <div class="test-section">
            <h3>当前用户信息</h3>
            <pre>{{ JSON.stringify(adminInfo.$state, null, 2) }}</pre>
        </div>
    </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useAdminInfo } from '/@/stores/adminInfo'
import { ElMessage } from 'element-plus'

const adminInfo = useAdminInfo()
const loading = ref(false)
const testResult = ref<any>(null)
const useMock = ref(true)

const loginForm = reactive({
    username: 'admin',
    password: 'admin123',
    tenantId: 'default',
    clientId: 'web-client',
})

const testLogin = async () => {
    loading.value = true
    testResult.value = null

    try {
        await adminInfo.loginWithNewAuth(loginForm.username, loginForm.password, useMock.value)
        testResult.value = {
            title: '登录成功',
            type: 'success',
            description: `用户登录成功，令牌已保存 (${useMock.value ? '模拟服务' : '真实服务'})`,
        }
        ElMessage.success('登录成功')
    } catch (error: any) {
        testResult.value = {
            title: '登录失败',
            type: 'error',
            description: error.message || '登录过程中发生错误',
        }
        ElMessage.error('登录失败')
    } finally {
        loading.value = false
    }
}

const testLogout = async () => {
    loading.value = true
    testResult.value = null

    try {
        await adminInfo.logoutWithNewAuth(useMock.value)
        testResult.value = {
            title: '登出成功',
            type: 'success',
            description: `用户已成功登出，令牌已清除 (${useMock.value ? '模拟服务' : '真实服务'})`,
        }
        ElMessage.success('登出成功')
    } catch (error: any) {
        testResult.value = {
            title: '登出失败',
            type: 'error',
            description: error.message || '登出过程中发生错误',
        }
        ElMessage.error('登出失败')
    } finally {
        loading.value = false
    }
}

const testValidateToken = async () => {
    loading.value = true
    testResult.value = null

    try {
        const isValid = await adminInfo.validateAuthToken(useMock.value)
        testResult.value = {
            title: '令牌验证成功',
            type: 'success',
            description: `令牌有效: ${isValid} (${useMock.value ? '模拟服务' : '真实服务'})`,
        }
        ElMessage.success('令牌验证成功')
    } catch (error: any) {
        testResult.value = {
            title: '令牌验证失败',
            type: 'error',
            description: error.message || '令牌验证过程中发生错误',
        }
        ElMessage.error('令牌验证失败')
    } finally {
        loading.value = false
    }
}

const testRefreshToken = async () => {
    loading.value = true
    testResult.value = null

    try {
        await adminInfo.refreshAuthToken(useMock.value)
        testResult.value = {
            title: '令牌刷新成功',
            type: 'success',
            description: `令牌已刷新 (${useMock.value ? '模拟服务' : '真实服务'})`,
        }
        ElMessage.success('令牌刷新成功')
    } catch (error: any) {
        testResult.value = {
            title: '令牌刷新失败',
            type: 'error',
            description: error.message || '令牌刷新过程中发生错误',
        }
        ElMessage.error('令牌刷新失败')
    } finally {
        loading.value = false
    }
}
</script>

<style scoped lang="scss">
.auth-test {
    padding: 20px;
    max-width: 800px;
    margin: 0 auto;
}

.test-section {
    margin-bottom: 30px;
    padding: 20px;
    border: 1px solid #e4e7ed;
    border-radius: 4px;
}

pre {
    background-color: #f5f5f5;
    padding: 10px;
    border-radius: 4px;
    overflow-x: auto;
    font-size: 12px;
}
</style>
