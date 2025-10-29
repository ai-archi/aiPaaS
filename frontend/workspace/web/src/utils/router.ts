import { ElNotification } from 'element-plus'
import { compact, isEmpty, reverse } from 'lodash-es'
import type { RouteLocationRaw, RouteRecordRaw } from 'vue-router'
import { isNavigationFailure, NavigationFailureType } from 'vue-router'
import { i18n } from '/@/lang/index'
import router from '/@/router/index'
import adminBaseRoute from '/@/router/static/adminBase'
import memberCenterBaseRoute from '/@/router/static/memberCenterBase'
import { useConfig } from '/@/stores/config'
import { useMemberCenter } from '/@/stores/memberCenter'
import { useNavTabs } from '/@/stores/navTabs'
import { useSiteConfig } from '/@/stores/siteConfig'
import { isAdminApp } from '/@/utils/common'
import { closeShade } from '/@/utils/pageShade'

/**
 * 导航失败有错误消息的路由push
 * @param to — 导航位置，同 router.push
 */
export const routePush = async (to: RouteLocationRaw) => {
    try {
        const failure = await router.push(to)
        if (isNavigationFailure(failure, NavigationFailureType.aborted)) {
            ElNotification({
                message: i18n.global.t('utils.Navigation failed, navigation guard intercepted!'),
                type: 'error',
            })
        } else if (isNavigationFailure(failure, NavigationFailureType.duplicated)) {
            // 静默处理重复导航，无需提示用户
            // 这是正常情况，比如刷新页面或重复点击相同菜单
            console.debug('Navigation to current location suppressed')
        }
    } catch (error) {
        ElNotification({
            message: i18n.global.t('utils.Navigation failed, invalid route!'),
            type: 'error',
        })
        console.error(error)
    }
}

/**
 * 获取第一个菜单
 */
export const getFirstRoute = (routes: RouteRecordRaw[]): false | RouteRecordRaw => {
    const routerPaths: string[] = []
    const routers = router.getRoutes()
    routers.forEach((item) => {
        if (item.path) routerPaths.push(item.path)
    })
    let find: boolean | RouteRecordRaw = false
    for (const key in routes) {
        if (routes[key].meta?.type == 'menu' && routerPaths.indexOf(routes[key].path) !== -1) {
            return routes[key]
        } else if (routes[key].children && routes[key].children?.length) {
            find = getFirstRoute(routes[key].children!)
            if (find) return find
        }
    }
    return find
}

/**
 * 打开侧边菜单
 * @param menu 菜单数据
 */
export const onClickMenu = (menu: RouteRecordRaw) => {
    console.log('onClickMenu called:', {
        name: menu.name,
        path: menu.path,
        renderType: menu.meta?.renderType,
        component: menu.component,
        fullMenu: menu
    })
    
    switch (menu.meta?.renderType) {
        case 'iframe':
        case 'tab':
            console.log('Pushing to path:', menu.path)
            routePush(menu.path)
            break
        case 'link':
            window.open(menu.path, '_blank')
            break

        default:
            console.error('Unknown renderType:', menu.meta?.renderType)
            ElNotification({
                message: i18n.global.t('utils.Navigation failed, the menu type is unrecognized!'),
                type: 'error',
            })
            break
    }

    const config = useConfig()
    if (config.layout.shrink) {
        closeShade(() => {
            config.setLayout('menuCollapse', true)
        })
    }
}

/**
 * 处理前台的路由
 * @param routes 路由规则
 * @param menus 会员中心菜单路由规则
 */
