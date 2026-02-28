<template>
  <div>
    <page-header title="商家信息" desc="商家基础信息的增删改查" />
    <div class="card mt-16">
      <div class="avatar-row">
        <el-avatar :size="72" :src="form.avatar" icon="el-icon-user-solid" />
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px" class="mt-16">
        <el-form-item label="手机号">
          <el-input :value="user.phone || ''" disabled />
        </el-form-item>
        <el-form-item label="商家名称" prop="realname">
          <el-input v-model.trim="form.realname" placeholder="请输入商家名称" />
        </el-form-item>
        <el-form-item label="头像URL" prop="avatar">
          <el-input v-model.trim="form.avatar" placeholder="请输入头像地址（可选）" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio :label="0">未知</el-radio>
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="form.age" :min="0" :max="120" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="save">{{ hasInfo ? '更新' : '新增' }}</el-button>
          <el-button :loading="loading" @click="refresh">刷新</el-button>
          <el-button type="danger" plain :disabled="!hasInfo" :loading="deleting" @click="remove">删除</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'
import { createShopBaseInfoRequest, deleteShopBaseInfoRequest, getShopBaseInfoRequest, updateShopBaseInfoRequest } from '@/api/request/shop'

export default {
  name: 'ShopProfile',
  components: { PageHeader },
  data() {
    return {
      loading: false,
      saving: false,
      deleting: false,
      hasInfo: false,
      form: {
        realname: '',
        avatar: '',
        gender: 0,
        age: 0
      },
      rules: {
        realname: [{ required: true, message: '请输入商家名称', trigger: 'blur' }]
      }
    }
  },
  computed: {
    user() {
      return this.$store.getters['auth/userInfo'] || {}
    }
  },
  created() {
    this.refresh()
  },
  methods: {
    async refresh() {
      this.loading = true
      try {
        const res = await getShopBaseInfoRequest()
        if (res.code !== 200 || !res.data) {
          this.hasInfo = false
          this.form = { realname: '', avatar: '', gender: 0, age: 0 }
        } else {
          this.hasInfo = true
          this.form = {
            realname: res.data.realname || '',
            avatar: res.data.avatar || '',
            gender: Number(res.data.gender ?? 0),
            age: Number(res.data.age ?? 0)
          }
        }
      } finally {
        this.loading = false
      }
      this.$store.dispatch('auth/fetchShopBaseInfo').catch(() => {})
    },
    save() {
      this.$refs.formRef.validate(async valid => {
        if (!valid) return
        this.saving = true
        try {
          const api = this.hasInfo ? updateShopBaseInfoRequest : createShopBaseInfoRequest
          const res = await api(this.form)
          if (res.code !== 200) throw new Error(res.message || '保存失败')
          this.$message.success(this.hasInfo ? '更新成功' : '新增成功')
          await this.refresh()
        } catch (e) {
          this.$message.error(e.message || '保存失败')
        } finally {
          this.saving = false
        }
      })
    },
    remove() {
      this.$confirm('确认删除当前商家信息？', '提示').then(async () => {
        this.deleting = true
        try {
          const res = await deleteShopBaseInfoRequest()
          if (res.code !== 200) throw new Error(res.message || '删除失败')
          this.$message.success('删除成功')
          await this.refresh()
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
.avatar-row {
  display: flex;
  align-items: center;
  gap: 12px;
}
</style>
