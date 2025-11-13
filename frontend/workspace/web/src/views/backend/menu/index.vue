<template>
    <div class="default-main ba-table-box">
        <el-alert class="ba-table-alert" v-if="baTable.table.remark" :title="baTable.table.remark" type="info" show-icon />

        <!-- 表格顶部菜单 -->
        <TableHeader
            :buttons="['refresh', 'add', 'edit', 'delete', 'unfold', 'quickSearch', 'columnDisplay']"
            :quick-search-placeholder="t('Quick search placeholder', { fields: t('menu.Title') })"
        />

        <!-- 设置合适的 max-height 实现隐藏布局主体部分本身的滚动条，这样就可以监听表格的 @scroll 了 -->
        <!-- max-height = 100vh - (当前布局顶栏高度 + 表头栏高度 + 表格上边距 + 预留的底部下边距) -->
        <Table
            ref="tableRef"
            :max-height="`calc(-${adminLayoutHeaderBarHeight[config.layout.layoutMode as keyof typeof adminLayoutHeaderBarHeight] + 75 + 16}px + 100vh)`"
            :pagination="false"
            :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
            @expand-change="onExpandChange"
            @scroll="onScroll"
        />

        <!-- 表单 -->
        <PopupForm />
    </div>
</template>

<script setup lang="ts">
import { cloneDeep, debounce } from 'lodash-es'
import { nextTick, onMounted, provide, useTemplateRef } from 'vue'
import baTableClass from '/@/utils/baTable'
import PopupForm from './popupForm.vue'
import Table from '/@/components/table/index.vue'
import TableHeader from '/@/components/table/header/index.vue'
import { defaultOptButtons } from '/@/components/table'
import { baTableApi } from '/@/api/common'
import { useI18n } from 'vue-i18n'
import { useConfig } from '/@/stores/config'
import { adminLayoutHeaderBarHeight } from '/@/utils/layout'

defineOptions({
    name: 'menu',
})

const { t } = useI18n()
const config = useConfig()
const tableRef = useTemplateRef('tableRef')

const baTable = new baTableClass(
    new baTableApi('/api/v1/menus'),
    {
        expandAll: false,
        dblClickNotEditColumn: [undefined, 'keepalive', 'visible'],
        column: [
            { type: 'selection', align: 'center' },
            { label: t('menu.Title'), prop: 'title', align: 'left', width: '200' },
            { label: t('menu.Icon'), prop: 'icon', align: 'center', width: '60', render: 'icon', default: 'fa fa-circle-o' },
            { label: t('menu.Name'), prop: 'name', align: 'center', showOverflowTooltip: true },
            {
                label: t('menu.Type'),
                prop: 'type',
                align: 'center',
                render: 'tag',
                custom: { menu: 'danger', menu_dir: 'success', button: 'info' },
                replaceValue: { menu: t('menu.Menu item'), menu_dir: t('menu.Menu directory'), button: t('menu.Page button') },
            },
            { label: t('menu.Cache'), prop: 'keepalive', align: 'center', width: '80', render: 'switch' },
            { label: t('menu.Status'), prop: 'visible', align: 'center', width: '80', render: 'switch' },
            { label: t('menu.Update time'), prop: 'updatedAt', align: 'center', width: '160', render: 'datetime' },
            {
                label: t('Operate'),
                align: 'center',
                width: '130',
                render: 'buttons',
                buttons: defaultOptButtons(),
            },
        ],
        dragSortLimitField: 'parentId',
        filter: {
            isTree: true, // 请求树形结构数据
        },
    },
    {
        defaultItems: {
            type: 'menu',
            renderType: 'tab',
            keepalive: false,
            visible: true,
            displayOrder: 0,
            icon: 'fa fa-circle-o',
        },
    }
)

// 重写 auth 方法，允许所有操作（如果权限系统未配置，可以临时返回 true）
baTable.auth = (node: string) => {
    // 如果需要权限控制，可以在这里实现权限检查逻辑
    // 目前返回 true 以显示所有按钮
    return true
}

/**
 * 内存缓存表格的一些状态数据，供数据刷新后恢复
 */
const sessionStateDefault = {
    expanded: [] as any[],
    scrollTop: 0,
    scrollLeft: 0,
    expandAll: false,
}
let sessionState = sessionStateDefault

/**
 * 记录表格行展开状态
 */
const onExpandChange = (row: any, expanded: boolean) => {
    if (expanded) {
        sessionState.expanded.push(row)
    } else {
        sessionState.expanded = sessionState.expanded.filter((item: any) => item.id !== row.id)
    }
}