export const handleFrontendRoute = (routes: any, menus: any) => {
    const siteConfig = useSiteConfig()
    const memberCenter = useMemberCenter()
    const viewsComponent = import.meta.glob('/src/views/frontend/**/*.vue')

    if (routes.length) {
        addRouteAll(viewsComponent, routes, '', true)
        memberCenter.mergeAuthNode(handleAuthNode(routes, '/'))
        siteConfig.setHeadNav(handleMenuRule(routes, '/', ['nav']))
        memberCenter.mergeNavUserMenus(handleMenuRule(routes, '/', ['nav_user_menu']))
    }
    if (menus.length && isEmpty(memberCenter.state.viewRoutes)) {
        addRouteAll(viewsComponent, menus, memberCenterBaseRoute.name as string)
        const menuMemberCenterBaseRoute = (memberCenterBaseRoute.path as string) + '/'
        memberCenter.mergeAuthNode(handleAuthNode(menus, menuMemberCenterBaseRoute))

        memberCenter.mergeNavUserMenus(handleMenuRule(menus, '/', ['nav_user_menu']))
        memberCenter.setShowHeadline(menus.length > 1)
        memberCenter.setViewRoutes(handleMenuRule(menus, menuMemberCenterBaseRoute))
    }
}

/**
 * 处理后台的路由
 */
export const handleAdminRoute = (routes: any) => {
    console.log('handleAdminRoute called with routes:', routes)
    const viewsComponent = import.meta.glob('/src/views/backend/**/*.vue')
    console.log('viewsComponent from glob:', Object.keys(viewsComponent).slice(0, 20))
    
    addRouteAll(viewsComponent, routes, adminBaseRoute.name as string)
    const menuAdminBaseRoute = (adminBaseRoute.path as string) + '/'

    // 更新stores中的路由菜单数据
    // 注意：后端返回的 path 已经包含 /admin/ 前缀，所以这里不需要再加前缀
    const navTabs = useNavTabs()
    navTabs.setTabsViewRoutes(handleMenuRule(routes, ''))
    navTabs.fillAuthNode(handleAuthNode(routes, menuAdminBaseRoute))
}

/**
 * 获取菜单的paths
 */
export const getMenuPaths = (menus: RouteRecordRaw[]): string[] => {
    let menuPaths: string[] = []
    menus.forEach((item) => {
        menuPaths.push(item.path)
        if (item.children && item.children.length > 0) {
            menuPaths = menuPaths.concat(getMenuPaths(item.children))
        }
    })
    return menuPaths
}

/**
 * 获取菜单唯一标识
 * @param menu 菜单数据
 * @param prefix 前缀
 */
export const getMenuKey = (menu: RouteRecordRaw, prefix = '') => {
    if (prefix === '') {
        prefix = menu.path
    }
    return `${prefix}-${menu.name as string}-${menu.meta && menu.meta.id ? menu.meta.id : ''}`
}

/**
 * 会员中心和后台的菜单处理
 */
const handleMenuRule = (routes: any, pathPrefix = '/', type = ['menu', 'menu_dir']) => {
    const menuRule: RouteRecordRaw[] = []
    for (const key in routes) {
        if (routes[key].extend == 'add_rules_only') {
            continue
        }
        if (!type.includes(routes[key].type)) {
            continue
        }
        if (routes[key].type == 'menu_dir' && routes[key].children && !routes[key].children.length) {
            continue
        }
        if (
            ['route', 'menu', 'nav_user_menu', 'nav'].includes(routes[key].type) &&
            ((routes[key].renderType == 'tab' && !routes[key].component) || (['link', 'iframe'].includes(routes[key].renderType) && !routes[key].url))
        ) {
            continue
        }
        // 直接使用后端返回的完整路径
        const currentPath = ['link', 'iframe'].includes(routes[key].renderType) ? routes[key].url : routes[key].path
        let children: RouteRecordRaw[] = []
        if (routes[key].children && routes[key].children.length > 0) {
            children = handleMenuRule(routes[key].children, pathPrefix, type)
        }
        menuRule.push({
            path: currentPath,
            name: routes[key].name,
            component: routes[key].component,
            meta: {
                id: routes[key].id,
                title: routes[key].title,
                icon: routes[key].icon,
                keepalive: routes[key].keepalive,
                renderType: routes[key].renderType,
                type: routes[key].type,
            },
            children: children,
        })
    }
    return menuRule
}

/**
 * 处理权限节点
 * @param routes 路由数据
 * @param prefix 节点前缀
 * @returns 组装好的权限节点
 */
