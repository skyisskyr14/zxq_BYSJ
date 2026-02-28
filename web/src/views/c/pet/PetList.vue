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
          <el-option label="猫" value="猫" />
          <el-option label="狗" value="狗" />
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
      <el-table-column prop="type" label="类型" width="100" />
      <el-table-column prop="age" label="年龄" width="80" />
      <el-table-column prop="weight" label="体重" width="80" />
      <el-table-column label="默认" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isDefault" type="success" size="mini">默认</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="scope">
          <el-button type="text" @click="openDialog(scope.row)">编辑</el-button>
          <el-button type="text" @click="setDefault(scope.row)">设为默认</el-button>
          <el-button type="text" style="color: #f56c6c" @click="removePet(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </data-table>

    <pagination :page="page" :page-size="pageSize" :total="filtered.length" @change="page = $event" @size="pageSize = $event" />

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="420px">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择">
            <el-option label="猫" value="猫" />
            <el-option label="狗" value="狗" />
          </el-select>
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input-number v-model="form.age" :min="1" />
        </el-form-item>
        <el-form-item label="体重" prop="weight">
          <el-input-number v-model="form.weight" :min="1" />
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
  name: 'PetList',
  components: { PageHeader, QueryBar, DataTable, Pagination },
  data() {
    return {
      loading: false,
      pets: [],
      query: { keyword: '', type: '' },
      page: 1,
      pageSize: 6,
      dialogVisible: false,
      form: { id: null, name: '', type: '', age: 1, weight: 1 },
      rules: {
        name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
        type: [{ required: true, message: '请选择类型', trigger: 'change' }]
      }
    }
  },
  computed: {
    filtered() {
      return this.pets.filter(p => {
        const matchKeyword = !this.query.keyword || p.name.includes(this.query.keyword)
        const matchType = !this.query.type || p.type === this.query.type
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
    refresh() {
      this.loading = true
      setTimeout(() => {
        this.pets = list('pets')
        this.loading = false
      }, 200)
    },
    reset() {
      this.query = { keyword: '', type: '' }
      this.page = 1
    },
    openDialog(row) {
      if (row) {
        this.form = { ...row }
      } else {
        this.form = { id: null, name: '', type: '', age: 1, weight: 1 }
      }
      this.dialogVisible = true
    },
    submit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (this.form.id) {
          update('pets', this.form.id, this.form)
        } else {
          const user = this.$store.getters['auth/userInfo']
          create('pets', { ...this.form, userId: user ? user.id : 1, isDefault: false })
        }
        this.dialogVisible = false
        this.refresh()
      })
    },
    removePet(row) {
      this.$confirm('确认删除该宠物？', '提示').then(() => {
        remove('pets', row.id)
        this.refresh()
      })
    },
    setDefault(row) {
      this.pets.forEach(p => update('pets', p.id, { isDefault: false }))
      update('pets', row.id, { isDefault: true })
      this.refresh()
      this.$message.success('已设置为默认宠物')
    }
  }
}
</script>

