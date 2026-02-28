import * as mock from '@/mock'

export const listOrders = () => Promise.resolve(mock.list('orders'))
export const getOrder = id => Promise.resolve(mock.findById('orders', id))
export const confirmPay = id => Promise.resolve(mock.confirmPay(id))
export const requestRefund = (id, reason) => Promise.resolve(mock.requestRefund(id, reason))
export const requestReschedule = (id, range, reason) => Promise.resolve(mock.requestReschedule(id, range, reason))

