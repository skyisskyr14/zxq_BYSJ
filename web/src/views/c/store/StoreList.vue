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
          <img :src="storeFirstImage(store)" alt="cover" />
          <div class="info">
            <div class="name">{{ store.name }}</div>
            <div class="text-muted">{{ store.city || '未设置城市' }} · 评分 {{ Number(store.score || 0).toFixed(1) }}</div>
            <div class="text-muted">{{ store.address || '未设置地址' }}</div>
          </div>
          <div class="actions">
            <el-button size="mini" @click="openDetail(store.id)">详情</el-button>
            <el-button size="mini" type="primary" @click="goBooking(store.id)">去预约</el-button>
          </div>
        </div>
        <empty-state v-if="!paged.length" description="暂无门店" />
      </div>
    </div>

    <pagination :page="page" :page-size="pageSize" :total="filtered.length" @change="page = $event" @size="pageSize = $event" />

    <el-dialog :visible.sync="detailVisible" width="760px" title="门店详情" destroy-on-close>
      <template v-if="detailStore">
        <el-carousel v-if="detailImages.length" height="280px" indicator-position="outside">
          <el-carousel-item v-for="(img, idx) in detailImages" :key="img + idx">
            <img :src="img" class="detail-img" alt="门店图片" />
          </el-carousel-item>
        </el-carousel>
        <div v-else class="no-img">暂无门店图片</div>

        <div class="detail-info mt-16">
          <div class="name">{{ detailStore.name }}</div>
          <div class="text-muted">{{ detailStore.city || '未设置城市' }} · 评分 {{ Number(detailStore.score || 0).toFixed(1) }}</div>
          <div class="text-muted">地址：{{ detailStore.address || '未设置地址' }}</div>
          <div class="text-muted">电话：{{ detailStore.phone || '未设置电话' }}</div>
          <div class="desc">{{ detailStore.intro || '暂无门店介绍' }}</div>
        </div>
      </template>
      <span slot="footer" class="dialog-footer">
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="primary" :disabled="!detailStore" @click="goBooking(detailStore.id)">去预约</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'
import QueryBar from '@/components/common/QueryBar.vue'
import Pagination from '@/components/common/Pagination.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import { getStoreDetailRequest, getStoreListRequest } from '@/api/request/store'

export default {
  name: 'StoreList',
  components: { PageHeader, QueryBar, Pagination, EmptyState },
  data() {
    return {
      loading: false,
      detailLoading: false,
      detailVisible: false,
      stores: [],
      detailStore: null,
      query: { keyword: '', city: '', rating: 0 },
      page: 1,
      pageSize: 6
    }
  },
  computed: {
    cities() {
      return [...new Set(this.stores.map(s => s.city).filter(Boolean))]
    },
    filtered() {
      return this.stores.filter(store => {
        const matchKeyword =
          !this.query.keyword ||
          (store.name || '').includes(this.query.keyword) ||
          (store.address || '').includes(this.query.keyword)
        const matchCity = !this.query.city || store.city === this.query.city
        const matchRating = Number(store.score || 0) >= Number(this.query.rating || 0)
        return matchKeyword && matchCity && matchRating
      })
    },
    paged() {
      const start = (this.page - 1) * this.pageSize
      return this.filtered.slice(start, start + this.pageSize)
    },
    detailImages() {
      return this.detailStore?.images || []
    }
  },
  created() {
    this.fetchStores()
  },
  methods: {
    async fetchStores() {
      this.loading = true
      try {
        const res = await getStoreListRequest()
        if (res.code !== 200) throw new Error(res.message || '查询门店失败')
        this.stores = Array.isArray(res.data) ? res.data : []
      } catch (e) {
        this.$message.error(e.message || '查询门店失败')
      } finally {
        this.loading = false
      }
    },
    storeFirstImage(store) {
      return store?.images?.[0] || 'https://via.placeholder.com/640x360?text=Store'
    },
    async openDetail(id) {
      this.detailVisible = true
      this.detailStore = null
      this.detailLoading = true
      try {
        const res = await getStoreDetailRequest(id)
        if (res.code !== 200 || !res.data) throw new Error(res.message || '查询门店详情失败')
        this.detailStore = res.data
      } catch (e) {
        this.$message.error(e.message || '查询门店详情失败')
        this.detailVisible = false
      } finally {
        this.detailLoading = false
      }
    },
    handleSearch() {
      this.page = 1
    },
    reset() {
      this.query = { keyword: '', city: '', rating: 0 }
      this.page = 1
    },
    goBooking(id) {
      if (!id) return
      this.detailVisible = false
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

.detail-img {
  width: 100%;
  height: 280px;
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

.desc {
  margin-top: 8px;
  color: #606266;
}
</style>
