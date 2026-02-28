export const roleHomes = {
  user: '/c/home',
  merchant: '/s/dashboard',
  admin: '/admin/dashboard'
}

export const menus = {
  user: [
    { title: '首页', icon: 'el-icon-house', path: '/c/home' },
    { title: '门店', icon: 'el-icon-shop', path: '/c/store/list' },
    { title: '预约', icon: 'el-icon-date', path: '/c/booking/create' },
    { title: '订单', icon: 'el-icon-tickets', path: '/c/order/list' },
    { title: '寄养动态', icon: 'el-icon-bell', path: '/c/boarding/1001' },
    { title: '宠物管理', icon: 'el-icon-guide', path: '/c/pet/list' },
    { title: '个人中心', icon: 'el-icon-user', path: '/c/me' }
  ],
  merchant: [
    { title: '仪表盘', icon: 'el-icon-s-data', path: '/s/dashboard' },
    { title: '预约管理', icon: 'el-icon-date', path: '/s/booking/list' },
    { title: '订单管理', icon: 'el-icon-tickets', path: '/s/order/list' },
    { title: '入住办理', icon: 'el-icon-s-check', path: '/s/checkin' },
    { title: '寄养动态', icon: 'el-icon-camera', path: '/s/boarding/list' },
    { title: '退房处理', icon: 'el-icon-s-release', path: '/s/checkout' },
    { title: '门店配置', icon: 'el-icon-office-building', path: '/s/store/profile' },
    { title: '笼位管理', icon: 'el-icon-menu', path: '/s/cage/list' },
    { title: '房型管理', icon: 'el-icon-s-home', path: '/s/room/list' },
    { title: '服务管理', icon: 'el-icon-star-on', path: '/s/service/list' }
  ],
  admin: [
    { title: '仪表盘', icon: 'el-icon-data-analysis', path: '/admin/dashboard' },
    { title: '用户管理', icon: 'el-icon-user', path: '/admin/user/list' },
    { title: '入驻审核', icon: 'el-icon-s-check', path: '/admin/merchant/apply' },
    { title: '商家列表', icon: 'el-icon-s-shop', path: '/admin/merchant/list' },
    { title: '门店管理', icon: 'el-icon-office-building', path: '/admin/store/list' },
    { title: '订单管理', icon: 'el-icon-tickets', path: '/admin/order/list' },
    { title: '退款审核', icon: 'el-icon-s-finance', path: '/admin/refund/list' },
    { title: '改期审核', icon: 'el-icon-refresh', path: '/admin/reschedule/list' },
    { title: '评价管理', icon: 'el-icon-chat-dot-square', path: '/admin/review/list' },
    { title: '公告管理', icon: 'el-icon-notebook-1', path: '/admin/notice' },
    { title: '系统配置', icon: 'el-icon-setting', path: '/admin/system/config' },
    { title: '管理员', icon: 'el-icon-s-custom', path: '/admin/admin/list' },
    { title: '角色权限', icon: 'el-icon-lock', path: '/admin/role/list' },
    { title: '权限列表', icon: 'el-icon-key', path: '/admin/permission/list' }
  ]
}

export const getMenusByRole = role => menus[role] || []

