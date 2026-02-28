<template>
  <div class="auth-page">
    <div class="bg-orb orb-a"></div>
    <div class="bg-orb orb-b"></div>
    <div class="auth-shell">
      <section class="brand-panel">
        <div class="brand-badge">Merchant Onboarding</div>
        <h1 class="brand-title">商家入驻注册</h1>
        <p class="brand-subtitle">提交商家账号后即可登录商家端，继续完善门店资料与服务配置。</p>
        <div class="brand-points">
          <div class="point-item">手机号作为登录用户名</div>
          <div class="point-item">验证码校验保障账号安全</div>
          <div class="point-item">注册后请先完成店铺设置</div>
        </div>
      </section>

      <section class="form-panel">
        <div class="panel-head">
          <div class="panel-title">商家入驻</div>
          <div class="panel-desc">注册完成后登录商家端进行店铺设置</div>
          <el-alert
            title="请商家注册完登录后进行店铺设置"
            type="info"
            :closable="false"
            show-icon
            class="tip-alert"
          />
        </div>

        <el-form ref="form" :model="form" :rules="rules" label-position="top" class="auth-form">
          <el-form-item label="手机号" prop="username">
            <el-input v-model.trim="form.username" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="真实姓名" prop="nickname">
            <el-input v-model.trim="form.nickname" placeholder="请输入商家真实姓名" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="form.confirmPassword"
              type="password"
              show-password
              placeholder="请再次输入密码"
              @keyup.enter.native="submit"
            />
          </el-form-item>
          <el-form-item label="验证码" prop="captchaCode">
            <div class="captcha-row">
              <el-input v-model="form.captchaCode" placeholder="请输入验证码" @keyup.enter.native="submit" />
              <button type="button" class="captcha-box" @click="fetchCaptcha">
                <img v-if="captchaImage" :src="captchaImage" alt="captcha" class="captcha-img" />
                <span v-else class="captcha-text">{{ captchaLoading ? '加载中...' : '点击刷新' }}</span>
              </button>
            </div>
          </el-form-item>
          <el-form-item class="submit-item">
            <el-button type="primary" class="w-100 submit-btn" :loading="loading" @click="submit">立即注册</el-button>
          </el-form-item>
        </el-form>

        <div class="link-area two-links">
          <router-link to="/login">返回登录</router-link>
          <router-link to="/register">用户注册</router-link>
        </div>
      </section>
    </div>
  </div>
</template>

<script>
import { getCaptcha, registerMerchantRequest } from '@/api/request/auth'

const phoneReg = /^1\d{10}$/

