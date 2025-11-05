<template>
    <div class="default-main ba-table-box">
        <el-alert class="ba-table-alert" v-if="baTable.table.remark" :title="baTable.table.remark" type="info" show-icon />

        <!-- 表格顶部菜单 -->
        <TableHeader
            :buttons="['refresh', 'add', 'edit', 'delete', 'comSearch', 'quickSearch', 'columnDisplay']"
            :quick-search-placeholder="t('Quick search placeholder', { fields: t('menu.Title') })"
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
    name: 'menu',
})

const { t } = useI18n()
const adminInfo = useAdminInfo()

const baTable = new baTableClass(
    new baTableApi('/menus'),
    {
        column: [
            { type: 'selection', align: 'center', operator: false },
            { label: t('Id'), prop: 'id', align: 'center', operator: '=', operatorPlaceholder: t('Id'), width: 70 },
            { label: t('menu.Title'), prop: 'title', align: 'center', operator: 'LIKE', operatorPlaceholder: t('Fuzzy query') },
            { label: t('menu.Name'), prop: 'name', align: 'center', operator: 'LIKE', operatorPlaceholder: t('Fuzzy query') },
            { label: t('menu.Icon'), prop: 'icon', align: 'center', operator: false, width: 80 },
            { label: t('menu.Path'), prop: 'path', align: 'center', operator: 'LIKE', operatorPlaceholder: t('Fuzzy query') },
            {
                label: t('menu.Type'),
                prop: 'type',
                align: 'center',
                render: 'tag',
                operator: false,
            },
            {
                label: t('menu.Cache'),
                prop: 'keepalive',
                align: 'center',
                render: 'switch',
                operator: false,
            },
            {
                label: t('menu.Status'),
                prop: 'visible',
                align: 'center',
                render: 'switch',
                operator: false,
            },
            { label: t('menu.Update time'), prop: 'updatedAt', align: 'center', render: 'datetime', sortable: 'custom', operator: 'RANGE', width: 160 },
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
    },
    {
        defaultItems: {
            type: 'menu',
            keepalive: false,
            visible: true,
            displayOrder: 0,
            tenantId: adminInfo.tenantId || '00000000-0000-0000-0000-000000000000',
        },
    }
)

baTable.mount()
baTable.getData()

provide('baTable', baTable)
</script>

<style scoped lang="scss"></style>

