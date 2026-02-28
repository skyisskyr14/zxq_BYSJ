<template>
  <div class="card table-wrap">
    <el-table :data="data" v-loading="loading" style="width: 100%">
      <slot />
    </el-table>
    <div v-if="!loading && (!data || data.length === 0)" class="empty-block">
      <el-empty description="暂无数据" />
    </div>
  </div>
</template>

<script>
export default {
  name: 'DataTable',
  props: {
    data: { type: Array, default: () => [] },
    loading: { type: Boolean, default: false }
  }
}
</script>

<style scoped>
.table-wrap {
  padding: 0;
}
</style>

