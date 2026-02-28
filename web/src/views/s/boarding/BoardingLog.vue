<template>
  <div>
    <page-header title="发布寄养动态" desc="记录寄养过程并同步给用户">
      <el-button type="primary" @click="dialogVisible = true">发布动态</el-button>
    </page-header>

    <div class="card mt-16">
      <div class="section-title">历史动态</div>
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

    <el-dialog title="发布动态" :visible.sync="dialogVisible" width="520px">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择">
            <el-option label="喂食" value="喂食" />
            <el-option label="遛弯" value="遛弯" />
            <el-option label="清洁" value="清洁" />
            <el-option label="玩耍" value="玩耍" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input type="textarea" v-model="form.content" rows="3" />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            action=""
            list-type="picture-card"
            :file-list="fileList"
            :auto-upload="false"
            :before-upload="beforeUpload"
            :on-remove="handleRemove"
          >
            <i class="el-icon-plus"></i>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">发布</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import ImagePreview from '@/components/common/ImagePreview.vue'
import { addBoardingLog, list } from '@/mock'

export default {
  name: 'BoardingLog',
  components: { PageHeader, EmptyState, ImagePreview },
  data() {
    return {
      dialogVisible: false,
      form: { type: '', content: '', images: [] },
      fileList: [],
      logs: [],
      rules: {
        type: [{ required: true, message: '请选择类型', trigger: 'change' }],
        content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.refresh()
  },
  methods: {
    refresh() {
      const orderId = this.$route.params.orderId
      this.logs = list('boardingLogs').filter(log => String(log.orderId) === String(orderId))
    },
    beforeUpload(file) {
      const reader = new FileReader()
      reader.onload = e => {
        this.form.images.push(e.target.result)
        this.fileList.push({ name: file.name, url: e.target.result })
      }
      reader.readAsDataURL(file)
      return false
    },
    handleRemove(file, fileList) {
      this.fileList = fileList
      this.form.images = fileList.map(item => item.url)
    },
    submit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const orderId = this.$route.params.orderId
        addBoardingLog(orderId, { ...this.form })
        this.$message.success('动态已发布')
        this.form = { type: '', content: '', images: [] }
        this.fileList = []
        this.dialogVisible = false
        this.refresh()
      })
    }
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
