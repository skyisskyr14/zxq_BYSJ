<template>
  <div class="auth-page">
    <div class="bg-orb orb-a"></div>
    <div class="bg-orb orb-b"></div>
    <div class="auth-shell">
      <section class="brand-panel">
        <div class="brand-badge">Pet Boarding Platform</div>
        <h1 class="brand-title">宠物寄养一体化管理</h1>
        <p class="brand-subtitle">用户端、商家端、管理端统一登录入口，流程清晰，演示高效</p>
        <div class="brand-points">
          <div class="point-item">预约与订单状态联动</div>
          <div class="point-item">退款改期全链路可演示</div>
          <div class="point-item">三端权限路由统一管控</div>
        </div>
      </section>

      <section class="form-panel">
        <div class="panel-head">
          <div class="panel-title">欢迎登录</div>
          <div class="panel-desc">请输入账号信息完成身份登录</div>
        </div>

        <el-form ref="form" :model="form" :rules="rules" label-position="top" class="auth-form">
          <el-form-item label="身份" prop="role">
            <el-select v-model="form.role" placeholder="请选择身份" class="w-100">
              <el-option label="用户" value="user" />
              <el-option label="商家" value="merchant" />
              <el-option label="管理" value="admin" />
            </el-select>
          </el-form-item>
          <el-form-item label="账号" prop="username">
            <el-input v-model="form.username" placeholder="请输入账号" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              show-password
              @keyup.enter.native="handleLogin"
            />
          </el-form-item>
          <el-form-item label="验证码" prop="captchaCode">
            <div class="captcha-row">
              <el-input v-model="form.captchaCode" placeholder="请输入验证码" @keyup.enter.native="handleLogin" />
              <button type="button" class="captcha-box" @click="fetchCaptcha">
                <img v-if="captchaImage" :src="captchaImage" alt="captcha" class="captcha-img" />
                <span v-else class="captcha-text">{{ captchaLoading ? '加载中...' : '点击刷新' }}</span>
              </button>
            </div>
          </el-form-item>
          <el-form-item class="submit-item">
            <el-button type="primary" class="w-100 submit-btn" :loading="loading" @click="handleLogin">登录</el-button>
          </el-form-item>
        </el-form>

        <div class="quick-area">
          <el-button size="mini" @click="quick('user')">一键用户</el-button>
          <el-button size="mini" @click="quick('merchant')">一键商家</el-button>
          <el-button size="mini" @click="quick('admin')">一键管理员</el-button>
        </div>
        <div class="link-area">
          <router-link to="/register">用户注册</router-link>
          <router-link to="/register-merchant">商家入驻</router-link>
          <router-link to="/forgot">忘记密码</router-link>
        </div>
      </section>
    </div>
  </div>
</template>

<script>
import { roleHomes } from '@/router/menus'
import { getCaptcha, loginRequest, adminLoginRequest } from '@/api/request/auth'

export default {
  name: 'Login',
  data() {
    return {
      loading: false,
      captchaLoading: false,
      captchaImage: '',
      form: {
        role: 'user',
        username: '12312312312',
        password: 'qwe123123',
        captchaCode: '',
        uuid: ''
      },
      rules: {
        role: [{ required: true, message: '请选择登录身份', trigger: 'change' }],
        username: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
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
          throw new Error(res.message || '获取验证码失败')
        }

        this.captchaImage = 'data:image/png;base64,' + (res.data?.data || '')
        this.form.uuid = res.data?.uuid || ''
      } catch (e) {
        this.captchaImage = ''
        this.form.uuid = ''
        this.$message.error(e.message || '获取验证码出错')
      } finally {
        this.captchaLoading = false
      }
    },
    async handleLogin() {
      this.loading = true
      try {
        const commonPayload = {
          username: this.form.username,
          password: this.form.password,
          captchaUuid: this.form.uuid,
          captchaInput: this.form.captchaCode,
          captchaType: 'image'
        }

        const isAdminLogin = this.form.role === 'admin'
        const payload = isAdminLogin
          ? { ...commonPayload, projectCode: 'zxq' }
          : { ...commonPayload, type: 1, role: this.form.role === "user" ? 2 : 3 }

        const res = isAdminLogin
          ? await adminLoginRequest(payload)
          : await loginRequest(payload)

        if (res.code !== 200) {
          this.$message.warning(res.message || '登录失败')
          this.fetchCaptcha()
          return
        }

        const backendRole = res.data?.role ?? res.data?.type ?? res.data?.userInfo?.role
        let role = this.form.role
        if (backendRole === 1 || backendRole === 'user') role = 'user'
        if (backendRole === 2 || backendRole === 'merchant') role = 'merchant'
        if (backendRole === 3 || backendRole === 'admin') role = 'admin'
        const adminFlagSource = typeof res.data === 'string'
          ? res.data
          : (res.data?.roleName || res.data?.name || res.message || '')
        const superAdmin = role === 'admin' && adminFlagSource === '\u8d85\u7ea7\u7ba1\u7406\u5458'

        this.$store.commit('auth/SET_AUTH', {
          role,
          userInfo: res.data?.userInfo || { username: this.form.username, role },
          permissions: res.data?.permissions || [],
          superAdmin,
          loginMessage: adminFlagSource
        })

        if (role === 'user') {
          try {
            await this.$store.dispatch('auth/fetchUserBaseInfo')
          } catch (err) {
            this.$message.warning(err.message || '已登录，但获取个人基础信息失败')
          }
        }

        this.$message.success('登录成功')
        await this.$router.replace(roleHomes[role] || '/login')
      } catch (e) {
        this.$message.error(e.message || '网络错误')
        this.fetchCaptcha()
      } finally {
        this.loading = false
      }
    },
    async quick(role) {
      this.loading = true
      try {
        await this.$store.dispatch('auth/demoLogin', role)
        this.$message.success('快速登录成功')
        this.$router.push(roleHomes[role])
      } catch (e) {
        this.$message.error(e.message || '请手动登录')
      } finally {
        this.loading = false
      }
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

.quick-area {
  display: flex;
  gap: 0.8vw;
  margin-bottom: 1.2vh;
}

.quick-area :deep(.el-button) {
  border-radius: 0.8vh;
  border-color: #d9e4f2;
  color: #58708e;
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

  .quick-area {
    gap: 1.8vw;
  }
}
</style>



