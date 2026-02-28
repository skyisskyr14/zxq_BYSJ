<template>
  <div>
    <page-header title="门店管理" desc="维护门店信息" />
    <query-bar :model="query" class="mt-16">
      <el-form-item label="城市">
        <el-select v-model="query.city" placeholder="全部">
          <el-option label="全部" value="" />
          <el-option v-for="item in cities" :key="item" :label="item" :value="item" />
        </el-select>
      </el-form-item>
      <el-form-item label="关键词">
        <el-input v-model="query.keyword" placeholder="门店/地址" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="page = 1">查询</el-button>
        <el-button @click="reset">重置</el-button>
        <el-button type="success" @click="$router.push('/admin/store/edit')">新增</el-button>
      </el-form-item>
    </query-bar>

    <data-table :data="paged" :loading="loading" class="mt-16">
      <el-table-column prop="name" label="门店" />
      <el-table-column prop="city" label="城市" width="120" />
      <el-table-column prop="rating" label="评分" width="100" />
      <el-table-column label="操作" width="160">
        <template slot-scope="scope">
          <el-button type="text" @click="$router.push(`/admin/store/edit/${scope.row.id}`)">编辑</el-button>
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
import { list } from '@/mock'

export default {
  name: 'AdminStoreList',
  components: { PageHeader, QueryBar, DataTable, Pagination },
  data() {
    return {
      loading: false,
      stores: [],
      query: { city: '', keyword: '' },
      page: 1,
      pageSize: 8
    }
  },
  computed: {
    cities() {
      return [...new Set(this.stores.map(s => s.city))]
    },
    filtered() {
      return this.stores.filter(s => {
        const matchCity = !this.query.city || s.city === this.query.city
        const matchKeyword = !this.query.keyword || s.name.includes(this.query.keyword) || s.address.includes(this.query.keyword)
        return matchCity && matchKeyword
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
    }, 200)
  },
  methods: {
    reset() {
      this.query = { city: '', keyword: '' }
      this.page = 1
    }
  }
}
</script>

