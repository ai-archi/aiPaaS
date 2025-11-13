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
                    <el-form-item prop="name" :label="t('permission.Permission name')">
                        <el-input
                            v-model="baTable.form.items!.name"
                            type="string"
                            :placeholder="t('Please input field', { field: t('permission.Permission name') })"
                        ></el-input>
                    </el-form-item>
                    <el-form-item prop="code" :label="t('permission.Permission code')">
                        <el-input
                            v-model="baTable.form.items!.code"
                            type="string"
                            :placeholder="t('Please input field', { field: t('permission.Permission code') })"
                        ></el-input>
                    </el-form-item>
                    <el-form-item prop="resource" :label="t('permission.Resource')">
                        <el-input
                            v-model="baTable.form.items!.resource"
                            type="string"
                            :placeholder="t('Please input field', { field: t('permission.Resource') })"
                        ></el-input>
                    </el-form-item>
                    <el-form-item prop="action" :label="t('permission.Action')">
                        <el-select
                            v-model="baTable.form.items!.action"
                            :placeholder="t('Please select field', { field: t('permission.Action') })"
                            style="width: 100%"
                        >
                            <el-option label="read" value="read" />
                            <el-option label="write" value="write" />
                            <el-option label="delete" value="delete" />
                            <el-option label="execute" value="execute" />
                            <el-option label="manage" value="manage" />
                        </el-select>
                    </el-form-item>
                    <el-form-item prop="type" :label="t('permission.Permission type')">
                        <el-select
                            v-model="baTable.form.items!.type"
                            :placeholder="t('Please select field', { field: t('permission.Permission type') })"
                            style="width: 100%"
                        >
                            <el-option :label="t('permission.Functional permission')" value="FUNCTIONAL" />
                            <el-option :label="t('permission.Data permission')" value="DATA" />
                        </el-select>
                    </el-form-item>
                    <el-form-item prop="description" :label="t('permission.Description')">
                        <el-input
                            v-model="baTable.form.items!.description"
                            type="textarea"
                            :rows="3"
                            :placeholder="t('Please input field', { field: t('permission.Description') })"
                        ></el-input>
                    </el-form-item>
                    <el-form-item prop="abacConditions" :label="t('permission.ABAC conditions')">
                        <el-input
                            v-model="abacConditionsText"
                            type="textarea"
                            :rows="4"
                            :placeholder="t('Please input JSON format', { field: t('permission.ABAC conditions') })"
                            @blur="handleAbacConditionsBlur"
                        ></el-input>
                        <div class="form-item-tip">{{ t('JSON format, optional') }}</div>
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
import { ref, reactive, watch } from 'vue'
import type { FormInstance } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { useConfig } from '/@/stores/config'
import { inject } from 'vue'

defineOptions({
    name: 'permission/permission/popupForm',
})

const { t } = useI18n()
const config = useConfig()
const baTable = inject('baTable') as any

const formRef = ref<FormInstance>()
const abacConditionsText = ref('')

// 监听表单数据变化，同步ABAC条件文本
watch(
    () => baTable.form.items?.abacConditions,
    (newVal) => {
        if (newVal) {
            try {
                abacConditionsText.value = JSON.stringify(newVal, null, 2)
            } catch (e) {
                abacConditionsText.value = ''
            }
        } else {
            abacConditionsText.value = ''
        }
    },
    { immediate: true, deep: true }
)

// 处理ABAC条件文本输入
const handleAbacConditionsBlur = () => {
    if (abacConditionsText.value.trim()) {
        try {
            const parsed = JSON.parse(abacConditionsText.value)
            baTable.form.items!.abacConditions = parsed
        } catch (e) {
            // JSON解析失败，保持为空
            baTable.form.items!.abacConditions = undefined
        }
    } else {
        baTable.form.items!.abacConditions = undefined
    }
}

const rules = reactive({
    name: [
        {
            required: true,
            message: t('Please input field', { field: t('permission.Permission name') }),
            trigger: 'blur',
        },
    ],
    code: [
        {
            required: true,
            message: t('Please input field', { field: t('permission.Permission code') }),
            trigger: 'blur',
        },
    ],
    resource: [
        {
            required: true,
            message: t('Please input field', { field: t('permission.Resource') }),
            trigger: 'blur',
        },
    ],
    action: [
        {
            required: true,
            message: t('Please select field', { field: t('permission.Action') }),
            trigger: 'change',
        },
    ],
    type: [
        {
            required: true,
            message: t('Please select field', { field: t('permission.Permission type') }),
            trigger: 'change',
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

