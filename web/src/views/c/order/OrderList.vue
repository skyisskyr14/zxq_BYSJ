<template>
  <div>
    <page-header title="订单列表" desc="查看历史与当前订单" />
    <query-bar :model="query" class="mt-16">
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部">
          <el-option label="全部" value="" />
          <el-option label="待确认" value="pending_confirm" />
          <el-option label="待入住" value="pending_checkin" />
          <el-option label="寄养中" value="boarding" />
          <el-option label="已完成" value="completed" />
          <el-option label="退款中" value="refund_pending" />
        </el-select>
      </el-form-item>
      <el-form-item label="日期">
        <el-date-picker v-model="query.range" value-format="yyyy-MM-dd" type="daterange" range-separator="至" start-placeholder="开始" end-placeholder="结束" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="page = 1">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </query-bar>

    <data-table :data="paged" :loading="loading" class="mt-16">
      <el-table-column prop="id" label="订单号" width="120" />
      <el-table-column prop="storeName" label="门店" />
      <el-table-column prop="dateRange" label="日期">
        <template slot-scope="scope">
          {{ scope.row.dateRange.join(' 至 ') }}
        </template>
      </el-table-column>
      <el-table-column prop="amount" label="金额" width="100">
        <template slot-scope="scope">￥{{ scope.row.amount }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="120">
        <template slot-scope="scope">
          <status-tag :status="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template slot-scope="scope">
          <el-button type="text" @click="$router.push(`/c/order/${scope.row.id}`)">详情</el-button>
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
  name: 'OrderList',
  components: { PageHeader, QueryBar, DataTable, Pagination, StatusTag },
  data() {
    return {
      loading: false,
      orders: [],
      query: { status: '', range: [] },
      page: 1,
      pageSize: 8
    }
  },
  computed: {
    filtered() {
      return this.orders.filter(order => {
        const matchStatus = !this.query.status || order.status === this.query.status
        const matchDate = !this.query.range.length || (order.dateRange[0] >= this.query.range[0] && order.dateRange[1] <= this.query.range[1])
        return matchStatus && matchDate
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
      const user = this.$store.getters['auth/userInfo']
      this.orders = list('orders').filter(o => String(o.userId) === String(user ? user.id : 1)).map(o => ({
        ...o,
        storeName: (findById('stores', o.storeId) || {}).name
      }))
      this.loading = false
    }, 300)
  },
  methods: {
    reset() {
      this.query = { status: '', range: [] }
      this.page = 1
    }
  }
}
</script>

