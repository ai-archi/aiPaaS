import createAxios from '/@/utils/axios'

/**
 * Dashboard API
 * 调用后端 workbench 服务
 */
export const url = '/workbench/dashboard'

/**
 * 获取仪表盘数据
 * 对应后端：GET /workbench/dashboard/index
 * 返回：{ remark: "...", statistics: {...} }
 */
export function index() {
    return createAxios({
        url: url + '/index',
        method: 'get',
    })
}

/**
 * 获取用户仪表盘配置
 * 对应后端：GET /workbench/dashboard
 */
export function getDashboard(params: { userId: string; tenantId: string }) {
    return createAxios({
        url: url,
        method: 'get',
        params: params,
    })
}

/**
 * 保存用户仪表盘配置
 * 对应后端：PUT /workbench/dashboard
 */
export function saveDashboard(data: any, params: { userId: string; tenantId: string }) {
    return createAxios({
        url: url,
        method: 'put',
        data: data,
        params: params,
    })
}
