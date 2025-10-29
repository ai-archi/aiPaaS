import { createRouter, createWebHashHistory } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import staticRoutes from '/@/router/static'
import { loading } from '/@/utils/loading'

const router = createRouter({
    history: createWebHashHistory(),
    routes: staticRoutes,
})

router.beforeEach((to, from, next) => {
    console.log('路由跳转:', to.path, to.name)
    NProgress.configure({ showSpinner: false })
    NProgress.start()

    // 简化loading逻辑
    if (!window.existLoading) {
        loading.show()
        window.existLoading = true
    }

    next()
})

// 路由加载后
router.afterEach(() => {
    console.log('路由跳转完成')
    if (window.existLoading) {
        loading.hide()
    }
    NProgress.done()
})

export default router
