import CLayout from '@/layouts/CLayout.vue'

export default {
  path: '/c',
  component: CLayout,
  redirect: '/c/home',
  meta: { role: ['user'] },
  children: [
    { path: 'home', component: () => import('@/views/c/Home.vue'), meta: { title: '用户首页', role: ['user'] } },
    { path: 'store/list', component: () => import('@/views/c/store/StoreList.vue'), meta: { title: '门店列表', role: ['user'] } },
    { path: 'store/:id', component: () => import('@/views/c/store/StoreDetail.vue'), meta: { title: '门店详情', role: ['user'] } },
    { path: 'pet/list', component: () => import('@/views/c/pet/PetList.vue'), meta: { title: '宠物管理', role: ['user'] } },
    { path: 'pet/edit/:id?', component: () => import('@/views/c/pet/PetEdit.vue'), meta: { title: '宠物编辑', role: ['user'] } },
    { path: 'booking/create', component: () => import('@/views/c/booking/BookingCreate.vue'), meta: { title: '创建预约', role: ['user'] } },
    { path: 'booking/confirm', component: () => import('@/views/c/booking/BookingConfirm.vue'), meta: { title: '确认订单', role: ['user'] } },
    { path: 'pay/:orderId', component: () => import('@/views/c/pay/Pay.vue'), meta: { title: '支付订单', role: ['user'] } },
    { path: 'order/list', component: () => import('@/views/c/order/OrderList.vue'), meta: { title: '订单列表', role: ['user'] } },
    { path: 'order/:id', component: () => import('@/views/c/order/OrderDetail.vue'), meta: { title: '订单详情', role: ['user'] } },
    { path: 'boarding/:orderId', component: () => import('@/views/c/boarding/BoardingTimeline.vue'), meta: { title: '寄养动态', role: ['user'] } },
    { path: 'review/:orderId', component: () => import('@/views/c/review/Review.vue'), meta: { title: '订单评价', role: ['user'] } },
    { path: 'me', component: () => import('@/views/c/me/Me.vue'), meta: { title: '个人中心', role: ['user'] } },
    { path: 'me/profile', component: () => import('@/views/c/me/Profile.vue'), meta: { title: '资料设置', role: ['user'] } }
  ]
}

