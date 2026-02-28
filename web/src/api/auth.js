import * as mock from '@/mock'

export const login = payload => Promise.resolve(mock.login(payload))
export const registerUser = payload => Promise.resolve(mock.registerUser(payload))
export const merchantApply = payload => Promise.resolve(mock.merchantApply(payload))

