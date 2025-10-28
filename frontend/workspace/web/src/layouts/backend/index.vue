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
    // 设置站点初始化状态
    siteConfig.setInitialize(true)
    
    // 如果用户已登录，设置用户初始化状态
    if (adminInfo.token) {
        siteConfig.setUserInitialize(true)
    }

    // 获取菜单
    getWorkbenchMenus()
        .then((res) => {
            console.log('获取菜单结果:', res)
            
            // 处理菜单路由
            if (res.data && Array.isArray(res.data) && res.data.length > 0) {
                handleAdminRoute(res.data)
                
                // 预跳转到上次路径
                if (route.params.to) {
                    const lastRoute = JSON.parse(route.params.to as string)
                    if (lastRoute.path != adminBaseRoutePath) {
                        let query = !isEmpty(lastRoute.query) ? lastRoute.query : {}
                        routePush({ path: lastRoute.path, query: query })
                        return
                    }
                }

                // 跳转到第一个菜单
                let firstRoute = getFirstRoute(navTabs.state.tabsViewRoutes)
                if (firstRoute) routePush(firstRoute.path)
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
