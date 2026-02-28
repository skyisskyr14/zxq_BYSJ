<template>
  <div>
    <page-header title="个人中心" desc="管理个人信息与常用入口" />
    <div class="card mt-16">
      <div class="profile">
        <div class="avatar">{{ user.name ? user.name.slice(0, 1) : 'U' }}</div>
        <div>
          <div class="name">{{ user.name || '用户' }}</div>
          <div class="text-muted">{{ user.phone }}</div>
        </div>
      </div>
      <div class="mt-16">
        <el-button type="primary" @click="$router.push('/c/me/profile')">编辑资料</el-button>
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
    }
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
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: #e8efff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 600;
}
.name {
  font-weight: 600;
  font-size: 18px;
}
</style>

