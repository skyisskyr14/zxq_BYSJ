<template>
  <div>
    <page-header title="退房办理" desc="完成寄养并释放笼位" />
    <query-bar :model="query" class="mt-16">
      <el-form-item label="订单号">
        <el-input v-model="query.keyword" placeholder="订单号" />
      </el-form-item>
      <el-form-item label="门店">
        <el-input v-model="query.store" placeholder="门店名" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="page = 1">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </query-bar>

    <div class="card mt-16">
      <el-form :model="form" label-width="100px">
        <el-form-item label="寄养中订单">
          <el-select v-model="form.orderId" placeholder="选择订单" class="w-100">
            <el-option v-for="order in boardingOrders" :key="order.id" :label="`#${order.id}`" :value="order.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="openConfirm">确认退房</el-button>
        </el-form-item>
      </el-form>
    </div>

    <data-table :data="paged" :loading="loading" class="mt-16">
      <el-table-column prop="id" label="订单号" width="120" />
      <el-table-column prop="storeName" label="门店" />
      <el-table-column prop="dateRange" label="日期">
        <template slot-scope="scope">{{ scope.row.dateRange.join(' 至 ') }}</template>
      </el-table-column>
      <el-table-column prop="amount" label="金额">
        <template slot-scope="scope">¥{{ scope.row.amount }}</template>
      </el-table-column>
    </data-table>

    <pagination :page="page" :page-size="pageSize" :total="filtered.length" @change="page = $event" @size="pageSize = $event" />

    <el-dialog title="确认退房" :visible.sync="confirmVisible" width="420px">
      <el-form ref="confirmForm" :model="confirmForm" :rules="confirmRules" label-width="80px">
        <el-form-item label="备注" prop="remark">
          <el-input type="textarea" v-model="confirmForm.remark" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="confirmVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">确认</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'
import QueryBar from '@/components/common/QueryBar.vue'
import DataTable from '@/components/common/DataTable.vue'
import Pagination from '@/components/common/Pagination.vue'
import { list, checkout, findById } from '@/mock'

export default {
  name: 'Checkout',
  components: { PageHeader, QueryBar, DataTable, Pagination },
  data() {
    return {
      loading: false,
      orders: [],
      form: { orderId: null },
      query: { keyword: '', store: '' },
      page: 1,
      pageSize: 8,
      confirmVisible: false,
      confirmForm: { remark: '' },
      confirmRules: { remark: [{ required: true, message: '请输入备注', trigger: 'blur' }] }
    }
  },
  computed: {
    boardingOrders() {
      return this.orders.filter(o => o.status === 'boarding')
    },
    filtered() {
      return this.boardingOrders.filter(o => {
        const matchKeyword = !this.query.keyword || String(o.id).includes(this.query.keyword)
        const matchStore = !this.query.store || o.storeName.includes(this.query.store)
        return matchKeyword && matchStore
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
        this.orders = list('orders').map(o => ({ ...o, storeName: (findById('stores', o.storeId) || {}).name }))
        this.loading = false
      }, 200)
    },
    reset() {
      this.query = { keyword: '', store: '' }
      this.page = 1
    },
    openConfirm() {
      if (!this.form.orderId) {
        this.$message.warning('请选择订单')
        return
      }
      this.confirmForm = { remark: '' }
      this.confirmVisible = true
    },
    submit() {
      this.$refs.confirmForm.validate(valid => {
        if (!valid) return
        checkout(this.form.orderId)
        this.$message.success('退房完成')
        this.confirmVisible = false
        this.form = { orderId: null }
        this.refresh()
      })
    }
  }
}
</script>
