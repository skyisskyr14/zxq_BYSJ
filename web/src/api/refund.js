import * as mock from '@/mock'

export const listRefunds = () => Promise.resolve(mock.list('refunds'))
export const auditRefund = (id, pass, opinion) => Promise.resolve(mock.adminAuditRefund(id, pass, opinion))

