<template>
  <div>
    <page-header title="支付订单" desc="模拟支付流程" />
    <div class="card mt-16" v-if="order">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单号">{{ order.id }}</el-descriptions-item>
        <el-descriptions-item label="金额">￥{{ order.amount }}</el-descriptions-item>
      </el-descriptions>
      <div class="pay-actions mt-16">
        <el-button type="primary" @click="paySuccess">支付成功</el-button>
        <el-button @click="payFail">支付失败</el-button>
      </div>
    </div>
    <empty-state v-else description="订单不存在" />
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import { confirmPay, findById } from '@/mock'

export default {
  name: 'Pay',
  components: { PageHeader, EmptyState },
  data() {
    return { order: null }
  },
  created() {
    const id = this.$route.params.orderId
    this.order = findById('orders', id)
  },
  methods: {
    paySuccess() {
      confirmPay(this.order.id)
      this.$message.success('支付成功')
      this.$router.push(`/c/order/${this.order.id}`)
    },
    payFail() {
      this.$message.error('支付失败，请重试')
    }
  }
}
</script>

<style scoped>
.pay-actions {
  display: flex;
  gap: 12px;
}
</style>

