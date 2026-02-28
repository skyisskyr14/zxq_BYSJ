import db from './db'

const clone = data => JSON.parse(JSON.stringify(data))

const getTable = name => {
  if (!db[name]) {
    throw new Error(`Unknown table: ${name}`)
  }
  return db[name]
}

const nextId = table => {
  const max = table.reduce((acc, item) => Math.max(acc, Number(item.id) || 0), 0)
  return max + 1
}

export const list = (name) => {
  return clone(getTable(name))
}

export const findById = (name, id) => {
  return clone(getTable(name).find(item => String(item.id) === String(id)))
}

export const create = (name, payload) => {
  const table = getTable(name)
  const item = { id: nextId(table), ...payload }
  table.unshift(item)
  return clone(item)
}

export const update = (name, id, payload) => {
  const table = getTable(name)
  const index = table.findIndex(item => String(item.id) === String(id))
  if (index === -1) return null
  table[index] = { ...table[index], ...payload }
  return clone(table[index])
}

export const remove = (name, id) => {
  const table = getTable(name)
  const index = table.findIndex(item => String(item.id) === String(id))
  if (index === -1) return false
  table.splice(index, 1)
  return true
}

export const login = ({ username, password, role }) => {
  const user = db.users.find(u => u.username === username && u.password === password && u.role === role)
  if (!user) {
    throw new Error('账号或密码错误')
  }
  return clone(user)
}

export const registerUser = payload => {
  return create('users', { role: 'user', ...payload })
}

export const merchantApply = payload => {
  return create('merchantApplies', { status: 'pending', createdAt: new Date().toISOString().slice(0, 10), ...payload })
}

export const adminAuditMerchantApply = (applyId, pass, opinion = '') => {
  const apply = update('merchantApplies', applyId, { status: pass ? 'approved' : 'rejected', adminOpinion: opinion })
  if (pass && apply) {
    const store = create('stores', {
      name: apply.storeName,
      city: apply.city,
      rating: 4.5,
      address: `${apply.city}新门店地址`,
      phone: apply.phone,
      cover: `https://picsum.photos/seed/store${apply.id}/640/360`,
      desc: '新入驻门店'
    })
    const user = create('users', {
      role: 'merchant',
      username: `merchant${apply.id}`,
      password: '123456',
      name: apply.owner,
      phone: apply.phone
    })
    create('merchants', { userId: user.id, storeId: store.id, name: apply.storeName, status: 'active' })
  }
  return apply
}

export const createBooking = payload => {
  const booking = create('bookings', { status: 'pending', remark: payload.remark || '', ...payload })
  const nights = calcNights(payload.dateRange)
  const room = db.rooms.find(r => r.id === payload.roomId)
  const serviceSum = (payload.serviceIds || []).reduce((sum, id) => {
    const srv = db.services.find(s => s.id === id)
    return sum + (srv ? srv.price : 0)
  }, 0)
  const amount = nights * (room ? room.price : 0) + serviceSum
  const order = create('orders', {
    bookingId: booking.id,
    userId: payload.userId,
    storeId: payload.storeId,
    status: 'draft',
    payStatus: 'unpaid',
    amount,
    dateRange: payload.dateRange,
    createdAt: new Date().toISOString().slice(0, 19).replace('T', ' '),
    updatedAt: new Date().toISOString().slice(0, 19).replace('T', ' ')
  })
  return { booking, order }
}

export const confirmPay = orderId => {
  return update('orders', orderId, { status: 'pending_confirm', payStatus: 'paid', updatedAt: new Date().toISOString().slice(0, 19).replace('T', ' ') })
}

export const merchantApproveBooking = (bookingId, ok, reason = '') => {
  const booking = update('bookings', bookingId, { status: ok ? 'approved' : 'rejected', merchantRemark: reason })
  const order = db.orders.find(o => String(o.bookingId) === String(bookingId))
  if (order) {
    update('orders', order.id, { status: ok ? 'pending_checkin' : 'rejected', updatedAt: new Date().toISOString().slice(0, 19).replace('T', ' ') })
  }
  return booking
}

