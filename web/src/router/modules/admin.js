import AdminLayout from '@/layouts/AdminLayout.vue'

export default {
  path: '/admin',
  component: AdminLayout,
  redirect: '/admin/dashboard',
  meta: { role: ['admin'] },
  children: [
    { path: 'dashboard', component: () => import('@/views/admin/Dashboard.vue'), meta: { title: '管理仪表盘', role: ['admin'] } },
    { path: 'user/list', component: () => import('@/views/admin/user/UserList.vue'), meta: { title: '用户列表', role: ['admin'] } },
    { path: 'merchant/apply', component: () => import('@/views/admin/merchant/MerchantApply.vue'), meta: { title: '入驻审核', role: ['admin'] } },
    { path: 'merchant/list', component: () => import('@/views/admin/merchant/MerchantList.vue'), meta: { title: '商家列表', role: ['admin'] } },
    { path: 'store/list', component: () => import('@/views/admin/store/StoreList.vue'), meta: { title: '门店列表', role: ['admin'] } },
    { path: 'store/edit/:id?', component: () => import('@/views/admin/store/StoreEdit.vue'), meta: { title: '门店编辑', role: ['admin'] } },
    { path: 'order/list', component: () => import('@/views/admin/order/OrderList.vue'), meta: { title: '订单列表', role: ['admin'] } },
    { path: 'order/detail/:id', component: () => import('@/views/admin/order/OrderDetail.vue'), meta: { title: '订单详情', role: ['admin'] } },
    { path: 'refund/list', component: () => import('@/views/admin/finance/RefundList.vue'), meta: { title: '退款审核', role: ['admin'] } },
    { path: 'reschedule/list', component: () => import('@/views/admin/order/RescheduleList.vue'), meta: { title: '改期审核', role: ['admin'] } },
    { path: 'review/list', component: () => import('@/views/admin/review/ReviewList.vue'), meta: { title: '评价管理', role: ['admin'] } },
    { path: 'notice', component: () => import('@/views/admin/content/Notice.vue'), meta: { title: '公告管理', role: ['admin'] } },
    { path: 'system/config', component: () => import('@/views/admin/system/Config.vue'), meta: { title: '系统配置', role: ['admin'] } },
    { path: 'admin/list', component: () => import('@/views/admin/rbac/AdminList.vue'), meta: { title: '管理员管理', role: ['admin'] } },
    { path: 'role/list', component: () => import('@/views/admin/rbac/RoleList.vue'), meta: { title: '角色管理', role: ['admin'] } },
    { path: 'permission/list', component: () => import('@/views/admin/rbac/PermissionList.vue'), meta: { title: '权限列表', role: ['admin'] } }
  ]
}

