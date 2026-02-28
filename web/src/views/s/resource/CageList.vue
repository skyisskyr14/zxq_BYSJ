<template>
  <div>
    <page-header title="笼位管理" desc="管理门店笼位资源" />
    <query-bar :model="query" class="mt-16">
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部">
          <el-option label="全部" value="" />
          <el-option label="空闲" value="free" />
          <el-option label="占用" value="occupied" />
        </el-select>
      </el-form-item>
      <el-form-item label="规格">
        <el-select v-model="query.size" placeholder="全部">
          <el-option label="全部" value="" />
          <el-option label="S" value="S" />
          <el-option label="M" value="M" />
          <el-option label="L" value="L" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="page = 1">查询</el-button>
        <el-button @click="reset">重置</el-button>
        <el-button type="success" @click="openDialog()">新增</el-button>
      </el-form-item>
    </query-bar>

    <data-table :data="paged" :loading="loading" class="mt-16">
      <el-table-column prop="code" label="编号" />
      <el-table-column prop="size" label="规格" width="100" />
      <el-table-column prop="price" label="价格" width="120">
        <template slot-scope="scope">￥{{ scope.row.price }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="120">
        <template slot-scope="scope"><status-tag :status="scope.row.status" /></template>
      </el-table-column>
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
        <el-form-item label="编号" prop="code">
          <el-input v-model="form.code" />
        </el-form-item>
        <el-form-item label="规格" prop="size">
          <el-select v-model="form.size">
            <el-option label="S" value="S" />
            <el-option label="M" value="M" />
            <el-option label="L" value="L" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status">
            <el-option label="空闲" value="free" />
            <el-option label="占用" value="occupied" />
          </el-select>
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
import StatusTag from '@/components/common/StatusTag.vue'
import { createCageRequest, deleteCageRequest, listCageRequest, updateCageRequest } from '@/api/request/cage'

export default {
  name: 'CageList',
  components: { PageHeader, QueryBar, DataTable, Pagination, StatusTag },
  data() {
    return {
      loading: false,
      saving: false,
      cages: [],
      query: { status: '', size: '' },
      page: 1,
      pageSize: 8,
      dialogVisible: false,
      form: { id: null, code: '', size: '', price: 60, status: 'free' },
      rules: {
        code: [{ required: true, message: '请输入编号', trigger: 'blur' }],
        size: [{ required: true, message: '请选择规格', trigger: 'change' }],
        status: [{ required: true, message: '请选择状态', trigger: 'change' }]
      }
    }
  },
  computed: {
    filtered() {
      return this.cages.filter(c => {
        const matchStatus = !this.query.status || c.status === this.query.status
        const matchSize = !this.query.size || c.size === this.query.size
        return matchStatus && matchSize
      })
    },
    paged() {
      const start = (this.page - 1) * this.pageSize
      return this.filtered.slice(start, start + this.pageSize)
    },
    dialogTitle() {
      return this.form.id ? '编辑笼位' : '新增笼位'
    }
  },
  created() {
    this.refresh()
  },
  methods: {
    async refresh() {
      this.loading = true
      try {
        const res = await listCageRequest()
        if (res.code !== 200) throw new Error(res.message || '查询失败')
        this.cages = Array.isArray(res.data) ? res.data : []
      } catch (e) {
        this.$message.error(e.message || '查询失败')
      } finally {
        this.loading = false
      }
    },
    reset() {
      this.query = { status: '', size: '' }
      this.page = 1
    },
    openDialog(row) {
      this.form = row ? { ...row } : { id: null, code: '', size: '', price: 60, status: 'free' }
      this.dialogVisible = true
    },
    submit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.saving = true
        try {
          const api = this.form.id ? updateCageRequest : createCageRequest
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
      this.$confirm('确认删除该笼位？', '提示').then(async () => {
        const res = await deleteCageRequest(row.id)
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
