<template>
  <div>
    <page-header title="房型管理" desc="维护房型价格与容量" />
    <query-bar :model="query" class="mt-16">
      <el-form-item label="关键词">
        <el-input v-model="query.keyword" placeholder="房型名称" />
      </el-form-item>
      <el-form-item label="价格>=">
        <el-input-number v-model="query.min" :min="0" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="page = 1">查询</el-button>
        <el-button @click="reset">重置</el-button>
        <el-button type="success" @click="openDialog()">新增</el-button>
      </el-form-item>
    </query-bar>

    <data-table :data="paged" :loading="loading" class="mt-16">
      <el-table-column prop="name" label="房型" />
      <el-table-column prop="price" label="价格" width="120">
        <template slot-scope="scope">￥{{ scope.row.price }}</template>
      </el-table-column>
      <el-table-column prop="capacity" label="容量" width="100" />
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
          <el-input-number v-model="form.price" :min="10" />
        </el-form-item>
        <el-form-item label="容量" prop="capacity">
          <el-input-number v-model="form.capacity" :min="1" />
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
  name: 'RoomList',
  components: { PageHeader, QueryBar, DataTable, Pagination },
  data() {
    return {
      loading: false,
      rooms: [],
      query: { keyword: '', min: 0 },
      page: 1,
      pageSize: 8,
      dialogVisible: false,
      form: { id: null, name: '', price: 100, capacity: 1 },
      rules: {
        name: [{ required: true, message: '请输入名称', trigger: 'blur' }]
      }
    }
  },
  computed: {
    filtered() {
      return this.rooms.filter(r => {
        const matchKeyword = !this.query.keyword || r.name.includes(this.query.keyword)
        const matchPrice = r.price >= Number(this.query.min || 0)
        return matchKeyword && matchPrice
      })
    },
    paged() {
      const start = (this.page - 1) * this.pageSize
      return this.filtered.slice(start, start + this.pageSize)
    },
    dialogTitle() {
      return this.form.id ? '编辑房型' : '新增房型'
    }
  },
  created() {
    this.refresh()
  },
  methods: {
    refresh() {
      this.loading = true
      setTimeout(() => {
        this.rooms = list('rooms')
        this.loading = false
      }, 200)
    },
    reset() {
      this.query = { keyword: '', min: 0 }
      this.page = 1
    },
    openDialog(row) {
      this.form = row ? { ...row } : { id: null, name: '', price: 100, capacity: 1 }
      this.dialogVisible = true
    },
    submit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (this.form.id) {
          update('rooms', this.form.id, this.form)
        } else {
          create('rooms', { ...this.form, storeId: 1 })
        }
        this.dialogVisible = false
        this.refresh()
      })
    },
    removeRow(row) {
      this.$confirm('确认删除该房型？', '提示').then(() => {
        remove('rooms', row.id)
        this.refresh()
      })
    }
  }
}
</script>

