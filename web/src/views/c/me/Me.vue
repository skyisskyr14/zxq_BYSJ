<template>
  <div>
    <page-header title="个人中心" desc="管理个人信息与常用入口" />
    <div class="card mt-16">
      <div class="profile">
        <el-avatar class="avatar" :size="64" :src="user.avatar" icon="el-icon-user-solid">{{ displayName ? displayName.slice(0, 1) : 'U' }}</el-avatar>
        <div>
          <div class="name">{{ displayName || '用户' }}</div>
          <div class="text-muted">{{ user.phone }}</div>
        </div>
      </div>
      <div class="mt-16">
        <el-button type="primary" @click="$router.push('/c/me/profile')">个人信息</el-button>
        <el-button @click="$router.push('/c/order/list')">我的订单</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'

export default {
  name: 'Me',
  components: { PageHeader },
  computed: {
    user() {
      return this.$store.getters['auth/userInfo'] || {}
    },
    displayName() {
      return this.user.nickname || this.user.name || this.user.username || ''
    }
  },
  created() {
    this.$store.dispatch('auth/fetchUserBaseInfo').catch(() => {})
  }
}
</script>

<style scoped>
.profile {
  display: flex;
  align-items: center;
  gap: 12px;
}
.avatar {
  background: #e8efff;
  color: #2563eb;
  font-size: 24px;
  font-weight: 600;
}
.name {
  font-weight: 600;
  font-size: 18px;
}
</style>

