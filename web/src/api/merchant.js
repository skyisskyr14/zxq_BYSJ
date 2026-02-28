import * as mock from '@/mock'

export const listMerchants = () => Promise.resolve(mock.list('merchants'))
export const listMerchantApplies = () => Promise.resolve(mock.list('merchantApplies'))
export const auditMerchantApply = (id, pass, opinion) => Promise.resolve(mock.adminAuditMerchantApply(id, pass, opinion))

