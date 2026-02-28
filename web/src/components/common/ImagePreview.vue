<template>
  <div class="image-preview">
    <el-image
      v-for="(url, index) in urls"
      :key="index"
      :src="url"
      :preview-src-list="urls"
      fit="cover"
      class="img"
    />
  </div>
</template>

<script>
export default {
  name: 'ImagePreview',
  props: {
    urls: { type: Array, default: () => [] }
  }
}
</script>

<style scoped>
.image-preview {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.img {
  width: 90px;
  height: 70px;
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid #eef2f7;
}
</style>

