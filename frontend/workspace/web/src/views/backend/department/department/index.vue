<template>
    <div class="default-main ba-table-box">
        <el-alert class="ba-table-alert" v-if="baTable.table.remark" :title="baTable.table.remark" type="info" show-icon />

        <!-- 表格顶部菜单 -->
        <TableHeader
            :buttons="['refresh', 'add', 'edit', 'delete', 'comSearch', 'quickSearch', 'columnDisplay']"
            :quick-search-placeholder="t('Quick search placeholder', { fields: t('department.Department name') })"
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
    name: 'department/department',
})

const { t } = useI18n()
const adminInfo = useAdminInfo()

const baTable = new baTableClass(
    new baTableApi('/api/v1/departments'),
    {
        column: [
            { type: 'selection', align: 'center', operator: false },
            { label: t('Id'), prop: 'id', align: 'center', operator: '=', operatorPlaceholder: t('Id'), width: 70 },
            { label: t('department.Department name'), prop: 'name', align: 'center', operator: 'LIKE', operatorPlaceholder: t('Fuzzy query') },
            { label: t('department.Description'), prop: 'description', align: 'center', operator: 'LIKE', operatorPlaceholder: t('Fuzzy query') },
            { label: t('department.Organization'), prop: 'orgName', align: 'center', operator: false, render: 'tag' },
            { label: t('department.Parent department'), prop: 'parentName', align: 'center', operator: false },
            { label: t('department.Update time'), prop: 'updatedAt', align: 'center', render: 'datetime', sortable: 'custom', operator: 'RANGE', width: 160 },
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
        // 启用树形表格
        expandAll: false,
    },
    {
        defaultItems: {
            tenantId: adminInfo.tenantId || 'default',
        },
    }
)

/**
 * 将平铺的部门列表转换为树形结构
 * @param departments 平铺的部门列表
 * @returns 树形结构的部门列表
 */
function buildDepartmentTree(departments: any[]): any[] {
    if (!departments || !Array.isArray(departments)) {
        return []
    }
    
    // 创建部门映射表，key为部门ID
    const deptMap = new Map<string, any>()
    departments.forEach(dept => {
        deptMap.set(dept.id, { ...dept, children: [] })
    })
    
    // 构建树形结构
    const rootDepartments: any[] = []
    departments.forEach(dept => {
        const deptNode = deptMap.get(dept.id)!
        if (!dept.parentId) {
            // 根部门
            rootDepartments.push(deptNode)
        } else {
            // 子部门，添加到父部门的children中
            const parent = deptMap.get(dept.parentId)
            if (parent) {
                if (!parent.children) {
                    parent.children = []
                }
                parent.children.push(deptNode)
            } else {
                // 父部门不存在，作为根部门处理
                rootDepartments.push(deptNode)
            }
        }
    })
    
    return rootDepartments
}

// 重写 getData 方法，在获取数据后转换为树形结构
baTable.after.getData = (res: any) => {
    // 将平铺的部门列表转换为树形结构
    const flatDepartments = res.data?.list || []
    const treeDepartments = buildDepartmentTree(flatDepartments)
    
    // 设置树形数据
    baTable.table.data = treeDepartments
}

baTable.mount()
baTable.getData()

provide('baTable', baTable)
</script>

<style scoped lang="scss"></style>

