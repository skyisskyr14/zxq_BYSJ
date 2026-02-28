<template>
  <div>
    <page-header title="商家仪表盘" desc="门店经营数据概览" />
    <div class="card mt-16 merchant-card">
      <el-avatar :size="56" :src="merchant.avatar" icon="el-icon-user-solid" />
      <div>
        <div class="name">{{ merchant.realname || merchant.name || '未设置商家名称' }}</div>
        <div class="text-muted">手机号：{{ merchant.phone || '未绑定' }}</div>
      </div>
    </div>
    <div class="grid mt-16">
      <el-card class="stat">
        <div class="label">待处理预约</div>
        <div class="value">{{ pendingBookings }}</div>
      </el-card>
      <el-card class="stat">
        <div class="label">待入住订单</div>
        <div class="value">{{ pendingCheckin }}</div>
      </el-card>
      <el-card class="stat">
        <div class="label">寄养中</div>
        <div class="value">{{ boardingCount }}</div>
      </el-card>
      <el-card class="stat">
        <div class="label">退款申请</div>
        <div class="value">{{ refundCount }}</div>
      </el-card>
    </div>
    <div class="card mt-16">
      <div class="section-title">快捷入口</div>
      <div class="quick">
        <el-button @click="$router.push('/s/booking/list')">预约管理</el-button>
        <el-button @click="$router.push('/s/checkin')">入住办理</el-button>
        <el-button @click="$router.push('/s/boarding/list')">寄养动态</el-button>
        <el-button @click="$router.push('/s/me/profile')">商家信息</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'
import { list } from '@/mock'

export default {
  name: 'SDashboard',
  components: { PageHeader },
  data() {
    return { bookings: [], orders: [], refunds: [] }
  },
  computed: {
    merchant() {
      return this.$store.getters['auth/userInfo'] || {}
    },
    pendingBookings() {
      return this.bookings.filter(b => b.status === 'pending').length
    },
    pendingCheckin() {
      return this.orders.filter(o => o.status === 'pending_checkin').length
    },
    boardingCount() {
      return this.orders.filter(o => o.status === 'boarding').length
    },
    refundCount() {
      return this.refunds.filter(r => r.status === 'pending').length
    }
  },
  created() {
    this.bookings = list('bookings')
    this.orders = list('orders')
    this.refunds = list('refunds')
    this.$store.dispatch('auth/fetchShopBaseInfo').catch(() => {})
  }
}
</script>

<style scoped>
.merchant-card {
  display: flex;
  align-items: center;
  gap: 12px;
}

.name {
  font-size: 16px;
  font-weight: 600;
}

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
.quick {
  display: flex;
  gap: 12px;
}
</style>
