import { createRouter, createWebHashHistory } from 'vue-router'
// import HelloWorld from "./components/HelloWorld.vue"
import Test from "./components/Test.vue"
import Menu from "./components/Menu.vue"
const routes = [
    // 添加您的路由
    {
        path:'/',
        component:Menu
    },
    {
        path:'/test',
        component:Test
    }
]

export const router = createRouter({
    history: createWebHashHistory(),
    routes,
})
