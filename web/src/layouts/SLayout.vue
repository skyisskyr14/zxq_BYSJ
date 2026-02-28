<template>
  <el-container class="layout">
    <el-aside :width="collapsed ? '64px' : '220px'" class="sidebar">
      <div class="logo" @click="$router.push(home)">
        <span v-if="!collapsed">PetBoarding</span>
        <span v-else>PB</span>
      </div>
      <el-menu
        :default-active="$route.path"
        :collapse="collapsed"
        background-color="#111827"
        text-color="#cbd5f5"
        active-text-color="#ffffff"
        router
      >
        <el-menu-item v-for="item in menus" :key="item.path" :index="item.path">
          <i :class="item.icon"></i>
          <span slot="title">{{ item.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="topbar">
        <div class="left">
          <el-button type="text" icon="el-icon-s-fold" @click="toggle" />
          <el-breadcrumb separator="/">
            <el-breadcrumb-item v-for="(item, idx) in breadcrumbs" :key="idx">{{ item }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="right">
          <span class="badge">{{ roleLabel }}</span>
          <el-dropdown v-if="canSwitchRole" @command="handleSwitch">
            <span class="el-dropdown-link">切换身份<i class="el-icon-arrow-down el-icon--right"></i></span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="user" :disabled="!canSwitchRole && role !== 'user'">用户端</el-dropdown-item>
              <el-dropdown-item command="merchant" :disabled="!canSwitchRole && role !== 'merchant'">商家端</el-dropdown-item>
              <el-dropdown-item command="admin" :disabled="!canSwitchRole && role !== 'admin'">管理端</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <el-button type="text" @click="logout">退出</el-button>
        </div>
      </el-header>
      <el-main class="page">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { mapGetters } from 'vuex'
import { getMenusByRole, roleHomes } from '@/router/menus'

export default {
  name: 'SLayout',
  computed: {
    ...mapGetters('auth', ['role', 'superAdmin']),
    ...mapGetters('app', ['sidebarCollapsed']),
    collapsed() {
      return this.sidebarCollapsed
    },
    menus() {
      return getMenusByRole(this.role)
    },
    roleLabel() {
      return this.role === 'admin' ? '管理员' : this.role === 'merchant' ? '商家' : '用户'
    },
    breadcrumbs() {
      return this.$route.matched.filter(r => r.meta && r.meta.title).map(r => r.meta.title)
    },
    home() {
      return roleHomes[this.role] || '/login'
    },
    canSwitchRole() {
      return !!this.superAdmin
    }
  },
  methods: {
    toggle() {
      this.$store.commit('app/TOGGLE_SIDEBAR')
    },
    logout() {
      this.$store.dispatch('auth/logout')
      this.$router.push('/login')
    },
    handleSwitch(role) {
      if (!this.canSwitchRole && role !== this.role) {
        this.$message.warning('仅超级管理员可切换其他身份')
        return
      }
      if (role === this.role) {
        this.$router.push(roleHomes[role])
        return
      }
      this.$store.dispatch('auth/switchRole', role)
      this.$router.push(roleHomes[role])
    }
  }
}
</script>

<style scoped>
.logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  color: #ffffff;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}
</style>

