<template>
  <div>
    <page-header title="个人信息" desc="查看并编辑用户基础信息" />
    <div class="card mt-16">
      <div class="avatar-row">
        <el-avatar :size="80" :src="form.avatar" icon="el-icon-user-solid" />
        <el-upload
          action=""
          :show-file-list="false"
          :http-request="handleAvatarUpload"
          accept="image/*"
        >
          <el-button size="small" type="primary" :loading="avatarUploading">重新上传头像</el-button>
        </el-upload>
      </div>

      <el-form ref="form" :model="form" :rules="rules" label-width="90px" class="mt-16">
        <el-form-item label="手机号">
          <el-input :value="user.phone || ''" disabled />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model.trim="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio :label="0">未知</el-radio>
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="save">保存</el-button>
          <el-button @click="refreshUserInfo">刷新信息</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'
import { updateUserBaseInfoRequest, uploadUserAvatarRequest } from '@/api/request/user'

export default {
  name: 'Profile',
  components: { PageHeader },
  data() {
    return {
      saving: false,
      avatarUploading: false,
      form: {
        nickname: '',
        gender: 0,
        avatar: ''
      },
      rules: {
        nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
      }
    }
  },
  computed: {
    user() {
      return this.$store.getters['auth/userInfo'] || {}
    }
  },
  created() {
    this.refreshUserInfo()
  },
  methods: {
    syncForm() {
      this.form.nickname = this.user.nickname || this.user.name || ''
      this.form.gender = Number(this.user.gender ?? 0)
      this.form.avatar = this.user.avatar || ''
    },
    async refreshUserInfo() {
      try {
        await this.$store.dispatch('auth/fetchUserBaseInfo')
      } finally {
        this.syncForm()
      }
    },
    async handleAvatarUpload(option) {
      this.avatarUploading = true
      try {
        const res = await uploadUserAvatarRequest(option.file)
        if (res.code !== 200) {
          throw new Error(res.message || '头像上传失败')
        }
        this.form.avatar = res.data?.avatar || ''
        await this.$store.dispatch('auth/fetchUserBaseInfo')
        this.syncForm()
        this.$message.success('头像上传成功')
      } catch (e) {
        this.$message.error(e.message || '头像上传失败')
      } finally {
        this.avatarUploading = false
      }
    },
    save() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.saving = true
        try {
          const res = await updateUserBaseInfoRequest({
            nickname: this.form.nickname,
            avatar: this.form.avatar,
            gender: this.form.gender
          })
          if (res.code !== 200) {
            throw new Error(res.message || '保存失败')
          }
          await this.$store.dispatch('auth/fetchUserBaseInfo')
          this.syncForm()
          this.$message.success('保存成功')
        } catch (e) {
          this.$message.error(e.message || '保存失败')
        } finally {
          this.saving = false
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
  gap: 16px;
}
</style>
