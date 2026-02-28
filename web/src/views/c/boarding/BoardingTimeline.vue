<template>
  <div>
    <page-header title="寄养动态" desc="查看商家发布的实时动态" />
    <div class="card mt-16">
      <el-timeline>
        <el-timeline-item v-for="log in logs" :key="log.id" :timestamp="log.createdAt" placement="top">
          <div class="log">
            <div class="type">{{ log.type }}</div>
            <div class="content">{{ log.content }}</div>
            <image-preview v-if="log.images && log.images.length" :urls="log.images" class="mt-12" />
          </div>
        </el-timeline-item>
      </el-timeline>
      <empty-state v-if="!logs.length" description="暂无动态" />
    </div>
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import ImagePreview from '@/components/common/ImagePreview.vue'
import { list } from '@/mock'

export default {
  name: 'BoardingTimeline',
  components: { PageHeader, EmptyState, ImagePreview },
  data() {
    return { logs: [] }
  },
  created() {
    const orderId = this.$route.params.orderId
    this.logs = list('boardingLogs').filter(log => String(log.orderId) === String(orderId))
  }
}
</script>

<style scoped>
.log {
  background: #f9fbff;
  padding: 12px;
  border-radius: 8px;
}
.type {
  font-weight: 600;
  margin-bottom: 6px;
}
</style>

