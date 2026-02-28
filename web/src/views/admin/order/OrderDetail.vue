<template>
  <div>
    <page-header title="订单详情" desc="查看订单关键信息" />
    <div class="card mt-16" v-if="order">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单号">{{ order.id }}</el-descriptions-item>
        <el-descriptions-item label="状态"><status-tag :status="order.status" /></el-descriptions-item>
        <el-descriptions-item label="门店">{{ store.name }}</el-descriptions-item>
        <el-descriptions-item label="金额">￥{{ order.amount }}</el-descriptions-item>
        <el-descriptions-item label="日期">{{ order.dateRange.join(' 至 ') }}</el-descriptions-item>
      </el-descriptions>
    </div>
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import { findById } from '@/mock'

export default {
  name: 'AdminOrderDetail',
  components: { PageHeader, StatusTag },
  data() {
    return { order: null, store: {} }
  },
  created() {
    const id = this.$route.params.id
    this.order = findById('orders', id)
    if (this.order) {
      this.store = findById('stores', this.order.storeId) || {}
    }
  }
}
</script>

