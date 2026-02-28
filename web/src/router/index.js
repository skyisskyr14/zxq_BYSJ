import Vue from 'vue'
import VueRouter from 'vue-router'
import cRoutes from './modules/c'
import sRoutes from './modules/s'
import adminRoutes from './modules/admin'

Vue.use(VueRouter)

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: () => import('@/views/common/Login.vue'), meta: { title: '登录' } },
  { path: '/register', component: () => import('@/views/common/RegisterUser.vue'), meta: { title: '用户注册' } },
  { path: '/register-merchant', component: () => import('@/views/common/RegisterMerchant.vue'), meta: { title: '商家入驻' } },
  { path: '/forgot', component: () => import('@/views/common/Forgot.vue'), meta: { title: '找回密码' } },
  { path: '/403', component: () => import('@/views/common/403.vue'), meta: { title: '无权限' } },
  cRoutes,
  sRoutes,
  adminRoutes,
  { path: '*', component: () => import('@/views/common/404.vue'), meta: { title: '未找到' } }
]

const router = new VueRouter({
  routes,
  scrollBehavior() {
    return { x: 0, y: 0 }
  }
})

export default router

