<template>
  <div>
    <page-header title="商家列表" desc="管理已入驻商家" />
    <query-bar :model="query" class="mt-16">
      <el-form-item label="关键词">
        <el-input v-model="query.keyword" placeholder="商家/门店" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部">
          <el-option label="全部" value="" />
          <el-option label="启用" value="active" />
          <el-option label="停用" value="disabled" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="page = 1">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </query-bar>

    <data-table :data="paged" :loading="loading" class="mt-16">
      <el-table-column prop="name" label="商家" />
      <el-table-column prop="storeName" label="门店" />
      <el-table-column prop="status" label="状态" width="120">
        <template slot-scope="scope"><status-tag :status="scope.row.status" /></template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template slot-scope="scope">
          <el-button type="text" @click="toggle(scope.row)">切换状态</el-button>
        </template>
      </el-table-column>
    </data-table>

    <pagination :page="page" :page-size="pageSize" :total="filtered.length" @change="page = $event" @size="pageSize = $event" />
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'
import QueryBar from '@/components/common/QueryBar.vue'
import DataTable from '@/components/common/DataTable.vue'
import Pagination from '@/components/common/Pagination.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import { list, update, findById } from '@/mock'

export default {
  name: 'MerchantList',
  components: { PageHeader, QueryBar, DataTable, Pagination, StatusTag },
  data() {
    return {
      loading: false,
      merchants: [],
      query: { keyword: '', status: '' },
      page: 1,
      pageSize: 8
    }
  },
  computed: {
    filtered() {
      return this.merchants.filter(m => {
        const matchKeyword = !this.query.keyword || m.name.includes(this.query.keyword) || m.storeName.includes(this.query.keyword)
        const matchStatus = !this.query.status || m.status === this.query.status
        return matchKeyword && matchStatus
      })
    },
    paged() {
      const start = (this.page - 1) * this.pageSize
      return this.filtered.slice(start, start + this.pageSize)
    }
  },
  created() {
    this.refresh()
  },
  methods: {
    refresh() {
      this.loading = true
      setTimeout(() => {
        this.merchants = list('merchants').map(m => ({
          ...m,
          storeName: (findById('stores', m.storeId) || {}).name
        }))
        this.loading = false
      }, 200)
    },
    reset() {
      this.query = { keyword: '', status: '' }
      this.page = 1
    },
    toggle(row) {
      const next = row.status === 'active' ? 'disabled' : 'active'
      update('merchants', row.id, { status: next })
      row.status = next
      this.$message.success('状态已更新')
    }
  }
}
</script>

