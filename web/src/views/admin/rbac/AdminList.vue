<template>
  <div>
    <page-header title="管理员管理" desc="维护管理员账号" />
    <query-bar :model="query" class="mt-16">
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部">
          <el-option label="全部" value="" />
          <el-option label="启用" value="active" />
          <el-option label="停用" value="disabled" />
        </el-select>
      </el-form-item>
      <el-form-item label="关键词">
        <el-input v-model="query.keyword" placeholder="姓名" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="page = 1">查询</el-button>
        <el-button @click="reset">重置</el-button>
        <el-button type="success" @click="openDialog()">新增</el-button>
      </el-form-item>
    </query-bar>

    <data-table :data="paged" :loading="loading" class="mt-16">
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="role" label="角色" />
      <el-table-column prop="status" label="状态" width="120">
        <template slot-scope="scope"><status-tag :status="scope.row.status" /></template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template slot-scope="scope">
          <el-button type="text" @click="openDialog(scope.row)">编辑</el-button>
          <el-button type="text" @click="toggle(scope.row)">切换状态</el-button>
        </template>
      </el-table-column>
    </data-table>

    <pagination :page="page" :page-size="pageSize" :total="filtered.length" @change="page = $event" @size="pageSize = $event" />

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="420px">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-input v-model="form.role" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
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
import { list, create, update } from '@/mock'

export default {
  name: 'AdminList',
  components: { PageHeader, QueryBar, DataTable, Pagination, StatusTag },
  data() {
    return {
      loading: false,
      admins: [],
      query: { status: '', keyword: '' },
      page: 1,
      pageSize: 8,
      dialogVisible: false,
      form: { id: null, name: '', role: '' },
      rules: { name: [{ required: true, message: '请输入姓名', trigger: 'blur' }] }
    }
  },
  computed: {
    filtered() {
      return this.admins.filter(a => {
        const matchStatus = !this.query.status || a.status === this.query.status
        const matchKeyword = !this.query.keyword || a.name.includes(this.query.keyword)
        return matchStatus && matchKeyword
      })
    },
    paged() {
      const start = (this.page - 1) * this.pageSize
      return this.filtered.slice(start, start + this.pageSize)
    },
    dialogTitle() {
      return this.form.id ? '编辑管理员' : '新增管理员'
    }
  },
  created() {
    this.loading = true
    setTimeout(() => {
      this.admins = list('adminUsers')
      this.loading = false
    }, 200)
  },
  methods: {
    reset() {
      this.query = { status: '', keyword: '' }
      this.page = 1
    },
    openDialog(row) {
      this.form = row ? { ...row } : { id: null, name: '', role: '' }
      this.dialogVisible = true
    },
    submit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (this.form.id) {
          update('adminUsers', this.form.id, this.form)
        } else {
          create('adminUsers', { ...this.form, status: 'active' })
        }
        this.dialogVisible = false
        this.admins = list('adminUsers')
      })
    },
    toggle(row) {
      const next = row.status === 'active' ? 'disabled' : 'active'
      update('adminUsers', row.id, { status: next })
      row.status = next
    }
  }
}
</script>

