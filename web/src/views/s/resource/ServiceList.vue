<template>
  <div>
    <page-header title="服务管理" desc="维护附加服务项目" />
    <query-bar :model="query" class="mt-16">
      <el-form-item label="关键词">
        <el-input v-model="query.keyword" placeholder="服务名称" />
      </el-form-item>
      <el-form-item label="价格<=">
        <el-input-number v-model="query.max" :min="0" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="page = 1">查询</el-button>
        <el-button @click="reset">重置</el-button>
        <el-button type="success" @click="openDialog()">新增</el-button>
      </el-form-item>
    </query-bar>

    <data-table :data="paged" :loading="loading" class="mt-16">
      <el-table-column prop="name" label="服务" />
      <el-table-column prop="price" label="价格" width="120">
        <template slot-scope="scope">￥{{ scope.row.price }}</template>
      </el-table-column>
      <el-table-column prop="description" label="说明" />
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
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" />
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="form.description" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'
import QueryBar from '@/components/common/QueryBar.vue'
import DataTable from '@/components/common/DataTable.vue'
import Pagination from '@/components/common/Pagination.vue'
import { createServiceRequest, deleteServiceRequest, listServiceRequest, updateServiceRequest } from '@/api/request/service'

export default {
  name: 'ServiceList',
  components: { PageHeader, QueryBar, DataTable, Pagination },
  data() {
    return {
      loading: false,
      saving: false,
      services: [],
      query: { keyword: '', max: 200 },
      page: 1,
      pageSize: 8,
      dialogVisible: false,
      form: { id: null, name: '', price: 20, description: '' },
      rules: {
        name: [{ required: true, message: '请输入名称', trigger: 'blur' }]
      }
    }
  },
  computed: {
    filtered() {
      return this.services.filter(s => {
        const matchKeyword = !this.query.keyword || (s.name || '').includes(this.query.keyword)
        const matchPrice = Number(s.price || 0) <= Number(this.query.max || 0)
        return matchKeyword && matchPrice
      })
    },
    paged() {
      const start = (this.page - 1) * this.pageSize
      return this.filtered.slice(start, start + this.pageSize)
    },
    dialogTitle() {
      return this.form.id ? '编辑服务' : '新增服务'
    }
  },
  created() {
    this.refresh()
  },
  methods: {
    async refresh() {
      this.loading = true
      try {
        const res = await listServiceRequest()
        if (res.code !== 200) throw new Error(res.message || '查询失败')
        this.services = Array.isArray(res.data) ? res.data : []
      } catch (e) {
        this.$message.error(e.message || '查询失败')
      } finally {
        this.loading = false
      }
    },
    reset() {
      this.query = { keyword: '', max: 200 }
      this.page = 1
    },
    openDialog(row) {
      this.form = row ? { ...row } : { id: null, name: '', price: 20, description: '' }
      this.dialogVisible = true
    },
    submit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.saving = true
        try {
          const api = this.form.id ? updateServiceRequest : createServiceRequest
          const payload = { ...this.form }
          if (!this.form.id) delete payload.id
          const res = await api(payload)
          if (res.code !== 200) throw new Error(res.message || '保存失败')
          this.$message.success(this.form.id ? '修改成功' : '新增成功')
          this.dialogVisible = false
          await this.refresh()
        } catch (e) {
          this.$message.error(e.message || '保存失败')
        } finally {
          this.saving = false
        }
      })
    },
    removeRow(row) {
      this.$confirm('确认删除该服务？', '提示').then(async () => {
        const res = await deleteServiceRequest(row.id)
        if (res.code !== 200) {
          this.$message.error(res.message || '删除失败')
          return
        }
        this.$message.success('删除成功')
        this.refresh()
      })
    }
  }
}
</script>
