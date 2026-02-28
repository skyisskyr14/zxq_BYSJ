<template>
  <div>
    <page-header title="宠物编辑" desc="维护宠物档案" />
    <div class="card mt-16">
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
        <el-form-item>
          <el-button type="primary" @click="save">保存</el-button>
          <el-button @click="$router.push('/c/pet/list')">返回</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'
import { findById, update } from '@/mock'

export default {
  name: 'PetEdit',
  components: { PageHeader },
  data() {
    return {
      form: { id: null, name: '', type: '', age: 1, weight: 1 },
      rules: {
        name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
        type: [{ required: true, message: '请选择类型', trigger: 'change' }]
      }
    }
  },
  created() {
    const id = this.$route.params.id
    if (id) {
      const pet = findById('pets', id)
      if (pet) this.form = { ...pet }
    }
  },
  methods: {
    save() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        update('pets', this.form.id, this.form)
        this.$message.success('保存成功')
        this.$router.push('/c/pet/list')
      })
    }
  }
}
</script>

<style scoped>
.card {
  padding: 20px;
}
</style>

