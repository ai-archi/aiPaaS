import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
import type { ConfigEnv, UserConfig } from 'vite'
import { loadEnv } from 'vite'
import { svgBuilder } from '/@/components/icon/svg/index'
import { customHotUpdate, isProd } from '/@/utils/vite'

const pathResolve = (dir: string): any => {
    return resolve(__dirname, '.', dir)
}

// https://vitejs.cn/config/
const viteConfig = ({ mode }: ConfigEnv): UserConfig => {
    const { VITE_PORT, VITE_OPEN, VITE_BASE_PATH, VITE_OUT_DIR } = loadEnv(mode, process.cwd())

    const alias: Record<string, string> = {
        '/@': pathResolve('./src/'),
        '@shared': pathResolve('../shared/'),
        '@workspace': pathResolve('./'),
        '@applications': pathResolve('../applications/'),
        assets: pathResolve('./src/assets'),
        'vue-i18n': isProd(mode) ? 'vue-i18n/dist/vue-i18n.cjs.prod.js' : 'vue-i18n/dist/vue-i18n.cjs.js',
    }

    return {
        plugins: [vue(), svgBuilder('./src/assets/icons/'), customHotUpdate()],
        root: process.cwd(),
        resolve: { alias },
        base: VITE_BASE_PATH,
        server: {
            port: parseInt(VITE_PORT),
            open: VITE_OPEN != 'false',
            proxy: {
                // 认证相关API代理到Auth服务 (8080)
                // 参考租户API的配置方式，统一使用 /api/v1 前缀
                '/api/v1/auth': {
                    target: 'http://localhost:8080',
                    changeOrigin: true,
                    secure: false,
                },
                '/api/v1/verification-codes': {
                    target: 'http://localhost:8080',
                    changeOrigin: true,
                    secure: false,
                },
                // 租户相关API代理到目录服务 (8081)
                '^/tenants': {
                    target: 'http://localhost:8081/api/v1',
                    changeOrigin: true,
                    secure: false,
                },
                '^/tenant-groups': {
                    target: 'http://localhost:8081/api/v1',
                    changeOrigin: true,
                    secure: false,
                },
                // 菜单相关API代理到Directory服务 (8081)
                '/api/v1/menus': {
                    target: 'http://localhost:8081',
                    changeOrigin: true,
                    secure: false,
                },
                // 兼容旧的 /menus 路径（已废弃，建议使用 /api/v1/menus）
                '^/menus': {
                    target: 'http://localhost:8081/api/v1',
                    changeOrigin: true,
                    secure: false,
                },
                // 租户相关API代理到Directory服务 (8081)
                '/api/v1/tenants': {
                    target: 'http://localhost:8081',
                    changeOrigin: true,
                    secure: false,
                },
                '/api/v1/tenant-groups': {
                    target: 'http://localhost:8081',
                    changeOrigin: true,
                    secure: false,
                },
                // Directory服务其他模块API代理 (8081)
                '/api/v1/users': {
                    target: 'http://localhost:8081',
                    changeOrigin: true,
                    secure: false,
                },
                '/api/v1/roles': {
                    target: 'http://localhost:8081',
                    changeOrigin: true,
                    secure: false,
                },
                '/api/v1/organizations': {
                    target: 'http://localhost:8081',
                    changeOrigin: true,
                    secure: false,
                },
                '/api/v1/departments': {
                    target: 'http://localhost:8081',
                    changeOrigin: true,
                    secure: false,
                },
                '/api/v1/positions': {
                    target: 'http://localhost:8081',
                    changeOrigin: true,
                    secure: false,
                },
                '/api/v1/groups': {
                    target: 'http://localhost:8081',
                    changeOrigin: true,
                    secure: false,
                },
                // 权限相关API代理到Directory服务 (8081)
                '/api/v1/permissions': {
                    target: 'http://localhost:8081',
                    changeOrigin: true,
                    secure: false,
                },
                '/api/v1/permissions/data': {
                    target: 'http://localhost:8081',
                    changeOrigin: true,
                    secure: false,
                },
                // 工作台菜单API代理到工作台服务 (8084)
                '/api/v1/workbench': {
                    target: 'http://localhost:8084',
                    changeOrigin: true,
                    secure: false,
                },
                '/workbench': {
                    target: 'http://localhost:8084/api/v1',
                    changeOrigin: true,
                    secure: false,
                },
                // 其他API代理到工作台服务 (8084)
                '/api/v1': {
                    target: 'http://localhost:8084',
                    changeOrigin: true,
                    secure: false,
                },
            },
        },
        build: {
            cssCodeSplit: false,
            sourcemap: false,
            outDir: VITE_OUT_DIR,
            emptyOutDir: true,
            chunkSizeWarningLimit: 1500,
            rollupOptions: {
                output: {
                    manualChunks: {
                        // 分包配置，配置完成自动按需加载
                        vue: ['vue', 'vue-router', 'pinia', 'vue-i18n', 'element-plus'],
                        echarts: ['echarts'],
                    },
                },
            },
        },
    }
}

export default viteConfig
