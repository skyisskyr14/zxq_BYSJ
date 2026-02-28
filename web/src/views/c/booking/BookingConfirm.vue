<template>
  <div>
    <page-header title="确认订单" desc="核对费用明细与服务内容" />
    <div class="card mt-16" v-if="order">
      <el-descriptions title="订单信息" :column="2" border>
        <el-descriptions-item label="订单号">{{ order.id }}</el-descriptions-item>
        <el-descriptions-item label="门店">{{ store.name }}</el-descriptions-item>
        <el-descriptions-item label="日期">{{ order.dateRange.join(' 至 ') }}</el-descriptions-item>
        <el-descriptions-item label="入住天数">{{ nights }} 天</el-descriptions-item>
      </el-descriptions>
      <el-divider />
      <el-descriptions title="费用明细" :column="2" border>
        <el-descriptions-item label="房型">{{ room.name }}</el-descriptions-item>
        <el-descriptions-item label="单价">¥{{ room.price }}</el-descriptions-item>
        <el-descriptions-item label="服务">{{ serviceNames }}</el-descriptions-item>
        <el-descriptions-item label="服务费">¥{{ serviceSum }}</el-descriptions-item>
        <el-descriptions-item label="合计">¥{{ order.amount }}</el-descriptions-item>
      </el-descriptions>
      <el-divider />
      <el-checkbox v-model="agree">我已阅读并同意《寄养服务协议》</el-checkbox>
      <div class="mt-16">
        <el-button type="primary" :disabled="!agree" @click="pay">去支付</el-button>
      </div>
    </div>
    <empty-state v-else description="订单不存在" />
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import { findById, list, calcNights } from '@/mock'

export default {
  name: 'BookingConfirm',
  components: { PageHeader, EmptyState },
  data() {
    return { order: null, booking: null, store: {}, room: {}, services: [], agree: false }
  },
  computed: {
    nights() {
      return calcNights(this.order ? this.order.dateRange : [])
    },
    serviceNames() {
      return this.services.map(s => s.name).join('、') || '无'
    },
    serviceSum() {
      return this.services.reduce((sum, s) => sum + s.price, 0)
    }
  },
  created() {
    const orderId = this.$route.query.orderId
    this.order = findById('orders', orderId)
    if (this.order) {
      this.store = findById('stores', this.order.storeId) || {}
      this.booking = findById('bookings', this.order.bookingId) || {}
      this.room = findById('rooms', this.booking.roomId) || {}
      const allServices = list('services')
      this.services = allServices.filter(s => (this.booking.serviceIds || []).includes(s.id))
    }
  },
  methods: {
    pay() {
      this.$router.push(`/c/pay/${this.order.id}`)
    }
  }
}
</script>
