<template>
    <div class="default-main ba-table-box">
        <el-alert class="ba-table-alert" v-if="baTable.table.remark" :title="baTable.table.remark" type="info" show-icon />

        <!-- 表格顶部菜单 -->
        <TableHeader
            :buttons="['refresh', 'add', 'edit', 'delete', 'comSearch', 'quickSearch', 'columnDisplay']"
            :quick-search-placeholder="t('Quick search placeholder', { fields: t('tenant.group.Group Name') })"
        />

        <!-- 表格 -->
        <Table ref="tableRef" />

        <!-- 表单 -->
        <PopupForm ref="formRef" />
    </div>
</template>

<script setup lang="ts">
import { provide, useTemplateRef } from 'vue'
import { useI18n } from 'vue-i18n'
import PopupForm from './popupForm.vue'
import { baTableApi } from '/@/api/common'
import { defaultOptButtons } from '/@/components/table'
import TableHeader from '/@/components/table/header/index.vue'
import Table from '/@/components/table/index.vue'
import baTableClass from '/@/utils/baTable'

defineOptions({
    name: 'tenant/group',
})

const { t } = useI18n()
const formRef = useTemplateRef('formRef')
const tableRef = useTemplateRef('tableRef')

const baTable = new baTableClass(
    new baTableApi('/tenant-groups'),
    {
        column: [
            { type: 'selection', align: 'center', operator: false },
            { label: t('Id'), prop: 'id', align: 'center', operator: '=', operatorPlaceholder: t('Id'), width: 70 },
            { label: t('tenant.group.Group Name'), prop: 'name', align: 'center', operator: 'LIKE', operatorPlaceholder: t('Fuzzy query') },
            { label: t('tenant.group.Description'), prop: 'description', align: 'center', operator: 'LIKE', operatorPlaceholder: t('Fuzzy query') },
            {
                label: t('tenant.group.Parent group'),
                prop: 'parentName',
                align: 'center',
                operator: false,
                render: 'tag',
            },
            {
                label: t('tenant.group.Sort order'),
                prop: 'sortOrder',
                align: 'center',
                operator: '=',
                operatorPlaceholder: t('tenant.group.Sort order'),
                width: 100,
            },
            {
                label: t('tenant.group.Status'),
                prop: 'status',
                align: 'center',
                render: 'tag',
                custom: { ACTIVE: 'success', SUSPENDED: 'danger' },
                replaceValue: { ACTIVE: t('tenant.group.Active'), SUSPENDED: t('tenant.group.Suspended') },
            },
            { label: t('Create time'), prop: 'createdAt', align: 'center', render: 'datetime', sortable: 'custom', operator: 'RANGE', width: 160 },
            {
                label: t('Operate'),
                align: 'center',
                width: '130',
                render: 'buttons',
                buttons: defaultOptButtons(['edit', 'delete']),
                operator: false,
            },
        ],
        dblClickNotEditColumn: [undefined],
    },
    {
        defaultItems: {
            status: 'ACTIVE',
            sortOrder: 0,
        },
    }
)

baTable.mount()
baTable.getData()

provide('baTable', baTable)
</script>

<style scoped lang="scss"></style>

