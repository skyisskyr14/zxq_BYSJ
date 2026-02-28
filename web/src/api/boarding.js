import * as mock from '@/mock'

export const listBoardingLogs = () => Promise.resolve(mock.list('boardingLogs'))
export const addBoardingLog = (orderId, payload) => Promise.resolve(mock.addBoardingLog(orderId, payload))

