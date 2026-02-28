<template>
  <div>
    <page-header title="用户概览" desc="快速查看寄养进度与常用入口" />
    <div class="grid mt-16">
      <el-card class="stat">
        <div class="label">门店数量</div>
        <div class="value">{{ stores.length }}</div>
      </el-card>
      <el-card class="stat">
        <div class="label">我的宠物</div>
        <div class="value">{{ pets.length }}</div>
      </el-card>
      <el-card class="stat">
        <div class="label">订单总数</div>
        <div class="value">{{ orders.length }}</div>
      </el-card>
      <el-card class="stat">
        <div class="label">寄养中</div>
        <div class="value">{{ boardingCount }}</div>
      </el-card>
    </div>
    <div class="card mt-16">
      <div class="section-title">推荐门店</div>
      <div class="store-list">
        <div v-for="store in stores.slice(0, 3)" :key="store.id" class="store-card">
          <img :src="store.cover" alt="cover" />
          <div class="info">
            <div class="name">{{ store.name }}</div>
            <div class="text-muted">{{ store.city }} · 评分 {{ store.rating }}</div>
          </div>
          <div>
            <el-button size="mini" type="primary" @click="$router.push(`/c/store/${store.id}`)">详情</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'
import { list } from '@/mock'

export default {
  name: 'CHome',
  components: { PageHeader },
  data() {
    return { stores: [], pets: [], orders: [] }
  },
  computed: {
    boardingCount() {
      return this.orders.filter(o => o.status === 'boarding').length
    }
  },
  created() {
    this.stores = list('stores')
    this.pets = list('pets')
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
.store-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.store-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  border-radius: 10px;
  border: 1px solid #eef2f7;
}
.store-card img {
  width: 120px;
  height: 72px;
  border-radius: 8px;
  object-fit: cover;
}
.info {
  flex: 1;
}
.name {
  font-weight: 600;
}
</style>

