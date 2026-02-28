<template>
  <div>
    <page-header title="公告管理" desc="发布平台公告" />
    <query-bar :model="query" class="mt-16">
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部">
          <el-option label="全部" value="" />
          <el-option label="已发布" value="published" />
          <el-option label="草稿" value="draft" />
        </el-select>
      </el-form-item>
      <el-form-item label="关键词">
        <el-input v-model="query.keyword" placeholder="公告标题" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="page = 1">查询</el-button>
        <el-button @click="reset">重置</el-button>
        <el-button type="success" @click="openDialog()">新增</el-button>
      </el-form-item>
    </query-bar>

    <data-table :data="paged" :loading="loading" class="mt-16">
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="status" label="状态" width="120">
        <template slot-scope="scope"><status-tag :status="scope.row.status" /></template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template slot-scope="scope">
          <el-button type="text" @click="openDialog(scope.row)">编辑</el-button>
          <el-button type="text" @click="publish(scope.row)">发布</el-button>
        </template>
      </el-table-column>
    </data-table>

    <pagination :page="page" :page-size="pageSize" :total="filtered.length" @change="page = $event" @size="pageSize = $event" />

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="520px">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input type="textarea" v-model="form.content" rows="4" />
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
  name: 'Notice',
  components: { PageHeader, QueryBar, DataTable, Pagination, StatusTag },
  data() {
    return {
      loading: false,
      notices: [],
      query: { status: '', keyword: '' },
      page: 1,
      pageSize: 8,
      dialogVisible: false,
      form: { id: null, title: '', content: '' },
      rules: {
        title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
        content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
      }
    }
  },
  computed: {
    filtered() {
      return this.notices.filter(n => {
        const matchStatus = !this.query.status || n.status === this.query.status
        const matchKeyword = !this.query.keyword || n.title.includes(this.query.keyword)
        return matchStatus && matchKeyword
      })
    },
    paged() {
      const start = (this.page - 1) * this.pageSize
      return this.filtered.slice(start, start + this.pageSize)
    },
    dialogTitle() {
      return this.form.id ? '编辑公告' : '新增公告'
    }
  },
  created() {
    this.loading = true
    setTimeout(() => {
      this.notices = list('notices')
      this.loading = false
    }, 200)
  },
  methods: {
    reset() {
      this.query = { status: '', keyword: '' }
      this.page = 1
    },
    openDialog(row) {
      this.form = row ? { ...row } : { id: null, title: '', content: '' }
      this.dialogVisible = true
    },
    submit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (this.form.id) {
          update('notices', this.form.id, this.form)
        } else {
          create('notices', { ...this.form, status: 'draft', createdAt: new Date().toISOString().slice(0, 10) })
        }
        this.dialogVisible = false
        this.notices = list('notices')
      })
    },
    publish(row) {
      update('notices', row.id, { status: 'published' })
      row.status = 'published'
      this.$message.success('已发布')
    }
  }
}
</script>

