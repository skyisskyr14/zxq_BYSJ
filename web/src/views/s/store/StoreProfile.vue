<template>
  <div>
    <page-header title="门店资料" desc="维护门店基础信息" />
    <div class="card mt-16" v-if="store">
      <el-form ref="form" :model="store" :rules="rules" label-width="100px">
        <el-form-item label="门店名称" prop="name">
          <el-input v-model="store.name" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="store.city" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="store.address" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="store.phone" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="save">保存</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'
import { list, update } from '@/mock'

export default {
  name: 'StoreProfile',
  components: { PageHeader },
  data() {
    return {
      store: null,
      rules: {
        name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
        city: [{ required: true, message: '请输入城市', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.store = list('stores')[0]
  },
  methods: {
    save() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        update('stores', this.store.id, this.store)
        this.$message.success('保存成功')
      })
    }
  }
}
</script>

<style scoped>
.card { padding: 20px; }
</style>

