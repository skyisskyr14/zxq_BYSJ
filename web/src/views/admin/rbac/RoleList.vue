<template>
  <div>
    <page-header title="角色管理" desc="维护角色与权限" />
    <query-bar :model="query" class="mt-16">
      <el-form-item label="关键词">
        <el-input v-model="query.keyword" placeholder="角色名称" />
      </el-form-item>
      <el-form-item label="描述">
        <el-input v-model="query.desc" placeholder="描述" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="page = 1">查询</el-button>
        <el-button @click="reset">重置</el-button>
        <el-button type="success" @click="openDialog()">新增</el-button>
      </el-form-item>
    </query-bar>

    <data-table :data="paged" :loading="loading" class="mt-16">
      <el-table-column prop="name" label="角色" />
      <el-table-column prop="desc" label="描述" />
      <el-table-column label="操作" width="140">
        <template slot-scope="scope">
          <el-button type="text" @click="openDialog(scope.row)">编辑</el-button>
        </template>
      </el-table-column>
    </data-table>

    <pagination :page="page" :page-size="pageSize" :total="filtered.length" @change="page = $event" @size="pageSize = $event" />

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="420px">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述" prop="desc">
          <el-input v-model="form.desc" />
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
import { list, create, update } from '@/mock'

export default {
  name: 'RoleList',
  components: { PageHeader, QueryBar, DataTable, Pagination },
  data() {
    return {
      loading: false,
      roles: [],
      query: { keyword: '', desc: '' },
      page: 1,
      pageSize: 8,
      dialogVisible: false,
      form: { id: null, name: '', desc: '' },
      rules: { name: [{ required: true, message: '请输入名称', trigger: 'blur' }] }
    }
  },
  computed: {
    filtered() {
      return this.roles.filter(r => {
        const matchKeyword = !this.query.keyword || r.name.includes(this.query.keyword)
        const matchDesc = !this.query.desc || r.desc.includes(this.query.desc)
        return matchKeyword && matchDesc
      })
    },
    paged() {
      const start = (this.page - 1) * this.pageSize
      return this.filtered.slice(start, start + this.pageSize)
    },
    dialogTitle() {
      return this.form.id ? '编辑角色' : '新增角色'
    }
  },
  created() {
    this.loading = true
    setTimeout(() => {
      this.roles = list('roles')
      this.loading = false
    }, 200)
  },
  methods: {
    reset() {
      this.query = { keyword: '', desc: '' }
      this.page = 1
    },
    openDialog(row) {
      this.form = row ? { ...row } : { id: null, name: '', desc: '' }
      this.dialogVisible = true
    },
    submit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (this.form.id) {
          update('roles', this.form.id, this.form)
        } else {
          create('roles', this.form)
        }
        this.dialogVisible = false
        this.roles = list('roles')
      })
    }
  }
}
</script>

