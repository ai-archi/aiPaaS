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
                    <el-form-item prop="name" :label="t('position.Position name')">
                        <el-input
                            v-model="baTable.form.items!.name"
                            type="string"
                            :placeholder="t('Please input field', { field: t('position.Position name') })"
                        ></el-input>
                    </el-form-item>
                    <el-form-item prop="description" :label="t('position.Description')">
                        <el-input
                            v-model="baTable.form.items!.description"
                            type="textarea"
                            :rows="3"
                            :placeholder="t('Please input field', { field: t('position.Description') })"
                        ></el-input>
                    </el-form-item>
                    <el-form-item prop="orgId" :label="t('position.Organization')">
                        <el-select
                            v-model="baTable.form.items!.orgId"
                            :placeholder="t('Please select field', { field: t('position.Organization') })"
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
                    <el-form-item prop="parentId" :label="t('position.Parent position')">
                        <el-select
                            v-model="baTable.form.items!.parentId"
                            :placeholder="t('Please select field', { field: t('position.Parent position') })"
                            filterable
                            clearable
                        >
                            <el-option
                                v-for="pos in positionList"
                                :key="pos.id"
                                :label="pos.name"
                                :value="pos.id"
                                :disabled="pos.id === baTable.form.items!.id"
                            />
                        </el-select>
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
import { useConfig } from '/@/stores/config'
import { getOrganizationList } from '/@/api/backend/organization/organization'
import { getPositionList } from '/@/api/backend/position/position'
import { inject } from 'vue'

defineOptions({
    name: 'position/position/popupForm',
})

const { t } = useI18n()
const config = useConfig()
const baTable = inject('baTable') as any

const formRef = ref<FormInstance>()
const organizationList = ref<any[]>([])
const positionList = ref<any[]>([])

const rules = reactive({
    name: [
        {
            required: true,
            message: t('Please input field', { field: t('position.Position name') }),
            trigger: 'blur',
        },
    ],
    orgId: [
        {
            required: true,
            message: t('Please select field', { field: t('position.Organization') }),
            trigger: 'change',
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

// 加载岗位列表（用于选择父岗位）
const loadPositions = async () => {
    try {
        const res = await getPositionList({ pageNum: 1, pageSize: 1000 })
        if (res.data?.list) {
            positionList.value = res.data.list
        }
    } catch (error) {
        console.error('加载岗位列表失败:', error)
    }
}

onMounted(() => {
    loadOrganizations()
    loadPositions()
})
</script>

<style scoped lang="scss"></style>

