import * as mock from '@/mock'

export const createBooking = payload => Promise.resolve(mock.createBooking(payload))
export const listBookings = () => Promise.resolve(mock.list('bookings'))
export const approveBooking = (id, ok, reason) => Promise.resolve(mock.merchantApproveBooking(id, ok, reason))

