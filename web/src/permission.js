import router from './router'
import store from './store'
import { roleHomes } from './router/menus'
import { Message } from 'element-ui'

const whiteList = ['/login', '/register', '/register-merchant', '/forgot', '/403', '/404']

router.beforeEach((to, from, next) => {
  const role = store.getters['auth/role']
  const isAuthenticated = !!role

  if (isAuthenticated) {
    if (to.path === '/login') {
      next(roleHomes[role] || '/login')
      return
    }
    if (to.meta && to.meta.role && to.meta.role.length > 0) {
      if (!to.meta.role.includes(role)) {
        next('/403')
        return
      }
    }
    next()
    return
  }

  if (whiteList.includes(to.path)) {
    next()
    return
  }

  Message.warning('请先登录')
  next(`/login?redirect=${to.fullPath}`)
})