export const checkin = (orderId, cageId) => {
  update('orders', orderId, { status: 'boarding', cageId, updatedAt: new Date().toISOString().slice(0, 19).replace('T', ' ') })
  update('cages', cageId, { status: 'occupied' })
}

export const addBoardingLog = (orderId, log) => {
  return create('boardingLogs', { orderId, createdAt: new Date().toISOString().slice(0, 19).replace('T', ' '), ...log })
}

export const checkout = orderId => {
  const order = db.orders.find(o => String(o.id) === String(orderId))
  if (order && order.cageId) {
    update('cages', order.cageId, { status: 'free' })
  }
  return update('orders', orderId, { status: 'completed', updatedAt: new Date().toISOString().slice(0, 19).replace('T', ' ') })
}

export const requestRefund = (orderId, reason) => {
  const order = db.orders.find(o => String(o.id) === String(orderId))
  if (!order) return null
  update('orders', orderId, { status: 'refund_pending', refundPrevStatus: order.status })
  return create('refunds', {
    orderId,
    reason,
    status: 'pending',
    merchantOpinion: '',
    adminOpinion: '',
    createdAt: new Date().toISOString().slice(0, 10)
  })
}

export const adminAuditRefund = (refundId, pass, opinion = '') => {
  const refund = update('refunds', refundId, { status: pass ? 'approved' : 'rejected', adminOpinion: opinion })
  if (refund) {
    const order = db.orders.find(o => String(o.id) === String(refund.orderId))
    if (order) {
      update('orders', order.id, { status: pass ? 'refunded' : (order.refundPrevStatus || 'pending_checkin') })
    }
  }
  return refund
}

export const requestReschedule = (orderId, newRange, reason) => {
  const order = db.orders.find(o => String(o.id) === String(orderId))
  if (!order) return null
  update('orders', orderId, { status: 'reschedule_pending', reschedulePrevStatus: order.status })
  return create('reschedules', {
    orderId,
    reason,
    newRange,
    status: 'pending',
    merchantOpinion: '',
    adminOpinion: '',
    createdAt: new Date().toISOString().slice(0, 10)
  })
}

export const adminAuditReschedule = (rescheduleId, pass, opinion = '') => {
  const reschedule = update('reschedules', rescheduleId, { status: pass ? 'approved' : 'rejected', adminOpinion: opinion })
  if (reschedule) {
    const order = db.orders.find(o => String(o.id) === String(reschedule.orderId))
    if (order) {
      update('orders', order.id, {
        status: pass ? 'pending_checkin' : (order.reschedulePrevStatus || 'pending_checkin'),
        dateRange: pass ? reschedule.newRange : order.dateRange
      })
    }
  }
  return reschedule
}

export const calcNights = range => {
  if (!range || range.length !== 2) return 0
  const start = new Date(range[0]).getTime()
  const end = new Date(range[1]).getTime()
  const nights = Math.max(1, Math.round((end - start) / (24 * 60 * 60 * 1000)))
  return nights
}

export const getStoreById = id => findById('stores', id)
export const getOrderById = id => findById('orders', id)
export const getBookingById = id => findById('bookings', id)
export const getUserByRole = role => clone(db.users.find(u => u.role === role))

export default {
  list,
  findById,
  create,
  update,
  remove,
  login,
  registerUser,
  merchantApply,
  adminAuditMerchantApply,
  createBooking,
  confirmPay,
  merchantApproveBooking,
  checkin,
  addBoardingLog,
  checkout,
  requestRefund,
  adminAuditRefund,
  requestReschedule,
  adminAuditReschedule,
  calcNights,
  getStoreById,
  getOrderById,
  getBookingById,
  getUserByRole
}

