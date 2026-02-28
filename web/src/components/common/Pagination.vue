<template>
  <div class="card pagination">
    <el-pagination
      background
      layout="prev, pager, next, sizes, total"
      :current-page="page"
      :page-sizes="[5, 10, 20, 50]"
      :page-size="pageSize"
      :total="total"
      @current-change="$emit('change', $event)"
      @size-change="$emit('size', $event)"
    />
  </div>
</template>

<script>
export default {
  name: 'Pagination',
  props: {
    page: { type: Number, default: 1 },
    pageSize: { type: Number, default: 10 },
    total: { type: Number, default: 0 }
  }
}
</script>

<style scoped>
.pagination {
  padding: 12px 16px;
  display: flex;
  justify-content: flex-end;
}
</style>

