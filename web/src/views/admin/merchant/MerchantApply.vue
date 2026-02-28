<template>
  <div>
    <page-header title="入驻审核" desc="审核商家入驻申请" />
    <query-bar :model="query" class="mt-16">
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部">
          <el-option label="全部" value="" />
          <el-option label="待审核" value="pending" />
          <el-option label="已通过" value="approved" />
          <el-option label="已驳回" value="rejected" />
        </el-select>
      </el-form-item>
      <el-form-item label="关键词">
        <el-input v-model="query.keyword" placeholder="门店/联系人" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="page = 1">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </query-bar>

    <data-table :data="paged" :loading="loading" class="mt-16">
      <el-table-column prop="storeName" label="门店" />
      <el-table-column prop="owner" label="联系人" width="120" />
      <el-table-column prop="phone" label="手机号" width="120" />
      <el-table-column prop="status" label="状态" width="120">
        <template slot-scope="scope"><status-tag :status="scope.row.status" /></template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template slot-scope="scope">
          <el-button type="text" @click="openDrawer(scope.row)">查看</el-button>
          <el-button type="text" :disabled="scope.row.status !== 'pending'" @click="openAudit(scope.row)">审核</el-button>
        </template>
      </el-table-column>
    </data-table>

    <pagination :page="page" :page-size="pageSize" :total="filtered.length" @change="page = $event" @size="pageSize = $event" />

    <el-drawer title="申请详情" :visible.sync="drawerVisible" size="30%">
      <div v-if="current">
        <p>门店：{{ current.storeName }}</p>
        <p>联系人：{{ current.owner }}</p>
        <p>手机号：{{ current.phone }}</p>
        <p>城市：{{ current.city }}</p>
        <p>状态：{{ current.status }}</p>
      </div>
    </el-drawer>

    <el-dialog title="审核" :visible.sync="dialogVisible" width="420px">
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
import { list, adminAuditMerchantApply } from '@/mock'

export default {
  name: 'MerchantApply',
  components: { PageHeader, QueryBar, DataTable, Pagination, StatusTag },
  data() {
    return {
      loading: false,
      applies: [],
      query: { status: '', keyword: '' },
      page: 1,
      pageSize: 8,
      drawerVisible: false,
      dialogVisible: false,
      current: null,
      form: { id: null, pass: true, opinion: '' },
      rules: {
        opinion: [{ required: true, message: '请输入意见', trigger: 'blur' }]
      }
    }
  },
  computed: {
    filtered() {
      return this.applies.filter(a => {
        const matchStatus = !this.query.status || a.status === this.query.status
        const matchKeyword = !this.query.keyword || a.storeName.includes(this.query.keyword) || a.owner.includes(this.query.keyword)
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
        this.applies = list('merchantApplies')
        this.loading = false
      }, 200)
    },
    reset() {
      this.query = { status: '', keyword: '' }
      this.page = 1
    },
    openDrawer(row) {
      this.current = row
      this.drawerVisible = true
    },
    openAudit(row) {
      this.form = { id: row.id, pass: true, opinion: '' }
      this.dialogVisible = true
    },
    submit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        adminAuditMerchantApply(this.form.id, this.form.pass, this.form.opinion)
        this.dialogVisible = false
        this.$message.success('审核完成')
        this.refresh()
      })
    }
  }
}
</script>