const handleAuthNode = (routes: any, prefix = '/') => {
    const authNode: Map<string, string[]> = new Map([])
    assembleAuthNode(routes, authNode, prefix, prefix)
    return authNode
}
const assembleAuthNode = (routes: any, authNode: Map<string, string[]>, prefix = '/', parent = '/') => {
    const authNodeTemp = []
    for (const key in routes) {
        if (routes[key].type == 'button') authNodeTemp.push(prefix + routes[key].name)
        if (routes[key].children && routes[key].children.length > 0) {
            assembleAuthNode(routes[key].children, authNode, prefix, prefix + routes[key].name)
        }
    }
    if (authNodeTemp && authNodeTemp.length > 0) {
        authNode.set(parent, authNodeTemp)
    }
}

/**
 * 动态添加路由-带子路由
 * @param viewsComponent
 * @param routes
 * @param parentName
 * @param analyticRelation 根据 name 从已注册路由分析父级路由
 */
export const addRouteAll = (viewsComponent: Record<string, any>, routes: any, parentName: string, analyticRelation = false) => {
    console.log('addRouteAll - viewsComponent keys:', Object.keys(viewsComponent).slice(0, 10))
    
    for (const idx in routes) {
        if (routes[idx].extend == 'add_menu_only') {
            continue
        }
        
        console.log('Processing route:', routes[idx].name, 'component:', routes[idx].component)
        
        if ((routes[idx].renderType == 'tab' && viewsComponent[routes[idx].component]) || routes[idx].renderType == 'iframe') {
            addRouteItem(viewsComponent, routes[idx], parentName, analyticRelation)
        } else {
            console.warn('Route skipped:', routes[idx].name, 'renderType:', routes[idx].renderType, 'component exists:', !!viewsComponent[routes[idx].component])
        }

        if (routes[idx].children && routes[idx].children.length > 0) {
            addRouteAll(viewsComponent, routes[idx].children, parentName, analyticRelation)
        }
    }
}

/**
 * 动态添加路由
 * @param viewsComponent
 * @param route
 * @param parentName
 * @param analyticRelation 根据 name 从已注册路由分析父级路由
 */
export const addRouteItem = (viewsComponent: Record<string, any>, route: any, parentName: string, analyticRelation: boolean) => {
    let path = '',
        component
    if (route.renderType == 'iframe') {
        path = (isAdminApp() ? adminBaseRoute.path : memberCenterBaseRoute.path) + '/iframe/' + encodeURIComponent(route.url)
        component = () => import('/@/layouts/common/router-view/iframe.vue')
    } else {
        // 直接使用后端返回的完整路径，不需要额外处理
        path = route.path || ''
        component = viewsComponent[route.component]
        
        // 调试信息
        if (!component) {
            console.error('Component not found:', {
                component: route.component,
                availableComponents: Object.keys(viewsComponent),
                route: route
            })
        }
    }

    if (route.renderType == 'tab' && analyticRelation) {
        const parentNames = getParentNames(route.name)
        if (parentNames.length) {
            for (const key in parentNames) {
                if (router.hasRoute(parentNames[key])) {
                    parentName = parentNames[key]
                    break
                }
            }
        }
    }

    const routeBaseInfo: RouteRecordRaw = {
        path: path,
        name: route.name,
        component: component,
        meta: {
            title: route.title,
            extend: route.extend,
            icon: route.icon,
            keepalive: route.keepalive,
            renderType: route.renderType,
            type: route.type,
            url: route.url,
            addtab: true,
        },
    }
    if (parentName) {
        router.addRoute(parentName, routeBaseInfo)
    } else {
        router.addRoute(routeBaseInfo)
    }
}

/**
 * 根据name字符串，获取父级name组合的数组
 * @param name
 */
const getParentNames = (name: string) => {
    const names = compact(name.split('/'))
    const tempNames = []
    const parentNames = []
    for (const key in names) {
        tempNames.push(names[key])
        if (parseInt(key) != names.length - 1) {
            parentNames.push(tempNames.join('/'))
        }
    }
    return reverse(parentNames)
}
