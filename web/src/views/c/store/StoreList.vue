<template>
  <div>
    <page-header title="门店列表" desc="选择心仪门店并快速预约" />
    <query-bar :model="query" class="mt-16">
      <el-form-item label="关键词">
        <el-input v-model="query.keyword" placeholder="门店/地址" />
      </el-form-item>
      <el-form-item label="城市">
        <el-select v-model="query.city" placeholder="全部">
          <el-option label="全部" value="" />
          <el-option v-for="item in cities" :key="item" :label="item" :value="item" />
        </el-select>
      </el-form-item>
      <el-form-item label="评分">
        <el-select v-model="query.rating" placeholder=">=">
          <el-option label="全部" :value="0" />
          <el-option label="4.0" :value="4" />
          <el-option label="4.5" :value="4.5" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </query-bar>

    <div class="card mt-16">
      <div v-if="loading" class="empty-block">
        <el-skeleton :rows="4" animated />
      </div>
      <div v-else class="cards">
        <div v-for="store in paged" :key="store.id" class="store">
          <img :src="store.cover" alt="cover" />
          <div class="info">
            <div class="name">{{ store.name }}</div>
            <div class="text-muted">{{ store.city }} · 评分 {{ store.rating }}</div>
            <div class="text-muted">{{ store.address }}</div>
          </div>
          <div class="actions">
            <el-button size="mini" @click="$router.push(`/c/store/${store.id}`)">详情</el-button>
            <el-button size="mini" type="primary" @click="goBooking(store.id)">去预约</el-button>
          </div>
        </div>
        <empty-state v-if="!paged.length" description="暂无门店" />
      </div>
    </div>

    <pagination :page="page" :page-size="pageSize" :total="filtered.length" @change="page = $event" @size="pageSize = $event" />
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'
import QueryBar from '@/components/common/QueryBar.vue'
import Pagination from '@/components/common/Pagination.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import { list } from '@/mock'

export default {
  name: 'StoreList',
  components: { PageHeader, QueryBar, Pagination, EmptyState },
  data() {
    return {
      loading: false,
      stores: [],
      query: { keyword: '', city: '', rating: 0 },
      page: 1,
      pageSize: 6
    }
  },
  computed: {
    cities() {
      return [...new Set(this.stores.map(s => s.city))]
    },
    filtered() {
      return this.stores.filter(store => {
        const matchKeyword = !this.query.keyword || store.name.includes(this.query.keyword) || store.address.includes(this.query.keyword)
        const matchCity = !this.query.city || store.city === this.query.city
        const matchRating = store.rating >= Number(this.query.rating || 0)
        return matchKeyword && matchCity && matchRating
      })
    },
    paged() {
      const start = (this.page - 1) * this.pageSize
      return this.filtered.slice(start, start + this.pageSize)
    }
  },
  created() {
    this.loading = true
    setTimeout(() => {
      this.stores = list('stores')
      this.loading = false
    }, 300)
  },
  methods: {
    handleSearch() {
      this.page = 1
    },
    reset() {
      this.query = { keyword: '', city: '', rating: 0 }
      this.page = 1
    },
    goBooking(id) {
      this.$router.push({ path: '/c/booking/create', query: { storeId: id } })
    }
  }
}
</script>

<style scoped>
.cards {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.store {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 10px;
  border: 1px solid #eef2f7;
}
.store img {
  width: 140px;
  height: 88px;
  border-radius: 8px;
  object-fit: cover;
}
.info {
  flex: 1;
}
.name {
  font-weight: 600;
  margin-bottom: 4px;
}
.actions {
  display: flex;
  gap: 8px;
}
</style>

