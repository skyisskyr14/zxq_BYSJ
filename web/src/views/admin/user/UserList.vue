<template>
  <div>
    <page-header title="用户列表" desc="管理平台用户" />
    <query-bar :model="query" class="mt-16">
      <el-form-item label="角色">
        <el-select v-model="query.role" placeholder="全部">
          <el-option label="全部" value="" />
          <el-option label="用户" value="user" />
          <el-option label="商家" value="merchant" />
          <el-option label="管理员" value="admin" />
        </el-select>
      </el-form-item>
      <el-form-item label="关键词">
        <el-input v-model="query.keyword" placeholder="姓名/手机号" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="page = 1">查询</el-button>
        <el-button @click="reset">重置</el-button>
        <el-button type="success" @click="openDialog()">新增</el-button>
      </el-form-item>
    </query-bar>

    <data-table :data="paged" :loading="loading" class="mt-16">
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="role" label="角色" width="120" />
      <el-table-column label="操作" width="160">
        <template slot-scope="scope">
          <el-button type="text" @click="openDialog(scope.row)">编辑</el-button>
          <el-button type="text" style="color:#f56c6c" @click="removeRow(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </data-table>

    <pagination :page="page" :page-size="pageSize" :total="filtered.length" @change="page = $event" @size="pageSize = $event" />

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="420px">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role">
            <el-option label="用户" value="user" />
            <el-option label="商家" value="merchant" />
            <el-option label="管理员" value="admin" />
          </el-select>
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
import { list, create, update, remove } from '@/mock'

export default {
  name: 'UserList',
  components: { PageHeader, QueryBar, DataTable, Pagination },
  data() {
    return {
      loading: false,
      users: [],
      query: { role: '', keyword: '' },
      page: 1,
      pageSize: 8,
      dialogVisible: false,
      form: { id: null, name: '', phone: '', role: 'user' },
      rules: {
        name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
      }
    }
  },
  computed: {
    filtered() {
      return this.users.filter(u => {
        const matchRole = !this.query.role || u.role === this.query.role
        const matchKeyword = !this.query.keyword || u.name.includes(this.query.keyword) || u.phone.includes(this.query.keyword)
        return matchRole && matchKeyword
      })
    },
    paged() {
      const start = (this.page - 1) * this.pageSize
      return this.filtered.slice(start, start + this.pageSize)
    },
    dialogTitle() {
      return this.form.id ? '编辑用户' : '新增用户'
    }
  },
  created() {
    this.refresh()
  },
  methods: {
    refresh() {
      this.loading = true
      setTimeout(() => {
        this.users = list('users')
        this.loading = false
      }, 200)
    },
    reset() {
      this.query = { role: '', keyword: '' }
      this.page = 1
    },
    openDialog(row) {
      this.form = row ? { ...row } : { id: null, name: '', phone: '', role: 'user' }
      this.dialogVisible = true
    },
    submit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (this.form.id) {
          update('users', this.form.id, this.form)
        } else {
          create('users', { ...this.form, username: `user${Date.now()}`, password: '123456' })
        }
        this.dialogVisible = false
        this.refresh()
      })
    },
    removeRow(row) {
      this.$confirm('确认删除该用户？', '提示').then(() => {
        remove('users', row.id)
        this.refresh()
      })
    }
  }
}
</script>

