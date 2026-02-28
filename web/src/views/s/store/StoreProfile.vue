<template>
  <div>
    <page-header title="门店资料" desc="维护门店基础信息（包含评分与门店图片）" />
    <div class="card mt-16">
      <el-form ref="form" :model="store" :rules="rules" label-width="100px">
        <el-form-item label="门店名称" prop="name">
          <el-input v-model.trim="store.name" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model.trim="store.city" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model.trim="store.address" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model.trim="store.phone" />
        </el-form-item>
        <el-form-item label="评分" prop="score">
          <el-rate v-model="store.score" :max="5" show-score />
        </el-form-item>
        <el-form-item label="门店介绍">
          <el-input v-model.trim="store.intro" type="textarea" :rows="3" placeholder="可选" />
        </el-form-item>
        <el-form-item label="门店图片">
          <el-upload
            :show-file-list="false"
            :http-request="handleStoreImagesUpload"
            :before-upload="beforeImageUpload"
            multiple
            accept="image/*"
          >
            <el-button size="small" type="primary" :loading="uploading">上传图片（可多选）</el-button>
          </el-upload>
          <div class="img-list" v-if="store.images.length">
            <div v-for="(img, idx) in store.images" :key="img + idx" class="img-item">
              <el-image :src="img" fit="cover" />
              <el-button type="danger" icon="el-icon-delete" size="mini" circle @click="removeImage(idx)" />
            </div>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="save">{{ hasStore ? '保存修改' : '创建门店' }}</el-button>
          <el-button :loading="loading" @click="loadStore">刷新</el-button>
          <el-button type="danger" plain :disabled="!hasStore" :loading="deleting" @click="removeStore">删除</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'
import {
  createShopStoreRequest,
  deleteShopStoreRequest,
  getShopStoreDetailRequest,
  updateShopStoreRequest,
  uploadShopStoreImagesRequest
} from '@/api/request/shop'

const defaultStore = () => ({
  name: '',
  city: '',
  address: '',
  phone: '',
  score: 5,
  intro: '',
  images: []
})

export default {
  name: 'StoreProfile',
  components: { PageHeader },
  data() {
    return {
      loading: false,
      saving: false,
      deleting: false,
      uploading: false,
      hasStore: false,
      store: defaultStore(),
      rules: {
        name: [{ required: true, message: '请输入门店名称', trigger: 'blur' }],
        city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
        address: [{ required: true, message: '请输入地址', trigger: 'blur' }],
        score: [{ required: true, message: '请选择评分', trigger: 'change' }]
      }
    }
  },
  created() {
    this.loadStore()
  },
  methods: {
    async loadStore() {
      this.loading = true
      try {
        const res = await getShopStoreDetailRequest()
        if (res.code !== 200 || !res.data) {
          this.hasStore = false
          this.store = defaultStore()
          return
        }
        this.hasStore = true
        this.store = {
          name: res.data.name || '',
          city: res.data.city || '',
          address: res.data.address || '',
          phone: res.data.phone || '',
          score: Number(res.data.score || 5),
          intro: res.data.intro || '',
          images: Array.isArray(res.data.images) ? res.data.images : []
        }
      } finally {
        this.loading = false
      }
    },
    beforeImageUpload(file) {
      const ok = file.type.startsWith('image/')
      if (!ok) this.$message.error('只能上传图片')
      return ok
    },
    async handleStoreImagesUpload(option) {
      this.uploading = true
      try {
        const files = option.file ? [option.file] : []
        const res = await uploadShopStoreImagesRequest(files)
        const urls = res?.data?.images || []
        if (res.code !== 200 || !urls.length) throw new Error(res.message || '上传失败')
        this.store.images = [...this.store.images, ...urls]
        this.$message.success('图片上传成功')
        option.onSuccess && option.onSuccess(res)
      } catch (e) {
        this.$message.error(e.message || '上传失败')
        option.onError && option.onError(e)
      } finally {
        this.uploading = false
      }
    },
    removeImage(index) {
      this.store.images.splice(index, 1)
    },
    save() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.saving = true
        try {
          const payload = { ...this.store }
          const api = this.hasStore ? updateShopStoreRequest : createShopStoreRequest
          const res = await api(payload)
          if (res.code !== 200) throw new Error(res.message || '保存失败')
          this.$message.success(this.hasStore ? '更新成功' : '创建成功')
          await this.loadStore()
        } catch (e) {
          this.$message.error(e.message || '保存失败')
        } finally {
          this.saving = false
        }
      })
    },
    removeStore() {
      this.$confirm('确定删除门店配置吗？', '提示').then(async () => {
        this.deleting = true
        try {
          const res = await deleteShopStoreRequest()
          if (res.code !== 200) throw new Error(res.message || '删除失败')
          this.$message.success('删除成功')
          await this.loadStore()
        } catch (e) {
          this.$message.error(e.message || '删除失败')
        } finally {
          this.deleting = false
        }
      })
    }
  }
}
</script>

<style scoped>
.card {
  padding: 20px;
}

.img-list {
  margin-top: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.img-item {
  position: relative;
  width: 110px;
  height: 110px;
}

.img-item .el-image {
  width: 100%;
  height: 100%;
  border-radius: 6px;
  border: 1px solid #ebeef5;
}

.img-item .el-button {
  position: absolute;
  right: -8px;
  top: -8px;
}
</style>
