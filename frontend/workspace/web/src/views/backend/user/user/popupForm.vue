<template>
    <!-- 对话框表单 -->
    <el-dialog
        class="ba-operate-dialog"
        :close-on-click-modal="false"
        :destroy-on-close="true"
        :model-value="['Add', 'Edit'].includes(baTable.form.operate!)"
        @close="baTable.toggleForm"
    >
        <template #header>
            <div class="title" v-drag="['.ba-operate-dialog', '.el-dialog__header']" v-zoom="'.ba-operate-dialog'">
                {{ baTable.form.operate ? t(baTable.form.operate) : '' }}
            </div>
        </template>
        <el-scrollbar v-loading="baTable.form.loading" class="ba-table-form-scrollbar">
            <div
                class="ba-operate-form"
                :class="'ba-' + baTable.form.operate + '-form'"
                :style="config.layout.shrink ? '' : 'width: calc(100% - ' + baTable.form.labelWidth! / 2 + 'px)'"
            >
                <el-form
                    ref="formRef"
                    @keyup.enter="baTable.onSubmit(formRef)"
                    :model="baTable.form.items"
                    :label-position="config.layout.shrink ? 'top' : 'right'"
                    :label-width="baTable.form.labelWidth + 'px'"
                    :rules="rules"
                    v-if="!baTable.form.loading"
                >
                    <el-form-item prop="username" :label="t('user.Username')">
                        <el-input
                            v-model="baTable.form.items!.username"
                            type="string"
                            :placeholder="t('Please input field', { field: t('user.Username') })"
                        ></el-input>
                    </el-form-item>
                    <el-form-item prop="email" :label="t('user.Email')">
                        <el-input
                            v-model="baTable.form.items!.email"
                            type="email"
                            :placeholder="t('Please input field', { field: t('user.Email') })"
                        ></el-input>
                    </el-form-item>
                    <el-form-item v-if="baTable.form.operate === 'Add'" prop="password" :label="t('user.Password')">
                        <el-input
                            v-model="baTable.form.items!.password"
                            type="password"
                            :placeholder="t('Please input field', { field: t('user.Password') })"
                            show-password
                        ></el-input>
                    </el-form-item>
                    <el-form-item prop="orgId" :label="t('user.Organization')">
                        <el-select
                            v-model="baTable.form.items!.orgId"
                            :placeholder="t('Please select field', { field: t('user.Organization') })"
                            filterable
                            clearable
                        >
                            <el-option
                                v-for="org in organizationList"
                                :key="org.id"
                                :label="org.name"
                                :value="org.id"
                            />
                        </el-select>
                    </el-form-item>
                    <el-form-item prop="deptId" :label="t('user.Department')">
                        <el-select
                            v-model="baTable.form.items!.deptId"
                            :placeholder="t('Please select field', { field: t('user.Department') })"
                            filterable
                            clearable
                        >
                            <el-option
                                v-for="dept in departmentList"
                                :key="dept.id"
                                :label="dept.name"
                                :value="dept.id"
                            />
                        </el-select>
                    </el-form-item>
                    <el-form-item prop="status" :label="t('user.Status')">
                        <el-radio-group v-model="baTable.form.items!.status">
                            <el-radio label="ACTIVE">{{ t('user.Active') }}</el-radio>
                            <el-radio label="INACTIVE">{{ t('user.Inactive') }}</el-radio>
                            <el-radio label="SUSPENDED">{{ t('user.Suspended') }}</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-form>
            </div>
        </el-scrollbar>
        <template #footer>
            <div>
                <el-button @click="baTable.toggleForm">{{ t('Cancel') }}</el-button>
                <el-button v-blur :loading="baTable.form.submitLoading" @click="baTable.onSubmit(formRef)" type="primary">
                    {{ t('Save') }}
                </el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import type { FormInstance } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { useAdminInfo } from '/@/stores/adminInfo'
import { useConfig } from '/@/stores/config'
import { getOrganizationList } from '/@/api/backend/organization/organization'
import { getDepartmentList } from '/@/api/backend/department/department'
import { inject } from 'vue'

defineOptions({
    name: 'user/user/popupForm',
})

const { t } = useI18n()
const config = useConfig()
const adminInfo = useAdminInfo()
const baTable = inject('baTable') as any

const formRef = ref<FormInstance>()
const organizationList = ref<any[]>([])
const departmentList = ref<any[]>([])

const rules = reactive({
    username: [
        {
            required: true,
            message: t('Please input field', { field: t('user.Username') }),
            trigger: 'blur',
        },
    ],
    email: [
        {
            required: true,
            type: 'email',
            message: t('Please input field', { field: t('user.Email') }),
            trigger: 'blur',
        },
    ],
    password: [
        {
            required: true,
            message: t('Please input field', { field: t('user.Password') }),
            trigger: 'blur',
        },
    ],
})

// 加载组织列表
const loadOrganizations = async () => {
    try {
        const res = await getOrganizationList({ pageNum: 1, pageSize: 1000 })
        if (res.data?.list) {
            organizationList.value = res.data.list
        }
    } catch (error) {
        console.error('加载组织列表失败:', error)
    }
}

// 加载部门列表
const loadDepartments = async () => {
    try {
        const res = await getDepartmentList({ pageNum: 1, pageSize: 1000 })
        if (res.data?.list) {
            departmentList.value = res.data.list
        }
    } catch (error) {
        console.error('加载部门列表失败:', error)
    }
}

onMounted(() => {
    loadOrganizations()
    loadDepartments()
})
</script>

<style scoped lang="scss"></style>
