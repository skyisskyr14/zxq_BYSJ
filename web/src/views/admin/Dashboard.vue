<template>
  <div>
    <page-header title="管理仪表盘" desc="平台整体数据概览" />
    <div class="grid mt-16">
      <el-card class="stat">
        <div class="label">商家入驻待审</div>
        <div class="value">{{ pendingApply }}</div>
      </el-card>
      <el-card class="stat">
        <div class="label">退款待审</div>
        <div class="value">{{ pendingRefund }}</div>
      </el-card>
      <el-card class="stat">
        <div class="label">改期待审</div>
        <div class="value">{{ pendingReschedule }}</div>
      </el-card>
      <el-card class="stat">
        <div class="label">订单总数</div>
        <div class="value">{{ orders.length }}</div>
      </el-card>
    </div>
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'
import { list } from '@/mock'

export default {
  name: 'AdminDashboard',
  components: { PageHeader },
  data() {
    return { applies: [], refunds: [], reschedules: [], orders: [] }
  },
  computed: {
    pendingApply() {
      return this.applies.filter(a => a.status === 'pending').length
    },
    pendingRefund() {
      return this.refunds.filter(r => r.status === 'pending').length
    },
    pendingReschedule() {
      return this.reschedules.filter(r => r.status === 'pending').length
    }
  },
  created() {
    this.applies = list('merchantApplies')
    this.refunds = list('refunds')
    this.reschedules = list('reschedules')
    this.orders = list('orders')
  }
}
</script>

<style scoped>
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}
.stat {
  text-align: center;
}
.label {
  color: #8c98a4;
  font-size: 12px;
}
.value {
  font-size: 24px;
  font-weight: 600;
  margin-top: 6px;
}
</style>

