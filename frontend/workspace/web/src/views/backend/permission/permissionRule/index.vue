<template>
    <div class="default-main ba-table-box">
        <el-alert class="ba-table-alert" v-if="baTable.table.remark" :title="baTable.table.remark" type="info" show-icon />

        <!-- 表格顶部菜单 -->
        <TableHeader
            :buttons="['refresh', 'add', 'edit', 'delete', 'comSearch', 'quickSearch', 'columnDisplay']"
            :quick-search-placeholder="t('Quick search placeholder', { fields: t('permissionRule.Path pattern') })"
        />

        <!-- 表格 -->
        <Table />

        <!-- 表单 -->
        <PopupForm />
    </div>
</template>

<script setup lang="ts">
import { provide } from 'vue'
import baTableClass from '/@/utils/baTable'
import PopupForm from './popupForm.vue'
import Table from '/@/components/table/index.vue'
import TableHeader from '/@/components/table/header/index.vue'
import { defaultOptButtons } from '/@/components/table'
import { baTableApi } from '/@/api/common'
import { useI18n } from 'vue-i18n'
import { useAdminInfo } from '/@/stores/adminInfo'

defineOptions({
    name: 'permission/permissionRule',
})

const { t } = useI18n()
const adminInfo = useAdminInfo()

const baTable = new baTableClass(
    new baTableApi('/api/v1/permissions'),
    {
        column: [
            { type: 'selection', align: 'center', operator: false },
            { label: t('Id'), prop: 'id', align: 'center', operator: '=', operatorPlaceholder: t('Id'), width: 70 },
            { label: t('permissionRule.Path pattern'), prop: 'pattern', align: 'center', operator: 'LIKE', operatorPlaceholder: t('Fuzzy query') },
            { 
                label: t('permissionRule.HTTP methods'), 
                prop: 'methods', 
                align: 'center',
                render: 'tag',
                replaceValue: (val: string[]) => {
                    return val ? val.join(', ') : ''
                },
            },
            { label: t('permissionRule.Permission identifier'), prop: 'permission', align: 'center', operator: 'LIKE', operatorPlaceholder: t('Fuzzy query') },
            { label: t('permissionRule.Priority'), prop: 'priority', align: 'center', sortable: 'custom', operator: '=', width: 100 },
            { 
                label: t('permissionRule.Enabled'), 
                prop: 'enabled', 
                align: 'center',
                render: 'tag',
                replaceValue: {
                    true: t('Yes'),
                    false: t('No'),
                },
                width: 100,
            },
            { label: t('permissionRule.Description'), prop: 'description', align: 'center', operator: 'LIKE', operatorPlaceholder: t('Fuzzy query'), showOverflowTooltip: true },
            { label: t('Update time'), prop: 'updatedAt', align: 'center', render: 'datetime', sortable: 'custom', operator: 'RANGE', width: 160 },
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
        filter: {
            tenantId: adminInfo.tenantId || 'default',
        },
        // 普通列表
    },
    {
        defaultItems: {
            tenantId: adminInfo.tenantId || 'default',
        },
    }
)

baTable.mount()
baTable.getData()

provide('baTable', baTable)
</script>

<style scoped lang="scss"></style>

