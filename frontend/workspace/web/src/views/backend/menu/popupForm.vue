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
                    <FormItem
                        type="remoteSelect"
                        prop="parentId"
                        :label="t('menu.Superior menu rule')"
                        v-model="baTable.form.items!.parentId"
                        :placeholder="t('Click select')"
                        :input-attr="{
                            params: { isTree: true },
                            field: 'title',
                            remoteUrl: baTable.api.actionUrl.get('index'),
                            emptyValues: ['', null, undefined, 0],
                            valueOnClear: null,
                        }"
                    />
                    <FormItem
                        :label="t('menu.Rule type')"
                        v-model="baTable.form.items!.type"
                        type="radio"
                        :input-attr="{
                            border: true,
                            content: { menu_dir: t('menu.Menu directory'), menu: t('menu.Menu item'), button: t('menu.Page button') },
                        }"
                    />
                    <el-form-item prop="title" :label="t('menu.Rule title')">
                        <el-input
                            v-model="baTable.form.items!.title"
                            type="string"
                            :placeholder="t('Please input field', { field: t('menu.Rule title') })"
                        ></el-input>
                    </el-form-item>
                    <el-form-item prop="name" :label="t('menu.Rule name')">
                        <el-input
                            v-model="baTable.form.items!.name"
                            type="string"
                            :placeholder="t('menu.English name, which does not need to start with `/admin`, such as menu/index')"
                        ></el-input>
                        <div class="block-help">
                            {{ t('menu.It will be registered as the web side routing name and used as the server side API authentication') }}
                        </div>
                    </el-form-item>
                    <el-form-item prop="path" v-if="baTable.form.items!.type != 'button'" :label="t('menu.Routing path')">
                        <el-input
                            v-model="baTable.form.items!.path"
                            type="string"
                            :placeholder="t('menu.The web side routing path (path) does not need to start with `/admin`, such as menu/index')"
                        ></el-input>
                    </el-form-item>
                    <FormItem
                        v-if="baTable.form.operate && baTable.form.items!.type != 'button'"
                        type="icon"
                        :label="t('menu.Rule Icon')"
                        v-model="baTable.form.items!.icon"
                        :input-attr="{
                            showIconName: true,
                        }"
                    />
                    <FormItem
                        v-if="baTable.form.items!.type == 'menu'"
                        :label="t('menu.Menu type')"
                        v-model="baTable.form.items!.renderType"
                        type="radio"
                        :input-attr="{
                            border: true,
                            content: { tab: t('menu.Menu type tab'), link: t('menu.Menu type link (offsite)'), iframe: 'Iframe' },
                        }"
                    />
                    <el-form-item
                        prop="url"
                        v-if="baTable.form.items!.renderType != 'tab' && baTable.form.items!.type == 'menu'"
                        :label="t('menu.Link address')"
                    >
                        <el-input
                            v-model="baTable.form.items!.url"
                            type="string"
                            :placeholder="t('menu.Please enter the URL address of the link or iframe')"
                        ></el-input>
                    </el-form-item>
                    <el-form-item
                        prop="component"
                        v-if="baTable.form.items!.type == 'menu' && baTable.form.items!.renderType == 'tab'"
                        :label="t('menu.Component path')"
                    >
                        <el-input
                            v-model="baTable.form.items!.component"
                            type="string"
                            :placeholder="t('menu.Web side component path, please start with /src, such as: /src/views/backend/menu/index')"
                        ></el-input>
                    </el-form-item>
                    <el-form-item
                        v-if="baTable.form.items!.type == 'menu' && baTable.form.items!.renderType == 'tab'"
                        :label="t('menu.Extended properties')"
                    >
                        <el-select
                            class="w100"
                            v-model="baTable.form.items!.extend"
                            :placeholder="t('Please select field', { field: t('menu.Extended properties') })"
                        >
                            <el-option :label="t('menu.none')" value="none"></el-option>
                            <el-option :label="t('menu.Add as route only')" value="add_rules_only"></el-option>
                            <el-option :label="t('menu.Add as menu only')" value="add_menu_only"></el-option>
                        </el-select>
                        <div class="block-help">{{ t('menu.extend Title') }}</div>
                    </el-form-item>
                    <el-form-item :label="t('menu.Rule weight')">
                        <el-input
                            v-model="baTable.form.items!.displayOrder"
                            type="number"
                            :placeholder="t('menu.Please enter the weight of menu rule (sort by)')"
                        ></el-input>
                    </el-form-item>
                    <FormItem
                        :label="t('menu.cache')"
                        v-model="baTable.form.items!.keepalive"
                        type="radio"
                        :input-attr="{
                            border: true,
                            content: { false: t('Disable'), true: t('Enable') },
                        }"
                    />
                    <FormItem
                        :label="t('State')"
                        v-model="baTable.form.items!.visible"
                        type="radio"
                        :input-attr="{
                            border: true,
                            content: { false: t('Disable'), true: t('Enable') },
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
    title: [buildValidatorData({ name: 'required', title: t('menu.Rule title') })],
    name: [buildValidatorData({ name: 'required', title: t('menu.Rule name') })],
    path: [buildValidatorData({ name: 'required', title: t('menu.Routing path') })],
    url: [
        buildValidatorData({ name: 'required', title: t('menu.Link address') }),
        buildValidatorData({ name: 'url', message: t('menu.Please enter the correct URL') }),
    ],
    component: [buildValidatorData({ name: 'required', message: t('menu.Component path') })],
    parentId: [
        {
            validator: (rule: any, val: string, callback: Function) => {
                if (!val) {
                    return callback()
                }
                if (val == baTable.form.items!.id) {
                    return callback(new Error(t('menu.The superior menu rule cannot be the rule itself')))
                }
                return callback()
            },
            trigger: 'blur',
        },
    ],
})

// 在提交前转换数据类型
baTable.before.onSubmit = ({ items }: { items: any }) => {
    // 将 keepalive 和 visible 转换为布尔值
    // FormItem radio 可能返回字符串 "true"/"false"、"0"/"1" 或布尔值
    if (items.keepalive !== undefined && items.keepalive !== null) {
        if (typeof items.keepalive === 'string') {
            items.keepalive = items.keepalive === 'true' || items.keepalive === '1'
        } else if (typeof items.keepalive === 'number') {
            items.keepalive = items.keepalive === 1
        }
    }
    
    if (items.visible !== undefined && items.visible !== null) {
        if (typeof items.visible === 'string') {
            items.visible = items.visible === 'true' || items.visible === '1'
        } else if (typeof items.visible === 'number') {
            items.visible = items.visible === 1
        }
    }
    
    // 处理 parentId：如果是空字符串或 "0"，转换为 null
    if (items.parentId === '' || items.parentId === '0' || items.parentId === 0) {
        items.parentId = null
    }
    
    // 将 displayOrder 转换为数字
    if (items.displayOrder !== undefined && items.displayOrder !== null) {
        if (typeof items.displayOrder === 'string') {
            const num = parseInt(items.displayOrder, 10)
            items.displayOrder = isNaN(num) ? 0 : num
        }
    } else {
        // 如果 displayOrder 未设置，默认为 0
        items.displayOrder = 0
    }
    
    // 清理空字符串字段，转换为 null（可选字段）
    const optionalStringFields = ['icon', 'component', 'url', 'config', 'extend']
    optionalStringFields.forEach(field => {
        if (items[field] === '') {
            items[field] = null
        }
    })
}
</script>

<style scoped lang="scss"></style>

