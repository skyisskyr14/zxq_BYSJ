<template>
  <div class="page">
    <el-empty description="无权限访问">
      <el-button type="primary" @click="$router.push('/login')">返回登录</el-button>
    </el-empty>
  </div>
</template>

<script>
export default { name: 'NoAuth' }
</script>

