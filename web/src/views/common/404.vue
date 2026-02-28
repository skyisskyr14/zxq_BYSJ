<template>
  <div class="page">
    <el-empty description="页面不存在">
      <el-button type="primary" @click="$router.push('/login')">返回登录</el-button>
    </el-empty>
  </div>
</template>

<script>
export default { name: 'NotFound' }
</script>

