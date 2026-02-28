<template>
  <div class="card empty-state">
    <el-empty :description="description" />
  </div>
</template>

<script>
export default {
  name: 'EmptyState',
  props: {
    description: { type: String, default: '暂无数据' }
  }
}
</script>

<style scoped>
.empty-state {
  padding: 24px;
}
</style>

