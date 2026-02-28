import * as mock from '@/mock'

export const listReschedules = () => Promise.resolve(mock.list('reschedules'))
export const auditReschedule = (id, pass, opinion) => Promise.resolve(mock.adminAuditReschedule(id, pass, opinion))

