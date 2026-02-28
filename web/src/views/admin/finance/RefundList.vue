<template>
  <div>
    <page-header title="退款审核" desc="处理用户退款申请" />
    <query-bar :model="query" class="mt-16">
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部">
          <el-option label="全部" value="" />
          <el-option label="待审核" value="pending" />
          <el-option label="已通过" value="approved" />
          <el-option label="已驳回" value="rejected" />
        </el-select>
      </el-form-item>
      <el-form-item label="订单号">
        <el-input v-model="query.keyword" placeholder="订单号" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="page = 1">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </query-bar>

    <data-table :data="paged" :loading="loading" class="mt-16">
      <el-table-column prop="orderId" label="订单号" width="120" />
      <el-table-column prop="reason" label="原因" />
      <el-table-column prop="status" label="状态" width="120">
        <template slot-scope="scope"><status-tag :status="scope.row.status" /></template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template slot-scope="scope">
          <el-button type="text" @click="openDialog(scope.row)">审核</el-button>
        </template>
      </el-table-column>
    </data-table>

    <pagination :page="page" :page-size="pageSize" :total="filtered.length" @change="page = $event" @size="pageSize = $event" />

    <el-dialog title="退款审核" :visible.sync="dialogVisible" width="420px">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="结果" prop="pass">
          <el-radio-group v-model="form.pass">
            <el-radio :label="true">通过</el-radio>
            <el-radio :label="false">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="意见" prop="opinion">
          <el-input type="textarea" v-model="form.opinion" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">提交</el-button>
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
import { list, adminAuditRefund } from '@/mock'

export default {
  name: 'RefundList',
  components: { PageHeader, QueryBar, DataTable, Pagination, StatusTag },
  data() {
    return {
      loading: false,
      refunds: [],
      query: { status: '', keyword: '' },
      page: 1,
      pageSize: 8,
      dialogVisible: false,
      form: { id: null, pass: true, opinion: '' },
      rules: { opinion: [{ required: true, message: '请输入意见', trigger: 'blur' }] }
    }
  },
  computed: {
    filtered() {
      return this.refunds.filter(r => {
        const matchStatus = !this.query.status || r.status === this.query.status
        const matchKeyword = !this.query.keyword || String(r.orderId).includes(this.query.keyword)
        return matchStatus && matchKeyword
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
        this.refunds = list('refunds')
        this.loading = false
      }, 200)
    },
    reset() {
      this.query = { status: '', keyword: '' }
      this.page = 1
    },
    openDialog(row) {
      this.form = { id: row.id, pass: true, opinion: '' }
      this.dialogVisible = true
    },
    submit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        adminAuditRefund(this.form.id, this.form.pass, this.form.opinion)
        this.dialogVisible = false
        this.$message.success('审核完成')
        this.refresh()
      })
    }
  }
}
</script>

