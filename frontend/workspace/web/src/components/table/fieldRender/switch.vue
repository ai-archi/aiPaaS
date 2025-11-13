<template>
    <div>
        <el-switch
            v-if="field.prop"
            @change="onChange"
            :model-value="cellValue"
            :loading="loading"
            active-value="1"
            inactive-value="0"
            v-bind="invokeTableContextDataFun(field.customRenderAttr?.switch, { row, field, cellValue, column, index })"
        />
    </div>
</template>

<script setup lang="ts">
import { TableColumnCtx } from 'element-plus'
import { inject, ref } from 'vue'
import { getCellValue, invokeTableContextDataFun } from '/@/components/table/index'
import type baTableClass from '/@/utils/baTable'

interface Props {
    row: TableRow
    field: TableColumn
    column: TableColumnCtx<TableRow>
    index: number
}

const loading = ref(false)
const props = defineProps<Props>()
const baTable = inject('baTable') as baTableClass
const cellValue = ref(getCellValue(props.row, props.field, props.column, props.index))

// 将值转换为 el-switch 期望的格式：字符串 "1" 或 "0"
if (typeof cellValue.value === 'boolean') {
    cellValue.value = cellValue.value ? '1' : '0'
} else if (typeof cellValue.value === 'number') {
    cellValue.value = cellValue.value ? '1' : '0'
} else if (typeof cellValue.value === 'string') {
    // 如果已经是字符串，确保是 "1" 或 "0"
    if (cellValue.value === 'true' || cellValue.value === 'True') {
        cellValue.value = '1'
    } else if (cellValue.value === 'false' || cellValue.value === 'False') {
        cellValue.value = '0'
    }
    // 如果已经是 "1" 或 "0"，保持不变
}

const onChange = (value: string | number | boolean) => {
    loading.value = true
    
    // 将值转换为后端期望的格式
    // 如果后端期望布尔值，需要将 "1"/"0" 转换为 true/false
    // 如果后端期望字符串，保持 "1"/"0"
    let backendValue: any = value
    if (value === '1' || value === 1 || value === true) {
        // 根据字段类型决定发送给后端的值
        // 对于 keepalive 和 visible 字段，后端期望布尔值
        if (props.field.prop === 'keepalive' || props.field.prop === 'visible') {
            backendValue = true
        } else {
            backendValue = value
        }
    } else if (value === '0' || value === 0 || value === false) {
        if (props.field.prop === 'keepalive' || props.field.prop === 'visible') {
            backendValue = false
        } else {
            backendValue = value
        }
    }
    
    baTable.api
        .postData('edit', {
            [baTable.table.pk!]: props.row[baTable.table.pk!],
            [props.field.prop!]: backendValue,
        })
        .then(() => {
            // 更新本地值，确保格式为 "1" 或 "0"（el-switch 期望的格式）
            if (backendValue === true || backendValue === 'true' || backendValue === 1 || backendValue === '1') {
                cellValue.value = '1'
            } else {
                cellValue.value = '0'
            }
            baTable.onTableAction('field-change', { value: backendValue, ...props })
        })
        .finally(() => {
            loading.value = false
        })
}
</script>
