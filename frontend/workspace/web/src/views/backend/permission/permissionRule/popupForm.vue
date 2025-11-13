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
                    <el-form-item prop="pattern" :label="t('permissionRule.Path pattern')">
                        <el-input
                            v-model="baTable.form.items!.pattern"
                            type="string"
                            :placeholder="t('permissionRule.Pattern placeholder')"
                        ></el-input>
                        <div class="form-item-tip">{{ t('Support Ant path matching, e.g., /api/v1/users/**') }}</div>
                    </el-form-item>
                    <el-form-item prop="methods" :label="t('permissionRule.HTTP methods')">
                        <el-select
                            v-model="baTable.form.items!.methods"
                            multiple
                            :placeholder="t('Please select field', { field: t('permissionRule.HTTP methods') })"
                            style="width: 100%"
                        >
                            <el-option label="GET" value="GET" />
                            <el-option label="POST" value="POST" />
                            <el-option label="PUT" value="PUT" />
                            <el-option label="DELETE" value="DELETE" />
                            <el-option label="PATCH" value="PATCH" />
                            <el-option label="HEAD" value="HEAD" />
                            <el-option label="OPTIONS" value="OPTIONS" />
                        </el-select>
                    </el-form-item>
                    <el-form-item prop="permission" :label="t('permissionRule.Permission identifier')">
                        <el-input
                            v-model="baTable.form.items!.permission"
                            type="string"
                            :placeholder="t('permissionRule.Permission placeholder')"
                        ></el-input>
                        <div class="form-item-tip">{{ t('Format: resource:action or admin:resource:action') }}</div>
                    </el-form-item>
                    <el-form-item prop="priority" :label="t('permissionRule.Priority')">
                        <el-input-number
                            v-model="baTable.form.items!.priority"
                            :min="0"
                            :max="9999"
                            :placeholder="t('Please input field', { field: t('permissionRule.Priority') })"
                            style="width: 100%"
                        ></el-input-number>
                        <div class="form-item-tip">{{ t('Higher number means higher priority') }}</div>
                    </el-form-item>
                    <el-form-item prop="enabled" :label="t('permissionRule.Enabled')">
                        <el-switch
                            v-model="baTable.form.items!.enabled"
                            :active-text="t('Yes')"
                            :inactive-text="t('No')"
                        ></el-switch>
                    </el-form-item>
                    <el-form-item prop="description" :label="t('permissionRule.Description')">
                        <el-input
                            v-model="baTable.form.items!.description"
                            type="textarea"
                            :rows="3"
                            :placeholder="t('Please input field', { field: t('permissionRule.Description') })"
                        ></el-input>
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
import { ref, reactive } from 'vue'
import type { FormInstance } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { useConfig } from '/@/stores/config'
import { inject } from 'vue'

defineOptions({
    name: 'permission/permissionRule/popupForm',
})

const { t } = useI18n()
const config = useConfig()
const baTable = inject('baTable') as any

const formRef = ref<FormInstance>()

const rules = reactive({
    pattern: [
        {
            required: true,
            message: t('Please input field', { field: t('permissionRule.Path pattern') }),
            trigger: 'blur',
        },
    ],
    methods: [
        {
            required: true,
            type: 'array',
            min: 1,
            message: t('Please select at least one HTTP method'),
            trigger: 'change',
        },
    ],
    permission: [
        {
            required: true,
            message: t('Please input field', { field: t('permissionRule.Permission identifier') }),
            trigger: 'blur',
        },
    ],
    priority: [
        {
            required: true,
            type: 'number',
            message: t('Please input field', { field: t('permissionRule.Priority') }),
            trigger: 'blur',
        },
    ],
})
</script>

<style scoped lang="scss">
.form-item-tip {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
}
</style>

