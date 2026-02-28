<template>
  <div>
    <page-header title="宠物编辑" desc="维护宠物档案" />
    <div class="card mt-16">
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
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="save">保存</el-button>
          <el-button @click="$router.push('/c/pet/list')">返回</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'
import { createPetRequest, detailPetRequest, updatePetRequest } from '@/api/request/pet'

export default {
  name: 'PetEdit',
  components: { PageHeader },
  data() {
    return {
      saving: false,
      form: { id: null, name: '', type: 1, age: 1, weight: 1 },
      rules: {
        name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
        type: [{ required: true, message: '请选择类型', trigger: 'change' }]
      }
    }
  },
  created() {
    this.init()
  },
  methods: {
    async init() {
      const id = this.$route.params.id
      if (!id) return
      const res = await detailPetRequest(id)
      if (res.code !== 200 || !res.data) {
        this.$message.error(res.message || '宠物不存在')
        this.$router.push('/c/pet/list')
        return
      }
      this.form = {
        id: res.data.id,
        name: res.data.name,
        type: Number(res.data.type || 1),
        age: Number(res.data.age || 1),
        weight: Number(res.data.weight || 1)
      }
    },
    save() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.saving = true
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
          this.$message.success('保存成功')
          this.$router.push('/c/pet/list')
        } catch (e) {
          this.$message.error(e.message || '保存失败')
        } finally {
          this.saving = false
        }
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
