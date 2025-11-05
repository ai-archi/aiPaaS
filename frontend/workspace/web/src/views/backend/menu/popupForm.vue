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
                    <el-form-item prop="title" :label="t('menu.Title')">
                        <el-input
                            v-model="baTable.form.items!.title"
                            type="string"
                            :placeholder="t('Please input field', { field: t('menu.Title') })"
                        ></el-input>
                    </el-form-item>
                    <el-form-item prop="name" :label="t('menu.Name')">
                        <el-input
                            v-model="baTable.form.items!.name"
                            type="string"
                            :placeholder="t('Please input field', { field: t('menu.Name') })"
                        ></el-input>
                    </el-form-item>
                    <el-form-item prop="icon" :label="t('menu.Icon')">
                        <el-input
                            v-model="baTable.form.items!.icon"
                            type="string"
                            :placeholder="t('Please input field', { field: t('menu.Icon') })"
                        ></el-input>
                    </el-form-item>
                    <el-form-item prop="path" :label="t('menu.Path')">
                        <el-input
                            v-model="baTable.form.items!.path"
                            type="string"
                            :placeholder="t('Please input field', { field: t('menu.Path') })"
                        ></el-input>
                    </el-form-item>
                    <el-form-item prop="component" :label="t('menu.Component')">
                        <el-input
                            v-model="baTable.form.items!.component"
                            type="string"
                            :placeholder="t('Please input field', { field: t('menu.Component') })"
                        ></el-input>
                    </el-form-item>
                    <FormItem
                        :label="t('menu.Type')"
                        v-model="baTable.form.items!.type"
                        type="radio"
                        :input-attr="{
                            border: true,
                            content: { menu: t('menu.Menu item'), button: t('menu.Page button'), menu_dir: t('menu.Menu directory') },
                        }"
                    />
                    <el-form-item prop="displayOrder" :label="t('menu.Display order')">
                        <el-input-number
                            v-model="baTable.form.items!.displayOrder"
                            :min="0"
                            :placeholder="t('Please input field', { field: t('menu.Display order') })"
                        ></el-input-number>
                    </el-form-item>
                    <FormItem
                        :label="t('menu.Cache')"
                        v-model="baTable.form.items!.keepalive"
                        type="switch"
                        :input-attr="{ activeText: t('Enable'), inactiveText: t('Disable') }"
                    />
                    <FormItem
                        :label="t('menu.Status')"
                        v-model="baTable.form.items!.visible"
                        type="switch"
                        :input-attr="{ activeText: t('Enable'), inactiveText: t('Disable') }"
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
    title: [buildValidatorData({ name: 'required', title: t('menu.Title') })],
    name: [buildValidatorData({ name: 'required', title: t('menu.Name') })],
    path: [buildValidatorData({ name: 'required', title: t('menu.Path') })],
})
</script>

<style scoped lang="scss"></style>