export default {
  name: 'RegisterMerchant',
  data() {
    const validateConfirm = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请再次输入密码'))
        return
      }
      if (value !== this.form.password) {
        callback(new Error('两次密码不一致'))
        return
      }
      callback()
    }

    return {
      loading: false,
      captchaLoading: false,
      captchaImage: '',
      form: {
        username: '',
        nickname: '',
        password: '',
        confirmPassword: '',
        captchaCode: '',
        captchaKey: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: phoneReg, message: '手机号格式不正确', trigger: 'blur' }
        ],
        nickname: [{ required: true, message: '请输入商家昵称', trigger: 'blur' }],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度为 6-20 位', trigger: 'blur' }
        ],
        confirmPassword: [{ validator: validateConfirm, trigger: 'blur' }],
        captchaCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.fetchCaptcha()
  },
  methods: {
    async fetchCaptcha() {
      this.captchaLoading = true
      try {
        const res = await getCaptcha('image')
        if (res && typeof res.code !== 'undefined' && res.code !== 200) {
          throw new Error(res.message || '验证码获取失败')
        }
        this.captchaImage = 'data:image/png;base64,' + (res.data?.data || '')
        this.form.captchaKey = res.data?.uuid || ''
      } catch (e) {
        this.captchaImage = ''
        this.form.captchaKey = ''
        this.$message.error(e.message || '验证码获取失败')
      } finally {
        this.captchaLoading = false
      }
    },
    async submit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return

        this.loading = true
        const payload = {
          phone: this.form.username,
          nickname: this.form.nickname,
          password: this.form.password,
          uuid: this.form.captchaKey,
          captcha: this.form.captchaCode,
          captchaType: 'image'
        }

        try {
          const res = await registerMerchantRequest(payload)
          if (res.code === 200) {
            this.$message.success(res.message || '注册成功，请登录后进行店铺设置')
            this.$router.push('/login')
          } else {
            this.$message.warning(res.message || '注册失败')
            this.fetchCaptcha()
          }
        } catch (e) {
          this.$message.error(e.message || '网络异常')
          this.fetchCaptcha()
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.auth-page {
  position: relative;
  height: 100vh;
  width: 100vw;
  overflow: hidden;
  background: linear-gradient(140deg, #f4f8ff 0%, #eef4ff 55%, #f9fcff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.bg-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(0.2vh);
  opacity: 0.5;
  pointer-events: none;
}

.orb-a {
  width: 34vw;
  height: 34vw;
  max-width: 36vh;
  max-height: 36vh;
  left: 8vw;
  top: -8vh;
  background: radial-gradient(circle, #d8e8ff 0%, rgba(216, 232, 255, 0) 72%);
}

.orb-b {
  width: 40vw;
  height: 40vw;
  max-width: 42vh;
  max-height: 42vh;
  right: -10vw;
  bottom: -12vh;
  background: radial-gradient(circle, #dff4ef 0%, rgba(223, 244, 239, 0) 72%);
}

.auth-shell {
  position: relative;
  z-index: 1;
  width: 92vw;
  max-width: 126vh;
  height: 78vh;
  min-height: 78vh;
  max-height: 78vh;
  border-radius: 2vh;
  overflow: hidden;
  display: grid;
  grid-template-columns: 1.05fr 0.95fr;
  box-shadow: 0 2.6vh 6vh rgba(20, 39, 74, 0.14);
  border: 0.1vh solid rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(0.4vh);
}

.brand-panel {
  padding: 8vh 4.5vw;
  background: linear-gradient(170deg, #2f6fb5 0%, #3b82c6 52%, #4c9fd3 100%);
  color: #f3f8ff;
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-height: 0;
  overflow: hidden;
}

.brand-badge {
  width: fit-content;
  padding: 0.6vh 1.1vw;
  border-radius: 2vh;
  border: 0.1vh solid rgba(243, 248, 255, 0.45);
  font-size: min(1.55vh, 2.4vw);
  margin-bottom: 2vh;
  letter-spacing: 0.04em;
}

.brand-title {
  margin: 0;
  font-size: min(4.1vh, 6vw);
  line-height: 1.28;
  font-weight: 600;
}

.brand-subtitle {
  margin: 2vh 0 3vh;
  font-size: min(2vh, 3.1vw);
  line-height: 1.75;
  color: rgba(243, 248, 255, 0.9);
}

.brand-points {
  display: grid;
  gap: 1.2vh;
}

.point-item {
  padding: 1.15vh 1vw;
  border-radius: 1.1vh;
  background: rgba(255, 255, 255, 0.16);
  border: 0.1vh solid rgba(255, 255, 255, 0.22);
  font-size: min(1.75vh, 2.8vw);
}

.form-panel {
  background: rgba(255, 255, 255, 0.96);
  padding: 5.4vh 3.2vw 3.6vh;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: stretch;
  overflow-y: auto;
  scrollbar-width: thin;
  min-height: 0;
  -webkit-overflow-scrolling: touch;
}

.form-panel::-webkit-scrollbar {
  width: 0.6vh;
}

.form-panel::-webkit-scrollbar-thumb {
  background: #cfdbeb;
  border-radius: 999px;
}

.auth-form {
  flex: 0 0 auto;
}

.panel-head {
  margin-bottom: 2.2vh;
}

.panel-title {
  font-size: min(3.6vh, 5vw);
  font-weight: 600;
  color: #1f2f46;
}

.panel-desc {
  margin-top: 0.7vh;
  font-size: min(1.7vh, 2.6vw);
  color: #6c7f99;
}

.tip-alert {
  margin-top: 1.2vh;
  border-radius: 0.8vh;
}

.auth-form :deep(.el-form-item__label) {
  line-height: 1.5;
  padding-bottom: 0.7vh;
  font-size: min(1.65vh, 2.6vw);
  color: #3e4f67;
}

.auth-form :deep(.el-input__inner),
.auth-form :deep(.el-select .el-input__inner) {
  height: 5.2vh;
  line-height: 5.2vh;
  border-radius: 1vh;
}

.captcha-row {
  display: flex;
  gap: 0.8vw;
}

.captcha-box {
  width: 28%;
  min-width: 10vw;
  max-width: 14vh;
  height: 5.2vh;
  border: 0.1vh solid #dbe6f4;
  border-radius: 1vh;
  overflow: hidden;
  cursor: pointer;
  background: #f5f9ff;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.captcha-box:hover {
  border-color: #7eaad6;
}

.captcha-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.captcha-text {
  font-size: min(1.45vh, 2.2vw);
  color: #8ba0bb;
}

.submit-item {
  margin-top: 0.8vh;
  margin-bottom: 1vh;
}

.submit-btn {
  height: 5.2vh;
  border-radius: 1vh;
  font-size: min(1.75vh, 2.8vw);
  letter-spacing: 0.02em;
  background: linear-gradient(90deg, #2f72bc 0%, #3e86cd 100%);
  border-color: transparent;
}

.link-area {
  display: flex;
  justify-content: space-between;
  font-size: min(1.5vh, 2.2vw);
}

.link-area a {
  color: #5f7898;
}

.link-area a:hover {
  color: #2f72bc;
}

.two-links {
  margin-top: 0.2vh;
}

@media (max-width: 900px) {
  .auth-shell {
    grid-template-columns: 1fr;
    grid-template-rows: 24vh 1fr;
    width: 94vw;
    height: 94vh;
    min-height: 94vh;
    max-height: 94vh;
  }

  .brand-panel {
    padding: 2.6vh 6vw 1.8vh;
    justify-content: flex-start;
  }

  .brand-subtitle {
    margin: 1vh 0 0;
    line-height: 1.55;
  }

  .brand-points {
    display: none;
  }

  .form-panel {
    padding: 2vh 6vw 3.2vh;
    min-height: 0;
  }
}
</style>
