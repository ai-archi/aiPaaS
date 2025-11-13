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
                    <el-form-item prop="name" :label="t('organization.Organization name')">
                        <el-input
                            v-model="baTable.form.items!.name"
                            type="string"
                            :placeholder="t('Please input field', { field: t('organization.Organization name') })"
                        ></el-input>
                    </el-form-item>
                    <el-form-item prop="description" :label="t('organization.Description')">
                        <el-input
                            v-model="baTable.form.items!.description"
                            type="textarea"
                            :rows="3"
                            :placeholder="t('Please input field', { field: t('organization.Description') })"
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
    name: 'organization/organization/popupForm',
})

const { t } = useI18n()
const config = useConfig()
const baTable = inject('baTable') as any

const formRef = ref<FormInstance>()

const rules = reactive({
    name: [
        {
            required: true,
            message: t('Please input field', { field: t('organization.Organization name') }),
            trigger: 'blur',
        },
    ],
})
</script>

<style scoped lang="scss"></style>

