<template>
  <div class="page">
    <page-header title="找回密码" desc="填写手机号或邮箱，我们将发送重置链接" />
    <div class="card mt-16">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="验证码" prop="code">
          <el-input v-model="form.code" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submit">提交</el-button>
          <el-button @click="$router.push('/login')">返回登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'

export default {
  name: 'Forgot',
  components: { PageHeader },
  data() {
    return {
      form: { phone: '', code: '' },
      rules: {
        phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
        code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
      }
    }
  },
  methods: {
    submit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        this.$message.success('已发送重置链接（模拟）')
        this.$router.push('/login')
      })
    }
  }
}
</script>

<style scoped>
.card {
  padding: 24px;
}
</style>

