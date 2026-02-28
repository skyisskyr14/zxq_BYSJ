const state = {
  orderStatus: {
    draft: '草稿',
    pending_confirm: '待确认',
    pending_checkin: '待入住',
    boarding: '寄养中',
    completed: '已完成',
    refund_pending: '退款中',
    refunded: '已退款',
    reschedule_pending: '改期待审',
    cancelled: '已取消',
    rejected: '已拒绝'
  }
}

export default {
  namespaced: true,
  state
}

