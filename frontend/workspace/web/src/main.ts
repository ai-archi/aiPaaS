import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { loadLang } from '/@/lang/index'
import { registerIcons } from '/@/utils/common'
import ElementPlus from 'element-plus'
import mitt from 'mitt'
import pinia from '/@/stores/index'
import { directives } from '/@/utils/directives'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/display.css'
import 'font-awesome/css/font-awesome.min.css'
import '/@/styles/index.scss'
// modules import mark, Please do not remove.

async function start() {
    console.log('前端开始启动...');
    try {
        const app = createApp(App)
        console.log('Vue应用创建成功');
        
        app.use(pinia)
        console.log('Pinia注册成功');

        // 全局语言包加载
        await loadLang(app)
        console.log('语言包加载成功');

        app.use(router)
        console.log('路由注册成功');
        
        app.use(ElementPlus)
        console.log('ElementPlus注册成功');

        // 全局注册
        directives(app) // 指令
        console.log('指令注册成功');
        
        registerIcons(app) // icons
        console.log('图标注册成功');

        app.mount('#app')
        console.log('应用挂载成功');

        // modules start mark, Please do not remove.

        app.config.globalProperties.eventBus = mitt()
        console.log('前端启动完成');
    } catch (error) {
        console.error('前端启动失败:', error);
    }
}
start()
