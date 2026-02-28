<template>
  <div>
    <page-header title="预约管理" desc="处理用户预约申请" />
    <query-bar :model="query" class="mt-16">
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部">
          <el-option label="全部" value="" />
          <el-option label="待处理" value="pending" />
          <el-option label="已通过" value="approved" />
          <el-option label="已拒绝" value="rejected" />
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
      <el-table-column prop="id" label="预约号" width="120" />
      <el-table-column prop="storeName" label="门店" />
      <el-table-column prop="dateRange" label="日期">
        <template slot-scope="scope">{{ scope.row.dateRange.join(' 至 ') }}</template>
      </el-table-column>
      <el-table-column label="状态" width="120">
        <template slot-scope="scope"><status-tag :status="scope.row.status" /></template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="scope">
          <el-button type="text" :disabled="scope.row.status !== 'pending'" @click="openDialog(scope.row, true)">确认</el-button>
          <el-button type="text" :disabled="scope.row.status !== 'pending'" style="color: #f56c6c" @click="openDialog(scope.row, false)">拒绝</el-button>
        </template>
      </el-table-column>
    </data-table>

    <pagination :page="page" :page-size="pageSize" :total="filtered.length" @change="page = $event" @size="pageSize = $event" />

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="420px">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="原因" prop="reason">
          <el-input type="textarea" v-model="form.reason" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'
import QueryBar from '@/components/common/QueryBar.vue'
import DataTable from '@/components/common/DataTable.vue'
import Pagination from '@/components/common/Pagination.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import { list, merchantApproveBooking, findById } from '@/mock'

export default {
  name: 'BookingList',
  components: { PageHeader, QueryBar, DataTable, Pagination, StatusTag },
  data() {
    return {
      loading: false,
      bookings: [],
      query: { status: '', range: [] },
      page: 1,
      pageSize: 8,
      dialogVisible: false,
      dialogTitle: '处理预约',
      form: { id: null, ok: true, reason: '' },
      rules: { reason: [{ required: true, message: '请输入原因', trigger: 'blur' }] }
    }
  },
  computed: {
    filtered() {
      return this.bookings.filter(b => {
        const matchStatus = !this.query.status || b.status === this.query.status
        const matchDate = !this.query.range.length || (b.dateRange[0] >= this.query.range[0] && b.dateRange[1] <= this.query.range[1])
        return matchStatus && matchDate
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
        this.bookings = list('bookings').map(b => ({
          ...b,
          storeName: (findById('stores', b.storeId) || {}).name
        }))
        this.loading = false
      }, 200)
    },
    reset() {
      this.query = { status: '', range: [] }
      this.page = 1
    },
    openDialog(row, ok) {
      this.form = { id: row.id, ok, reason: '' }
      this.dialogTitle = ok ? '确认预约' : '拒绝预约'
      this.dialogVisible = true
    },
    submit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        merchantApproveBooking(this.form.id, this.form.ok, this.form.reason)
        this.dialogVisible = false
        this.$message.success('处理完成')
        this.refresh()
      })
    }
  }
}
</script>

