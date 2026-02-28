<template>
  <div>
    <page-header title="订单评价" desc="分享寄养体验" />
    <div class="card mt-16">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="评分" prop="score">
          <el-rate v-model="form.score" />
        </el-form-item>
        <el-form-item label="评价" prop="content">
          <el-input type="textarea" v-model="form.content" rows="4" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submit">提交评价</el-button>
          <el-button @click="$router.push('/c/order/list')">返回订单</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'

export default {
  name: 'Review',
  components: { PageHeader },
  data() {
    return {
      form: { score: 5, content: '' },
      rules: {
        score: [{ required: true, message: '请选择评分', trigger: 'change' }],
        content: [{ required: true, message: '请输入评价', trigger: 'blur' }]
      }
    }
  },
  methods: {
    submit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        this.$message.success('评价已提交（模拟）')
        this.$router.push('/c/order/list')
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

