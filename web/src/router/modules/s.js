import SLayout from '@/layouts/SLayout.vue'

export default {
  path: '/s',
  component: SLayout,
  redirect: '/s/dashboard',
  meta: { role: ['merchant'] },
  children: [
    { path: 'dashboard', component: () => import('@/views/s/Dashboard.vue'), meta: { title: '商家仪表盘', role: ['merchant'] } },
    { path: 'booking/list', component: () => import('@/views/s/booking/BookingList.vue'), meta: { title: '预约管理', role: ['merchant'] } },
    { path: 'order/list', component: () => import('@/views/s/order/OrderList.vue'), meta: { title: '订单列表', role: ['merchant'] } },
    { path: 'order/detail/:id', component: () => import('@/views/s/order/OrderDetail.vue'), meta: { title: '订单详情', role: ['merchant'] } },
    { path: 'checkin', component: () => import('@/views/s/flow/Checkin.vue'), meta: { title: '入住办理', role: ['merchant'] } },
    { path: 'checkout', component: () => import('@/views/s/flow/Checkout.vue'), meta: { title: '退房办理', role: ['merchant'] } },
    { path: 'boarding/list', component: () => import('@/views/s/boarding/BoardingList.vue'), meta: { title: '寄养动态', role: ['merchant'] } },
    { path: 'boarding/log/:orderId', component: () => import('@/views/s/boarding/BoardingLog.vue'), meta: { title: '发布动态', role: ['merchant'] } },
    { path: 'store/profile', component: () => import('@/views/s/store/StoreProfile.vue'), meta: { title: '门店资料', role: ['merchant'] } },
    { path: 'cage/list', component: () => import('@/views/s/resource/CageList.vue'), meta: { title: '笼位管理', role: ['merchant'] } },
    { path: 'room/list', component: () => import('@/views/s/resource/RoomList.vue'), meta: { title: '房型管理', role: ['merchant'] } },
    { path: 'service/list', component: () => import('@/views/s/resource/ServiceList.vue'), meta: { title: '服务管理', role: ['merchant'] } }
  ]
}

