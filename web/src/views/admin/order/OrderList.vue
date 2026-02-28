<template>
  <div>
    <page-header title="订单管理" desc="全量订单查看" />
    <query-bar :model="query" class="mt-16">
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部">
          <el-option label="全部" value="" />
          <el-option label="待确认" value="pending_confirm" />
          <el-option label="待入住" value="pending_checkin" />
          <el-option label="寄养中" value="boarding" />
          <el-option label="已完成" value="completed" />
        </el-select>
      </el-form-item>
      <el-form-item label="门店">
        <el-input v-model="query.keyword" placeholder="门店名" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="page = 1">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </query-bar>

    <data-table :data="paged" :loading="loading" class="mt-16">
      <el-table-column prop="id" label="订单号" width="120" />
      <el-table-column prop="storeName" label="门店" />
      <el-table-column prop="amount" label="金额" width="100">
        <template slot-scope="scope">￥{{ scope.row.amount }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="120">
        <template slot-scope="scope"><status-tag :status="scope.row.status" /></template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template slot-scope="scope">
          <el-button type="text" @click="$router.push(`/admin/order/detail/${scope.row.id}`)">详情</el-button>
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
import { list, findById } from '@/mock'

export default {
  name: 'AdminOrderList',
  components: { PageHeader, QueryBar, DataTable, Pagination, StatusTag },
  data() {
    return {
      loading: false,
      orders: [],
      query: { status: '', keyword: '' },
      page: 1,
      pageSize: 8
    }
  },
  computed: {
    filtered() {
      return this.orders.filter(o => {
        const matchStatus = !this.query.status || o.status === this.query.status
        const matchKeyword = !this.query.keyword || o.storeName.includes(this.query.keyword)
        return matchStatus && matchKeyword
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
      this.orders = list('orders').map(o => ({ ...o, storeName: (findById('stores', o.storeId) || {}).name }))
      this.loading = false
    }, 200)
  },
  methods: {
    reset() {
      this.query = { status: '', keyword: '' }
      this.page = 1
    }
  }
}
</script>

