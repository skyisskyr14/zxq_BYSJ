<template>
  <div class="page-header card">
    <div>
      <div class="title">{{ title }}</div>
      <div v-if="desc" class="desc">{{ desc }}</div>
    </div>
    <div class="actions">
      <slot />
    </div>
  </div>
</template>

<script>
export default {
  name: 'PageHeader',
  props: {
    title: { type: String, required: true },
    desc: { type: String, default: '' }
  }
}
</script>

<style scoped>
.page-header {
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.title {
  font-size: 20px;
  font-weight: 600;
}
.desc {
  margin-top: 6px;
  color: #8c98a4;
  font-size: 12px;
}
.actions > * + * {
  margin-left: 8px;
}
</style>

