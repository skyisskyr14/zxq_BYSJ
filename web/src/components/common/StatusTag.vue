<template>
  <el-tag :type="tagType" size="mini" class="status-tag">{{ label }}</el-tag>
</template>

<script>
const STATUS_MAP = {
  draft: { label: '草稿', type: 'info' },
  pending_confirm: { label: '待确认', type: 'warning' },
  pending_checkin: { label: '待入住', type: 'warning' },
  boarding: { label: '寄养中', type: 'success' },
  completed: { label: '已完成', type: 'success' },
  refund_pending: { label: '退款中', type: 'warning' },
  refunded: { label: '已退款', type: 'info' },
  reschedule_pending: { label: '改期待审', type: 'warning' },
  cancelled: { label: '已取消', type: 'info' },
  rejected: { label: '已拒绝', type: 'danger' },
  pending: { label: '待处理', type: 'warning' },
  approved: { label: '已通过', type: 'success' },
  published: { label: '已发布', type: 'success' },
  active: { label: '启用', type: 'success' },
  disabled: { label: '停用', type: 'info' }
}

export default {
  name: 'StatusTag',
  props: {
    status: { type: String, default: '' }
  },
  computed: {
    tagType() {
      return (STATUS_MAP[this.status] || { type: 'info' }).type
    },
    label() {
      return (STATUS_MAP[this.status] || { label: this.status || '-' }).label
    }
  }
}
</script>

<style scoped>
.status-tag {
  font-weight: 600;
}
</style>

