<template>
  <div class="card query-bar">
    <el-form :inline="inline" :model="model" class="form-inline">
      <slot />
    </el-form>
  </div>
</template>

<script>
export default {
  name: 'QueryBar',
  props: {
    model: { type: Object, required: true },
    inline: { type: Boolean, default: true }
  }
}
</script>

<style scoped>
.query-bar {
  padding: 12px 16px;
}
</style>

