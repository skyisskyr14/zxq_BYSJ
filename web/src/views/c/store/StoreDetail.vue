<template>
  <div>
    <page-header :title="store ? store.name : '门店详情'" desc="查看门店信息与服务" />
    <div class="card mt-16" v-if="store">
      <div class="detail">
        <img :src="store.cover" alt="cover" />
        <div class="info">
          <div class="name">{{ store.name }}</div>
          <div class="text-muted">{{ store.city }} · 评分 {{ store.rating }}</div>
          <div class="text-muted">{{ store.address }}</div>
          <div class="text-muted">电话：{{ store.phone }}</div>
          <div class="desc">{{ store.desc }}</div>
          <el-button type="primary" size="mini" @click="goBooking">立即预约</el-button>
        </div>
      </div>
    </div>
    <div class="grid mt-16">
      <el-card>
        <div class="section-title">房型</div>
        <el-table :data="rooms" size="small">
          <el-table-column prop="name" label="房型" />
          <el-table-column prop="price" label="价格" />
          <el-table-column prop="capacity" label="容量" />
        </el-table>
      </el-card>
      <el-card>
        <div class="section-title">服务</div>
        <el-table :data="services" size="small">
          <el-table-column prop="name" label="服务" />
          <el-table-column prop="price" label="价格" />
          <el-table-column prop="desc" label="说明" />
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'
import { list, findById } from '@/mock'

export default {
  name: 'StoreDetail',
  components: { PageHeader },
  data() {
    return { store: null, rooms: [], services: [] }
  },
  created() {
    const id = this.$route.params.id
    this.store = findById('stores', id)
    this.rooms = list('rooms').filter(r => String(r.storeId) === String(id))
    this.services = list('services').filter(s => String(s.storeId) === String(id))
  },
  methods: {
    goBooking() {
      this.$router.push({ path: '/c/booking/create', query: { storeId: this.store.id } })
    }
  }
}
</script>

<style scoped>
.detail {
  display: flex;
  gap: 16px;
}
.detail img {
  width: 260px;
  height: 160px;
  border-radius: 10px;
  object-fit: cover;
}
.info {
  flex: 1;
}
.name {
  font-size: 20px;
  font-weight: 600;
}
.desc {
  margin: 8px 0 12px;
}
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 12px;
}
</style>

