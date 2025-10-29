<template>
    <!-- 对话框表单 -->
    <el-dialog
        class="ba-operate-dialog"
        top="10vh"
        :close-on-click-modal="false"
        :model-value="['Add', 'Edit'].includes(baTable.form.operate!)"
        @close="baTable.toggleForm"
        :destroy-on-close="true"
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
                    @submit.prevent=""
                    @keyup.enter="baTable.onSubmit(formRef)"
                    :model="baTable.form.items"
                    :label-position="config.layout.shrink ? 'top' : 'right'"
                    :label-width="baTable.form.labelWidth + 'px'"
                    :rules="rules"
                >
                    <el-form-item prop="name" :label="t('tenant.group.Group Name')">
                        <el-input
                            v-model="baTable.form.items!.name"
                            type="string"
                            :placeholder="t('Please input field', { field: t('tenant.group.Group Name') })"
                        ></el-input>
                    </el-form-item>
                    <el-form-item :label="t('tenant.group.Description')">
                        <el-input
                            v-model="baTable.form.items!.description"
                            type="textarea"
                            :placeholder="t('Please input field', { field: t('tenant.group.Description') })"
                            :rows="3"
                        ></el-input>
                    </el-form-item>
                    <FormItem
                        type="remoteSelect"
                        :label="t('tenant.group.Parent group')"
                        v-model="baTable.form.items!.parentId"
                        :placeholder="t('tenant.group.Parent group')"
                        :input-attr="{
                            params: { status: 'ACTIVE' },
                            field: 'name',
                            remoteUrl: '/admin/directory.TenantGroup/index',
                        }"
                        :clearable="true"
                    />
                    <el-form-item :label="t('tenant.group.Sort order')">
                        <el-input-number
                            v-model="baTable.form.items!.sortOrder"
                            :min="0"
                            :max="9999"
                            :placeholder="t('Please input field', { field: t('tenant.group.Sort order') })"
                        />
                    </el-form-item>
                    <FormItem
                        :label="t('tenant.group.Status')"
                        v-model="baTable.form.items!.status"
                        type="radio"
                        :input-attr="{
                            border: true,
                            content: { ACTIVE: t('tenant.group.Active'), SUSPENDED: t('tenant.group.Suspended') },
                        }"
                    />
                </el-form>
            </div>
        </el-scrollbar>
        <template #footer>
            <div :style="'width: calc(100% - ' + baTable.form.labelWidth! / 1.8 + 'px)'">
                <el-button @click="baTable.toggleForm('')">{{ t('Cancel') }}</el-button>
                <el-button v-blur :loading="baTable.form.submitLoading" @click="baTable.onSubmit(formRef)" type="primary">
                    {{ baTable.form.operateIds && baTable.form.operateIds.length > 1 ? t('Save and edit next item') : t('Save') }}
                </el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script setup lang="ts">
import { reactive, inject, useTemplateRef } from 'vue'
import { useI18n } from 'vue-i18n'
import type baTableClass from '/@/utils/baTable'
import type { FormItemRule } from 'element-plus'
import FormItem from '/@/components/formItem/index.vue'
import { buildValidatorData } from '/@/utils/validate'
import { useConfig } from '/@/stores/config'

const config = useConfig()
const formRef = useTemplateRef('formRef')
const baTable = inject('baTable') as baTableClass

const { t } = useI18n()

const rules: Partial<Record<string, FormItemRule[]>> = reactive({
    name: [buildValidatorData({ name: 'required', title: t('tenant.group.Group Name') })],
})
</script>

<style scoped lang="scss"></style>

