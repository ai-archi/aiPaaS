<template>
    <component :is="config.layout.layoutMode"></component>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useConfig } from '/@/stores/config'
import { useNavTabs } from '/@/stores/navTabs'
import { useTerminal } from '/@/stores/terminal'
import { useSiteConfig } from '/@/stores/siteConfig'
import { useAdminInfo } from '/@/stores/adminInfo'
import { useRoute } from 'vue-router'
import Default from '/@/layouts/backend/container/default.vue'
import Classic from '/@/layouts/backend/container/classic.vue'
import Streamline from '/@/layouts/backend/container/streamline.vue'
import Double from '/@/layouts/backend/container/double.vue'
import { onMounted, onBeforeMount } from 'vue'
import { Session } from '/@/utils/storage'
import { getWorkbenchMenus } from '/@/api/backend'
import { handleAdminRoute, getFirstRoute, routePush } from '/@/utils/router'
import router from '/@/router/index'
import { adminBaseRoutePath } from '/@/router/static/adminBase'
import { useEventListener } from '@vueuse/core'
import { BEFORE_RESIZE_LAYOUT } from '/@/stores/constant/cacheKey'
import { isEmpty } from 'lodash-es'
import { setNavTabsWidth } from '/@/utils/layout'

defineOptions({
    components: { Default, Classic, Streamline, Double },
})

const terminal = useTerminal()
const navTabs = useNavTabs()
const config = useConfig()
const route = useRoute()
const siteConfig = useSiteConfig()
const adminInfo = useAdminInfo()

const state = reactive({
    autoMenuCollapseLock: false,
})

onMounted(() => {
    if (!adminInfo.token) return router.push({ name: 'adminLogin' })

    // 强制清除初始化状态，确保重新获取菜单
    console.log('强制重新初始化，清除所有缓存状态')
    siteConfig.setInitialize(false)
    siteConfig.setUserInitialize(false)
    
    init()
    setNavTabsWidth()
    useEventListener(window, 'resize', setNavTabsWidth)
})
onBeforeMount(() => {
    onAdaptiveLayout()
    useEventListener(window, 'resize', onAdaptiveLayout)
})

const init = () => {
    /**
     * 后台初始化：直接获取workbench菜单
     */
    // 强制重新获取菜单，清除可能的缓存
    console.log('强制重新获取菜单，清除初始化状态')
    siteConfig.setInitialize(false)
    siteConfig.setUserInitialize(false)
    
    // 设置站点初始化状态
    siteConfig.setInitialize(true)
    
    // 如果用户已登录，设置用户初始化状态
    if (adminInfo.token) {
        siteConfig.setUserInitialize(true)
    }

    // 获取菜单
    getWorkbenchMenus()
        .then(async (res) => {
            console.log('获取菜单结果:', res)
            
            // 处理菜单路由
            if (res.data && Array.isArray(res.data) && res.data.length > 0) {
                console.log('开始调用 handleAdminRoute，路由数量:', res.data.length)
                handleAdminRoute(res.data)
                console.log('handleAdminRoute 调用完成')
                
                // 等待路由添加完成
                await new Promise(resolve => setTimeout(resolve, 100))
                
                // 检查是否有 to 参数（从 fallback 重定向过来的）
                if (route.params.to) {
                    let targetPath: string | null = null
                    try {
                        console.log('尝试解析 to 参数:', route.params.to)
                        const lastRoute = JSON.parse(route.params.to as string)
                        console.log('解析成功（JSON格式），目标路径:', lastRoute.path)
                        targetPath = lastRoute.path
                    } catch (e) {
                        // 如果不是 JSON 格式，直接使用 to 参数作为路径
                        console.log('to 参数不是 JSON 格式，直接使用为路径:', route.params.to)
                        targetPath = adminBaseRoutePath + '/' + (route.params.to as string)
                    }
                    
                    if (targetPath && !targetPath.includes('/loading') && targetPath != adminBaseRoutePath) {
                        console.log('准备跳转到:', targetPath)
                        await routePush({ path: targetPath })
                        console.log('跳转完成')
                        return
                    } else {
                        console.warn('目标路径无效或包含 loading，将跳转到第一个菜单')
                    }
                }
                
                // 如果是加载页面或未定义的路由，跳转到第一个菜单
                if (route.name == 'adminMainLoading' || route.path === adminBaseRoutePath || route.path === adminBaseRoutePath + '/') {
                    let firstRoute = getFirstRoute(navTabs.state.tabsViewRoutes)
                    if (firstRoute) {
                        // 确保路径正确
                        const targetPath = firstRoute.path
                        console.log('跳转到第一个菜单:', targetPath)
                        await routePush(targetPath)
                        console.log('跳转到第一个菜单完成')
                    }
                }
            } else {
                console.warn('没有获取到菜单数据')
            }
        })
        .catch((error) => {
            console.error('获取菜单失败:', error)
        })
}

const onAdaptiveLayout = () => {
    let defaultBeforeResizeLayout = {
        layoutMode: config.layout.layoutMode,
        menuCollapse: config.layout.menuCollapse,
    }
    let beforeResizeLayout = Session.get(BEFORE_RESIZE_LAYOUT)
    if (!beforeResizeLayout) Session.set(BEFORE_RESIZE_LAYOUT, defaultBeforeResizeLayout)

    const clientWidth = document.body.clientWidth
    if (clientWidth < 1024) {
        /**
         * 锁定窗口改变自动调整 menuCollapse
         * 避免已是小窗且打开了菜单栏时，意外的自动关闭菜单栏
         */
        if (!state.autoMenuCollapseLock) {
            state.autoMenuCollapseLock = true
            config.setLayout('menuCollapse', true)
        }
        config.setLayout('shrink', true)
        config.setLayoutMode('Classic')
    } else {
        state.autoMenuCollapseLock = false
        let beforeResizeLayoutTemp = beforeResizeLayout || defaultBeforeResizeLayout

        config.setLayout('menuCollapse', beforeResizeLayoutTemp.menuCollapse)
        config.setLayout('shrink', false)
        config.setLayoutMode(beforeResizeLayoutTemp.layoutMode)
    }
}
</script>
