<template>
  <div>
    <page-header title="评价管理" desc="查看用户评价" />
    <query-bar :model="query" class="mt-16">
      <el-form-item label="评分>=">
        <el-input-number v-model="query.min" :min="1" :max="5" />
      </el-form-item>
      <el-form-item label="关键词">
        <el-input v-model="query.keyword" placeholder="评价内容" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="page = 1">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </query-bar>

    <data-table :data="paged" :loading="loading" class="mt-16">
      <el-table-column prop="orderId" label="订单号" width="120" />
      <el-table-column prop="score" label="评分" width="100" />
      <el-table-column prop="content" label="评价" />
      <el-table-column label="操作" width="140">
        <template slot-scope="scope">
          <el-button type="text" @click="openDialog(scope.row)">回复</el-button>
        </template>
      </el-table-column>
    </data-table>

    <pagination :page="page" :page-size="pageSize" :total="filtered.length" @change="page = $event" @size="pageSize = $event" />

    <el-dialog title="回复评价" :visible.sync="dialogVisible" width="420px">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="回复" prop="reply">
          <el-input type="textarea" v-model="form.reply" />
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
import { list, update } from '@/mock'

export default {
  name: 'ReviewList',
  components: { PageHeader, QueryBar, DataTable, Pagination },
  data() {
    return {
      loading: false,
      reviews: [],
      query: { min: 1, keyword: '' },
      page: 1,
      pageSize: 8,
      dialogVisible: false,
      form: { id: null, reply: '' },
      rules: { reply: [{ required: true, message: '请输入回复', trigger: 'blur' }] }
    }
  },
  computed: {
    filtered() {
      return this.reviews.filter(r => {
        const matchScore = r.score >= Number(this.query.min || 1)
        const matchKeyword = !this.query.keyword || r.content.includes(this.query.keyword)
        return matchScore && matchKeyword
      })
    },
    paged() {
      const start = (this.page - 1) * this.pageSize
      return this.filtered.slice(start, start + this.pageSize)
    }
  },
  created() {
    this.loading = true
    setTimeout(() => {
      this.reviews = list('reviews')
      this.loading = false
    }, 200)
  },
  methods: {
    reset() {
      this.query = { min: 1, keyword: '' }
      this.page = 1
    },
    openDialog(row) {
      this.form = { id: row.id, reply: row.reply || '' }
      this.dialogVisible = true
    },
    submit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        update('reviews', this.form.id, { reply: this.form.reply })
        this.dialogVisible = false
        this.$message.success('已回复')
      })
    }
  }
}
</script>

