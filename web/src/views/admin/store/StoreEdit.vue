<template>
  <div>
    <page-header title="门店编辑" desc="新增或编辑门店" />
    <div class="card mt-16">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="门店名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="form.city" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" />
        </el-form-item>
        <el-form-item label="评分" prop="rating">
          <el-input-number v-model="form.rating" :min="1" :max="5" :step="0.1" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="save">保存</el-button>
          <el-button @click="$router.push('/admin/store/list')">返回</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'
import { findById, update, create } from '@/mock'

export default {
  name: 'StoreEdit',
  components: { PageHeader },
  data() {
    return {
      form: { id: null, name: '', city: '', address: '', rating: 4.5 },
      rules: {
        name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
        city: [{ required: true, message: '请输入城市', trigger: 'blur' }]
      }
    }
  },
  created() {
    const id = this.$route.params.id
    if (id) {
      const store = findById('stores', id)
      if (store) this.form = { ...store }
    }
  },
  methods: {
    save() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (this.form.id) {
          update('stores', this.form.id, this.form)
        } else {
          create('stores', { ...this.form, cover: `https://picsum.photos/seed/store${Date.now()}/640/360` })
        }
        this.$message.success('保存成功')
        this.$router.push('/admin/store/list')
      })
    }
  }
}
</script>

<style scoped>
.card { padding: 20px; }
</style>

