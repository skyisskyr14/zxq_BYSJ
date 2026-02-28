<template>
  <div>
    <page-header title="权限列表" desc="查看权限配置" />
    <query-bar :model="query" class="mt-16">
      <el-form-item label="类型">
        <el-select v-model="query.type" placeholder="全部">
          <el-option label="全部" value="" />
          <el-option label="菜单" value="菜单" />
          <el-option label="按钮" value="按钮" />
        </el-select>
      </el-form-item>
      <el-form-item label="关键词">
        <el-input v-model="query.keyword" placeholder="权限名称" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="page = 1">查询</el-button>
        <el-button @click="reset">重置</el-button>
        <el-button type="success" @click="openDialog()">新增</el-button>
      </el-form-item>
    </query-bar>

    <data-table :data="paged" :loading="loading" class="mt-16">
      <el-table-column prop="name" label="权限" />
      <el-table-column prop="code" label="编码" />
      <el-table-column prop="type" label="类型" width="120" />
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
        <el-form-item label="编码" prop="code">
          <el-input v-model="form.code" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type">
            <el-option label="菜单" value="菜单" />
            <el-option label="按钮" value="按钮" />
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
import { list, create, update } from '@/mock'

export default {
  name: 'PermissionList',
  components: { PageHeader, QueryBar, DataTable, Pagination },
  data() {
    return {
      loading: false,
      permissions: [],
      query: { type: '', keyword: '' },
      page: 1,
      pageSize: 8,
      dialogVisible: false,
      form: { id: null, name: '', code: '', type: '菜单' },
      rules: {
        name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
        code: [{ required: true, message: '请输入编码', trigger: 'blur' }]
      }
    }
  },
  computed: {
    filtered() {
      return this.permissions.filter(p => {
        const matchType = !this.query.type || p.type === this.query.type
        const matchKeyword = !this.query.keyword || p.name.includes(this.query.keyword)
        return matchType && matchKeyword
      })
    },
    paged() {
      const start = (this.page - 1) * this.pageSize
      return this.filtered.slice(start, start + this.pageSize)
    },
    dialogTitle() {
      return this.form.id ? '编辑权限' : '新增权限'
    }
  },
  created() {
    this.loading = true
    setTimeout(() => {
      this.permissions = list('permissions')
      this.loading = false
    }, 200)
  },
  methods: {
    reset() {
      this.query = { type: '', keyword: '' }
      this.page = 1
    },
    openDialog(row) {
      this.form = row ? { ...row } : { id: null, name: '', code: '', type: '菜单' }
      this.dialogVisible = true
    },
    submit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (this.form.id) {
          update('permissions', this.form.id, this.form)
        } else {
          create('permissions', this.form)
        }
        this.dialogVisible = false
        this.permissions = list('permissions')
      })
    }
  }
}
</script>

