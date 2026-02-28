const now = new Date()
const day = 24 * 60 * 60 * 1000

const cities = ['北京', '上海', '广州', '深圳', '杭州', '成都', '南京', '武汉']

const stores = Array.from({ length: 5 }).map((_, i) => ({
  id: i + 1,
  name: `星宠寄养中心-${i + 1}`,
  city: cities[i % cities.length],
  rating: Number((4 + Math.random()).toFixed(1)),
  address: `${cities[i % cities.length]}高新区星宠大道${i + 1}号`,
  phone: `1380000${1000 + i}`,
  cover: `https://picsum.photos/seed/store${i + 1}/640/360`,
  desc: '24小时看护，独立空间，透明化寄养流程。'
}))

const rooms = Array.from({ length: 10 }).map((_, i) => ({
  id: i + 1,
  storeId: stores[i % stores.length].id,
  name: i % 2 === 0 ? '标准房' : '豪华房',
  price: i % 2 === 0 ? 98 : 168,
  capacity: i % 2 === 0 ? 1 : 2
}))

const cages = Array.from({ length: 20 }).map((_, i) => ({
  id: i + 1,
  storeId: stores[i % stores.length].id,
  code: `C-${String(i + 1).padStart(3, '0')}`,
  size: i % 3 === 0 ? 'S' : i % 3 === 1 ? 'M' : 'L',
  price: i % 3 === 0 ? 60 : i % 3 === 1 ? 80 : 120,
  status: i % 5 === 0 ? 'occupied' : 'free'
}))

const services = Array.from({ length: 10 }).map((_, i) => ({
  id: i + 1,
  storeId: stores[i % stores.length].id,
  name: ['洗护', '遛弯', '训练', '健康检查', '专属陪伴'][i % 5],
  price: 20 + (i % 5) * 15,
  desc: '提供专属服务支持。'
}))

const pets = Array.from({ length: 10 }).map((_, i) => ({
  id: i + 1,
  userId: 1,
  name: ['点点', '球球', '旺财', '团子', '橘子'][i % 5],
  type: i % 2 === 0 ? '猫' : '狗',
  age: 1 + (i % 5),
  weight: 2 + (i % 6),
  gender: i % 2 === 0 ? '公' : '母',
  isDefault: i === 0
}))

const users = [
  { id: 1, role: 'user', username: 'user1', password: '123456', name: '示例用户', phone: '13800001111', avatar: '' },
  { id: 2, role: 'merchant', username: 'merchant1', password: '123456', name: '示例商家', phone: '13800002222', avatar: '' },
  { id: 3, role: 'admin', username: 'admin', password: '123456', name: '超级管理员', phone: '13800003333', avatar: '' }
]

const merchants = [
  { id: 1, userId: 2, storeId: 1, name: '示例商家', status: 'active' }
]

const merchantApplies = Array.from({ length: 10 }).map((_, i) => ({
  id: i + 1,
  owner: `申请人${i + 1}`,
  phone: `1390000${1000 + i}`,
  storeName: `入驻门店${i + 1}`,
  city: cities[i % cities.length],
  status: i % 3 === 0 ? 'approved' : 'pending',
  createdAt: new Date(now.getTime() - day * (i + 1)).toISOString().slice(0, 10)
}))

const bookings = Array.from({ length: 10 }).map((_, i) => ({
  id: i + 1,
  userId: 1,
  storeId: stores[i % stores.length].id,
  petId: pets[i % pets.length].id,
  dateRange: [
    new Date(now.getTime() + day * (i + 1)).toISOString().slice(0, 10),
    new Date(now.getTime() + day * (i + 3)).toISOString().slice(0, 10)
  ],
  roomId: rooms[i % rooms.length].id,
  serviceIds: [services[i % services.length].id],
  status: i % 3 === 0 ? 'approved' : i % 3 === 1 ? 'pending' : 'rejected',
  remark: '需要专属看护'
}))

