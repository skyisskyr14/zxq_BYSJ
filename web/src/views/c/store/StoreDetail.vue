<template>
  <div>
    <page-header :title="store ? store.name : '门店详情'" desc="查看门店信息与服务" />
    <div class="card mt-16" v-if="store">
      <el-carousel v-if="images.length" height="300px" indicator-position="outside">
        <el-carousel-item v-for="(img, idx) in images" :key="img + idx">
          <img :src="img" alt="cover" class="cover" />
        </el-carousel-item>
      </el-carousel>
      <div v-else class="no-img">暂无门店图片</div>

      <div class="detail mt-16">
        <div class="info">
          <div class="name">{{ store.name }}</div>
          <div class="text-muted">{{ store.city }} · 评分 {{ Number(store.score || 0).toFixed(1) }}</div>
          <div class="text-muted">{{ store.address }}</div>
          <div class="text-muted">电话：{{ store.phone || '未设置' }}</div>
          <div class="desc">{{ store.intro || '暂无门店介绍' }}</div>
          <el-button type="primary" size="mini" @click="goBooking">立即预约</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'
import { getStoreDetailRequest } from '@/api/request/store'

export default {
  name: 'StoreDetail',
  components: { PageHeader },
  data() {
    return { store: null }
  },
  computed: {
    images() {
      return this.store?.images || []
    }
  },
  async created() {
    const id = this.$route.params.id
    try {
      const res = await getStoreDetailRequest(id)
      if (res.code !== 200 || !res.data) throw new Error(res.message || '门店不存在')
      this.store = res.data
    } catch (e) {
      this.$message.error(e.message || '获取门店失败')
      this.$router.replace('/c/store/list')
    }
  },
  methods: {
    goBooking() {
      this.$router.push({ path: '/c/booking/create', query: { storeId: this.store.id } })
    }
  }
}
</script>

<style scoped>
.cover {
  width: 100%;
  height: 300px;
  border-radius: 10px;
  object-fit: cover;
}

.no-img {
  height: 160px;
  border: 1px dashed #dcdfe6;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
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
</style>
