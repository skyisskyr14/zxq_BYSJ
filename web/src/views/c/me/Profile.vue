<template>
  <div>
    <page-header title="资料设置" desc="更新个人资料" />
    <div class="card mt-16">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
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

export default {
  name: 'Profile',
  components: { PageHeader },
  data() {
    const user = this.$store.getters['auth/userInfo'] || {}
    return {
      form: { name: user.name || '', phone: user.phone || '' },
      rules: {
        name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
      }
    }
  },
  methods: {
    save() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        this.$message.success('保存成功（模拟）')
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

