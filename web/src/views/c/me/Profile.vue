<template>
  <div>
    <page-header title="个人信息" desc="查看用户基础信息" />
    <div class="card mt-16">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="昵称">{{ user.nickname || user.name || '未设置' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ user.phone || '未绑定' }}</el-descriptions-item>
        <el-descriptions-item label="头像地址">{{ user.avatar || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ genderText }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ user.createTime || '暂无' }}</el-descriptions-item>
      </el-descriptions>
    </div>
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'

export default {
  name: 'Profile',
  components: { PageHeader },
  computed: {
    user() {
      return this.$store.getters['auth/userInfo'] || {}
    },
    genderText() {
      const gender = Number(this.user.gender)
      if (gender === 1) return '男'
      if (gender === 2) return '女'
      return '未知'
    }
  },
  created() {
    this.$store.dispatch('auth/fetchUserBaseInfo').catch(() => {})
  }
}
</script>
