<template>
    <div class="default-main ba-table-box">
        <el-alert class="ba-table-alert" v-if="baTable.table.remark" :title="baTable.table.remark" type="info" show-icon />

        <!-- 表格顶部菜单 -->
        <TableHeader
            :buttons="['refresh', 'add', 'edit', 'delete', 'comSearch', 'quickSearch', 'columnDisplay']"
            :quick-search-placeholder="t('Quick search placeholder', { fields: t('position.Position name') })"
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
    name: 'position/position',
})

const { t } = useI18n()
const adminInfo = useAdminInfo()

const baTable = new baTableClass(
    new baTableApi('/api/v1/positions'),
    {
        column: [
            { type: 'selection', align: 'center', operator: false },
            { label: t('Id'), prop: 'id', align: 'center', operator: '=', operatorPlaceholder: t('Id'), width: 70 },
            { label: t('position.Position name'), prop: 'name', align: 'center', operator: 'LIKE', operatorPlaceholder: t('Fuzzy query') },
            { label: t('position.Description'), prop: 'description', align: 'center', operator: 'LIKE', operatorPlaceholder: t('Fuzzy query') },
            { label: t('position.Organization'), prop: 'orgName', align: 'center', operator: false, render: 'tag' },
            { label: t('position.Parent position'), prop: 'parentName', align: 'center', operator: false },
            { label: t('position.Update time'), prop: 'updatedAt', align: 'center', render: 'datetime', sortable: 'custom', operator: 'RANGE', width: 160 },
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
        // 启用树形表格（岗位序列制）
        expandAll: false,
    },
    {
        defaultItems: {
            tenantId: adminInfo.tenantId || 'default',
        },
    }
)

/**
 * 将平铺的岗位列表转换为树形结构（岗位序列制）
 * @param positions 平铺的岗位列表
 * @returns 树形结构的岗位列表
 */
function buildPositionTree(positions: any[]): any[] {
    if (!positions || !Array.isArray(positions)) {
        return []
    }
    
    // 创建岗位映射表，key为岗位ID
    const posMap = new Map<string, any>()
    positions.forEach(pos => {
        posMap.set(pos.id, { ...pos, children: [] })
    })
    
    // 构建树形结构
    const rootPositions: any[] = []
    positions.forEach(pos => {
        const posNode = posMap.get(pos.id)!
        // 如果后端支持 parentId，使用 parentId；否则作为根岗位处理
        if (!pos.parentId) {
            // 根岗位（最高级别）
            rootPositions.push(posNode)
        } else {
            // 子岗位，添加到父岗位的children中
            const parent = posMap.get(pos.parentId)
            if (parent) {
                if (!parent.children) {
                    parent.children = []
                }
                parent.children.push(posNode)
            } else {
                // 父岗位不存在，作为根岗位处理
                rootPositions.push(posNode)
            }
        }
    })
    
    return rootPositions
}

// 重写 getData 方法，在获取数据后转换为树形结构
baTable.after.getData = (res: any) => {
    // 将平铺的岗位列表转换为树形结构
    const flatPositions = res.data?.list || []
    const treePositions = buildPositionTree(flatPositions)
    
    // 设置树形数据
    baTable.table.data = treePositions
}

baTable.mount()
baTable.getData()

provide('baTable', baTable)
</script>

<style scoped lang="scss"></style>

