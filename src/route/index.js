import Vue from 'vue'
import VueRouter from 'vue-router'

// 引入组件
import indexPage from "@/pages/index.vue"
import login from "@/pages/login.vue";
import login2 from "@/pages/login2.vue";
import MainPage from "@/pages/mainpage.vue";
import UserInfo from "@/pages/info.vue"
import Messages from "@/pages/message.vue"
import blogEdit from "@/pages/blogEdit.vue";
import blogDetail from "@/pages/blogDetail.vue";
import shopList from "@/pages/shopList.vue";
import userProfile from "@/pages/otherInfo.vue"
import UserProfileEdit from "@/pages/infoEdit.vue"
import shopDetail from "@/pages/shopDetail.vue";
import voucherPAge from "@/components/myJsBarcode.vue"
// 使用vue-router插件
Vue.use(VueRouter)

// 定义路由规则
const routes = [
    {
        path: '/',
        redirect: '/index',
    },
    {
        path: '/login',
        redirect: '/login1',
    },
    {
        path: '/login1',          // 路径
        name: 'login1',       // 路由名称
        component: login     // 对应的组件
    },
    {
        path: '/login2',
        name: 'login2',
        component: login2
    },
    {
        path: '/index',  // 动态路由
        component: MainPage,
        children: [
            {
                path: '',
                component: indexPage,
                name: 'index',
                meta: {
                    title: '主页',
                    requiresFootBar: true,
                    footBarIndex: 1
                }
            }
            ,{
                path: '/index/map',
                name: 'Map',
                component: shopList,
                meta: {
                    title: '地图',
                    requiresFootBar: true,
                    footBarIndex: 2
                }
            },
            {
                path: '/index/messages',
                name: 'Messages',
                component: Messages,
                meta: {
                    title: '消息',
                    requiresFootBar: true,
                    footBarIndex: 3
                }
            },
            {
                path: '/index/info',
                name: 'UserInfo',
                component: UserInfo,
                meta: {
                    title: '我的信息',
                    requiresFootBar: true,
                    footBarIndex: 4
                }
            },{
            path: '/index/user-profile',
                name: 'UserProfile',
                component: userProfile

            },{
            path:"/index/UserProfileEdit",
                name: 'UserProfileEdit',
                component: UserProfileEdit
            }
        ]
    },
    {
        path: '/blog-edit',
        component: blogEdit,
        name: 'blogEdit',
        meta: {
            title: '编辑文章',
            requiresFootBar: true,
            footBarIndex: 0
        }
    },{
        path: "/blog-detail/:id",
        name: 'blogDetail',
        component:blogDetail,
        props: true // 可选：将 params 自动转为组件 props
    },
    {
        path: "/shop-list",
        name: 'shopList',
        component:shopList,
        // props: true // 可选：将 params 自动转为组件 props
    },
    {
      path: '/shop-detail/:id',
      name: 'shopDetail',
        component:shopDetail,
    },
    {
        path: '/voucher',
        name: 'voucher',
        component: voucherPAge
    },
    {
        path: '*',          // 404页面，匹配所有路径
        component: Messages
    }
]

// 创建路由实例
const router = new VueRouter({
    mode: 'history',      // 使用HTML5 History模式，去掉URL中的#号
    base: process.env.BASE_URL,
    routes,               // 路由规则
    scrollBehavior(to, from, savedPosition) {
        // 滚动行为控制
        if (savedPosition) {
            return savedPosition
        } else {
            return { x: 0, y: 0 }
        }
    }
})

export default router