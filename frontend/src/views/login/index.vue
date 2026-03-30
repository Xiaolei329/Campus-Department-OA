<template>
  <div class="login-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <h2>在线办公系统登录</h2>
        </div>
      </template>
      <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" label-width="80px" :hide-required-asterisk="true">
        <el-form-item label="账号" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入账号"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="loginForm.password" placeholder="请输入密码" @keyup.enter="handleLogin"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button class="btn-login" type="primary" style="width: 100%" @click="handleLogin" :loading="loading">
            登录系统
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import useUserStore from '@/store/user'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()

const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = () => {
  loginFormRef.value.validate((valid) => {
    if (valid) {
      loading.value = true
      userStore.login(loginForm).then(() => {
        ElMessage.success('登录成功')
        const redirect = route.query.redirect || '/'
        router.push(redirect)
      }).catch(() => {
        loading.value = false
      })
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: url('/images/background.png') no-repeat center center, linear-gradient(135deg, #0f172a 0%, #1e1b4b 100%);
  background-size: cover;
  position: relative;
  overflow: hidden;
}

.login-container::before, .login-container::after {
  content: '';
  position: absolute;
  width: 400px;
  height: 400px;
  border-radius: 50%;
  filter: blur(80px);
  z-index: 0;
  animation: float 10s infinite ease-in-out alternate;
}
.login-container::before {
  background: rgba(99, 102, 241, 0.4);
  top: -100px;
  left: -100px;
}
.login-container::after {
  background: rgba(236, 72, 153, 0.4);
  bottom: -100px;
  right: -100px;
  animation-delay: -5s;
}
@keyframes float {
  0% { transform: translate(0, 0) scale(1); }
  100% { transform: translate(100px, 50px) scale(1.2); }
}

.box-card {
  width: 440px;
  padding: 30px 20px;
  background: rgba(255, 255, 255, 0.1) !important;
  -webkit-backdrop-filter: blur(20px) saturate(180%) !important;
  backdrop-filter: blur(20px) saturate(180%) !important;
  border: 1px solid rgba(255, 255, 255, 0.3) !important;
  border-radius: 24px !important;
  box-shadow: 0 30px 60px rgba(0, 0, 0, 0.3) !important;
  z-index: 1;
  animation: slideUp 0.8s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes slideUp {
  0% { opacity: 0; transform: translateY(40px); }
  100% { opacity: 1; transform: translateY(0); }
}

.card-header h2 {
  margin: 0 0 10px 0;
  text-align: center;
  font-size: 28px;
  font-weight: 800;
  letter-spacing: 2px;
  color: #ffffff;
  text-shadow: 0 2px 8px rgba(0,0,0,0.4);
}


:deep(.el-card__header) {
  background: transparent !important;
  border-bottom: 1px solid rgba(255,255,255,0.15) !important;
}


:deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.05) !important;
  border-radius: 12px;
  box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.2) inset !important;
  padding: 8px 15px;
}
:deep(.el-input__inner) {
  color: #fff !important;
  font-size: 16px;
}
:deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.5);
}
:deep(.el-form-item__label) {
  color: rgba(255, 255, 255, 0.9) !important;
  font-weight: 600;
  font-size: 15px;
}

.btn-login {
  width: 100%;
  height: 50px;
  font-size: 18px;
  font-weight: bold;
  letter-spacing: 4px;
  border-radius: 12px !important;
  background: linear-gradient(135deg, #6366f1, #d946ef) !important;
  border: none !important;
  margin-top: 15px;
}
.btn-login:hover {
  filter: brightness(1.1);
  box-shadow: 0 8px 25px rgba(217, 70, 239, 0.5) !important;
}
</style>