const orders = Array.from({ length: 10 }).map((_, i) => ({
  id: i + 1001,
  bookingId: bookings[i % bookings.length].id,
  userId: 1,
  storeId: stores[i % stores.length].id,
  status: ['pending_confirm', 'pending_checkin', 'boarding', 'completed'][i % 4],
  payStatus: 'paid',
  amount: 300 + i * 15,
  dateRange: bookings[i % bookings.length].dateRange,
  createdAt: new Date(now.getTime() - day * (i + 1)).toISOString().slice(0, 19).replace('T', ' '),
  updatedAt: new Date(now.getTime() - day * i).toISOString().slice(0, 19).replace('T', ' '),
  cageId: i % 4 === 2 ? cages[i % cages.length].id : null
}))

const boardingLogs = Array.from({ length: 10 }).map((_, i) => ({
  id: i + 1,
  orderId: orders[i % orders.length].id,
  type: i % 2 === 0 ? '喂食' : '遛弯',
  content: '宠物状态良好，已完成护理。',
  images: [`https://picsum.photos/seed/log${i + 1}/320/200`],
  createdAt: new Date(now.getTime() - day * (i + 1)).toISOString().slice(0, 19).replace('T', ' ')
}))

const refunds = Array.from({ length: 10 }).map((_, i) => ({
  id: i + 1,
  orderId: orders[i % orders.length].id,
  reason: '临时行程调整',
  status: i % 3 === 0 ? 'approved' : i % 3 === 1 ? 'pending' : 'rejected',
  merchantOpinion: i % 2 === 0 ? '同意退款' : '',
  adminOpinion: i % 3 === 0 ? '审核通过' : '',
  createdAt: new Date(now.getTime() - day * (i + 2)).toISOString().slice(0, 10)
}))

const reschedules = Array.from({ length: 10 }).map((_, i) => ({
  id: i + 1,
  orderId: orders[i % orders.length].id,
  reason: '计划调整',
  newRange: [
    new Date(now.getTime() + day * (i + 4)).toISOString().slice(0, 10),
    new Date(now.getTime() + day * (i + 6)).toISOString().slice(0, 10)
  ],
  status: i % 3 === 0 ? 'approved' : i % 3 === 1 ? 'pending' : 'rejected',
  merchantOpinion: i % 2 === 0 ? '可调整' : '',
  adminOpinion: i % 3 === 0 ? '同意改期' : '',
  createdAt: new Date(now.getTime() - day * (i + 1)).toISOString().slice(0, 10)
}))

const reviews = Array.from({ length: 10 }).map((_, i) => ({
  id: i + 1,
  orderId: orders[i % orders.length].id,
  score: 4 + (i % 2),
  content: '环境干净，服务细致。',
  createdAt: new Date(now.getTime() - day * (i + 1)).toISOString().slice(0, 10)
}))

const notices = Array.from({ length: 10 }).map((_, i) => ({
  id: i + 1,
  title: `系统公告 ${i + 1}`,
  content: '节假日营业时间调整通知。',
  status: i % 2 === 0 ? 'published' : 'draft',
  createdAt: new Date(now.getTime() - day * (i + 1)).toISOString().slice(0, 10)
}))

const configs = Array.from({ length: 10 }).map((_, i) => ({
  id: i + 1,
  key: `config_${i + 1}`,
  value: `value_${i + 1}`,
  remark: '系统配置项'
}))

const adminUsers = Array.from({ length: 10 }).map((_, i) => ({
  id: i + 1,
  name: `管理员${i + 1}`,
  role: i % 2 === 0 ? '超级管理员' : '运营',
  status: i % 3 === 0 ? 'disabled' : 'active'
}))

const roles = Array.from({ length: 10 }).map((_, i) => ({
  id: i + 1,
  name: `角色${i + 1}`,
  desc: '平台角色权限'
}))

const permissions = Array.from({ length: 10 }).map((_, i) => ({
  id: i + 1,
  name: `权限${i + 1}`,
  code: `perm_${i + 1}`,
  type: i % 2 === 0 ? '菜单' : '按钮'
}))

export default {
  stores,
  rooms,
  cages,
  services,
  pets,
  users,
  merchants,
  merchantApplies,
  bookings,
  orders,
  boardingLogs,
  refunds,
  reschedules,
  reviews,
  notices,
  configs,
  adminUsers,
  roles,
  permissions
}

