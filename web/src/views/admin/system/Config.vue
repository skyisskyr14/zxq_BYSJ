<template>
  <div>
    <page-header title="系统配置" desc="维护系统参数" />
    <query-bar :model="query" class="mt-16">
      <el-form-item label="关键词">
        <el-input v-model="query.keyword" placeholder="配置Key" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="query.remark" placeholder="备注" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="page = 1">查询</el-button>
        <el-button @click="reset">重置</el-button>
        <el-button type="success" @click="openDialog()">新增</el-button>
      </el-form-item>
    </query-bar>

    <data-table :data="paged" :loading="loading" class="mt-16">
      <el-table-column prop="key" label="配置Key" />
      <el-table-column prop="value" label="配置值" />
      <el-table-column prop="remark" label="备注" />
      <el-table-column label="操作" width="140">
        <template slot-scope="scope">
          <el-button type="text" @click="openDialog(scope.row)">编辑</el-button>
        </template>
      </el-table-column>
    </data-table>

    <pagination :page="page" :page-size="pageSize" :total="filtered.length" @change="page = $event" @size="pageSize = $event" />

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="420px">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="Key" prop="key">
          <el-input v-model="form.key" />
        </el-form-item>
        <el-form-item label="值" prop="value">
          <el-input v-model="form.value" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" />
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
  name: 'Config',
  components: { PageHeader, QueryBar, DataTable, Pagination },
  data() {
    return {
      loading: false,
      configs: [],
      query: { keyword: '', remark: '' },
      page: 1,
      pageSize: 8,
      dialogVisible: false,
      form: { id: null, key: '', value: '', remark: '' },
      rules: {
        key: [{ required: true, message: '请输入Key', trigger: 'blur' }],
        value: [{ required: true, message: '请输入值', trigger: 'blur' }]
      }
    }
  },
  computed: {
    filtered() {
      return this.configs.filter(c => {
        const matchKey = !this.query.keyword || c.key.includes(this.query.keyword)
        const matchRemark = !this.query.remark || c.remark.includes(this.query.remark)
        return matchKey && matchRemark
      })
    },
    paged() {
      const start = (this.page - 1) * this.pageSize
      return this.filtered.slice(start, start + this.pageSize)
    },
    dialogTitle() {
      return this.form.id ? '编辑配置' : '新增配置'
    }
  },
  created() {
    this.loading = true
    setTimeout(() => {
      this.configs = list('configs')
      this.loading = false
    }, 200)
  },
  methods: {
    reset() {
      this.query = { keyword: '', remark: '' }
      this.page = 1
    },
    openDialog(row) {
      this.form = row ? { ...row } : { id: null, key: '', value: '', remark: '' }
      this.dialogVisible = true
    },
    submit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (this.form.id) {
          update('configs', this.form.id, this.form)
        } else {
          create('configs', this.form)
        }
        this.dialogVisible = false
        this.configs = list('configs')
      })
    }
  }
}
</script>

