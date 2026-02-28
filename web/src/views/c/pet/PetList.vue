<template>
  <div>
    <page-header title="宠物管理" desc="维护宠物档案" />
    <query-bar :model="query" class="mt-16">
      <el-form-item label="关键词">
        <el-input v-model="query.keyword" placeholder="宠物名" />
      </el-form-item>
      <el-form-item label="类型">
        <el-select v-model="query.type" placeholder="全部">
          <el-option label="全部" value="" />
          <el-option label="猫" value="1" />
          <el-option label="狗" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="page = 1">查询</el-button>
        <el-button @click="reset">重置</el-button>
        <el-button type="success" @click="openDialog()">新增</el-button>
      </el-form-item>
    </query-bar>

    <data-table :data="paged" :loading="loading" class="mt-16">
      <el-table-column prop="name" label="名称" />
      <el-table-column label="类型" width="100">
        <template slot-scope="scope">{{ petTypeText(scope.row.type) }}</template>
      </el-table-column>
      <el-table-column prop="age" label="年龄(月)" width="100" />
      <el-table-column prop="weight" label="体重(kg)" width="100" />
      <el-table-column label="操作" width="150">
        <template slot-scope="scope">
          <el-button type="text" @click="openDialog(scope.row)">编辑</el-button>
          <el-button type="text" style="color: #f56c6c" @click="removePet(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </data-table>

    <pagination :page="page" :page-size="pageSize" :total="filtered.length" @change="page = $event" @size="pageSize = $event" />

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="420px">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model.trim="form.name" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择">
            <el-option label="猫" :value="1" />
            <el-option label="狗" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input-number v-model="form.age" :min="1" :precision="1" :step="0.5" />
        </el-form-item>
        <el-form-item label="体重" prop="weight">
          <el-input-number v-model="form.weight" :min="0.1" :precision="1" :step="0.1" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submit">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'
import QueryBar from '@/components/common/QueryBar.vue'
import DataTable from '@/components/common/DataTable.vue'
import Pagination from '@/components/common/Pagination.vue'
import { createPetRequest, deletePetRequest, listPetRequest, updatePetRequest } from '@/api/request/pet'

export default {
  name: 'PetList',
  components: { PageHeader, QueryBar, DataTable, Pagination },
  data() {
    return {
      loading: false,
      submitLoading: false,
      pets: [],
      query: { keyword: '', type: '' },
      page: 1,
      pageSize: 6,
      dialogVisible: false,
      form: { id: null, name: '', type: 1, age: 1, weight: 1 },
      rules: {
        name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
        type: [{ required: true, message: '请选择类型', trigger: 'change' }]
      }
    }
  },
  computed: {
    filtered() {
      return this.pets.filter(p => {
        const matchKeyword = !this.query.keyword || (p.name || '').includes(this.query.keyword)
        const matchType = !this.query.type || String(p.type) === this.query.type
        return matchKeyword && matchType
      })
    },
    paged() {
      const start = (this.page - 1) * this.pageSize
      return this.filtered.slice(start, start + this.pageSize)
    },
    dialogTitle() {
      return this.form.id ? '编辑宠物' : '新增宠物'
    }
  },
  created() {
    this.refresh()
  },
  methods: {
    petTypeText(type) {
      return Number(type) === 1 ? '猫' : Number(type) === 2 ? '狗' : '未知'
    },
    async refresh() {
      this.loading = true
      try {
        const res = await listPetRequest()
        if (res.code !== 200) {
          throw new Error(res.message || '获取宠物列表失败')
        }
        this.pets = res.data || []
      } catch (e) {
        this.$message.error(e.message || '获取宠物列表失败')
      } finally {
        this.loading = false
      }
    },
    reset() {
      this.query = { keyword: '', type: '' }
      this.page = 1
    },
    openDialog(row) {
      if (row) {
        this.form = {
          id: row.id,
          name: row.name,
          type: Number(row.type || 1),
          age: Number(row.age || 1),
          weight: Number(row.weight || 1)
        }
      } else {
        this.form = { id: null, name: '', type: 1, age: 1, weight: 1 }
      }
      this.dialogVisible = true
    },
    submit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.submitLoading = true
        try {
          const payload = {
            id: this.form.id,
            name: this.form.name,
            type: Number(this.form.type),
            age: Number(this.form.age),
            weight: Number(this.form.weight)
          }
          const res = this.form.id ? await updatePetRequest(payload) : await createPetRequest(payload)
          if (res.code !== 200) {
            throw new Error(res.message || '保存失败')
          }
          this.$message.success(this.form.id ? '修改成功' : '创建成功')
          this.dialogVisible = false
          await this.refresh()
        } catch (e) {
          this.$message.error(e.message || '保存失败')
        } finally {
          this.submitLoading = false
        }
      })
    },
    removePet(row) {
      this.$confirm('确认删除该宠物？', '提示').then(async () => {
        const res = await deletePetRequest(row.id)
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
