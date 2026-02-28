import * as mock from '@/mock'

export const fetchStores = () => Promise.resolve(mock.list('stores'))
export const fetchStore = id => Promise.resolve(mock.findById('stores', id))
export const updateStore = (id, payload) => Promise.resolve(mock.update('stores', id, payload))