/**
 * 记录表格滚动条位置
 */
const onScroll = debounce(({ scrollLeft, scrollTop }: { scrollLeft: number; scrollTop: number }) => {
    sessionState.scrollTop = scrollTop
    sessionState.scrollLeft = scrollLeft
}, 500)

/**
 * 记录表格行展开折叠状态
 */
const onUnfoldAll = (state: boolean) => {
    sessionState.expandAll = state
}

/**
 * 恢复已记录的表格状态
 */
const restoreState = () => {
    nextTick(() => {
        const sessionStateTemp = sessionState

        // 重置 sessionState 为默认值，恢复缓存的记录时将自动重设
        sessionState = cloneDeep(sessionStateDefault)

        for (const key in sessionStateTemp.expanded) {
            tableRef.value?.getRef()?.toggleRowExpansion(sessionStateTemp.expanded[key], true)
        }
        nextTick(() => {
            if (sessionStateTemp.scrollTop || sessionStateTemp.scrollLeft) {
                tableRef.value?.getRef()?.scrollTo({ top: sessionStateTemp.scrollTop || 0, left: sessionStateTemp.scrollLeft || 0 })
            }

            /**
             * expandAll 为 "是否默认展开所有行"
             * 此处表格数据已渲染，仅做顶部按钮状态标记用，不会实际上的执行展开折叠操作
             * 展开全部行之后，再只对某一行进行折叠时，expandAll 不会改变，所以此处并不根据 expandAll 值执行折叠展开所有行的操作
             */
            baTable.table.expandAll = sessionStateTemp.expandAll
            onUnfoldAll(sessionStateTemp.expandAll)
        })
    })
}

// 获取数据前钩子
baTable.before.getData = () => {
    baTable.table.expandAll = baTable.table.filter?.quickSearch ? true : false
}

// 获取到编辑行数据后的钩子
baTable.after.getEditData = () => {
    if (baTable.form.items && !baTable.form.items.icon) {
        baTable.form.items.icon = 'fa fa-circle-o'
    }
}

// 表格顶部按钮事件触发后的钩子
baTable.after.onTableHeaderAction = ({ event, data }: { event: string; data: any }) => {
    if (event == 'unfold') {
        onUnfoldAll(data.unfold)
    }
}

// 获取到表格数据后的钩子
// 后端应该直接返回树形结构的数据，不需要前端转换
baTable.after.getData = (res: any) => {
    // 调试：打印完整的响应数据
    console.log('菜单API完整响应:', res)
    console.log('响应数据结构:', {
        'res': res,
        'res.data': res?.data,
        'res.data.list': res?.data?.list,
        'res.data.data': res?.data?.data,
        'res.data.data.list': res?.data?.data?.list,
    })
    
    // 后端返回格式：{ code: 200, data: { list: [...], remark: '...' } }
    // list 应该是树形结构的数据
    let treeData: any[] = []
    
    if (res?.data?.list && Array.isArray(res.data.list)) {
        treeData = res.data.list
        console.log('从 res.data.list 获取数据，数量:', treeData.length)
    } else if (res?.data?.data?.list && Array.isArray(res.data.data.list)) {
        treeData = res.data.data.list
        console.log('从 res.data.data.list 获取数据，数量:', treeData.length)
    } else if (Array.isArray(res?.data)) {
        treeData = res.data
        console.log('从 res.data 获取数据（数组），数量:', treeData.length)
    } else if (Array.isArray(res)) {
        treeData = res
        console.log('从 res 获取数据（数组），数量:', treeData.length)
    } else {
        console.warn('无法从响应中提取数据，响应结构:', res)
    }
    
    // 调试：打印数据结构，确认是否有 children 字段
    console.log('菜单树形数据:', JSON.stringify(treeData, null, 2))
    if (treeData.length > 0) {
        console.log('第一个菜单项:', treeData[0])
        if (treeData[0] && treeData[0].children) {
            console.log('第一个菜单项的子菜单:', treeData[0].children)
        } else {
            console.warn('第一个菜单项没有 children 字段，数据可能是平铺的')
        }
    } else {
        console.warn('菜单树形数据为空数组，请检查后端是否返回了数据')
    }
    
    baTable.table.data = treeData
    
    restoreState()
}

provide('baTable', baTable)

onMounted(() => {
    baTable.table.ref = tableRef.value
    baTable.mount()
    baTable.getData()?.then(() => {
        baTable.dragSort()
    })
})
</script>

<style scoped lang="scss">
.default-main {
    margin-bottom: 0;
}
</style>

