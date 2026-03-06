<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import type { LoginRequest } from '@/types/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const loginForm = reactive<LoginRequest>({
  account: '',
  password: '',
})

const loading = ref(false)
const rules = {
  account: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少 6 个字符', trigger: 'blur' },
  ],
}

const formRef = ref()

const handleLogin = async () => {
  try {
    const valid = await formRef.value?.validate()
    if (!valid) return

    loading.value = true
    await authStore.login(loginForm)

    const redirect = (route.query.redirect as string) || '/'
    router.push(redirect)
  } catch {
    // Error is handled by request interceptor
  } finally {
    loading.value = false
  }
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<template>
  <div class="auth-container">
    <el-card class="auth-card">
      <template #header>
        <div class="card-header">
          <h2>登录</h2>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="loginForm"
        :rules="rules"
        label-width="80px"
        @submit.prevent="handleLogin"
      >
        <el-form-item label="用户名" prop="account">
          <el-input
            v-model="loginForm.account"
            placeholder="请输入用户名"
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            style="width: 100%"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>

        <el-form-item>
          <div class="auth-footer">
            <span>还没有账号？</span>
            <el-link type="primary" @click="goToRegister">立即注册</el-link>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #1E90FF 0%, #FFFFFF 100%);
  padding: 20px;
}

.auth-card {
  width: 100%;
  max-width: 360px;
  border-radius: 8px;
}

/* 平板及以上 (>= 768px) */
@media (min-width: 768px) {
  .auth-card {
    max-width: 420px;
  }
}

/* 桌面 (>= 1024px) */
@media (min-width: 1024px) {
  .auth-card {
    max-width: 480px;
  }
}

/* 大屏幕 (>= 1440px) */
@media (min-width: 1440px) {
  .auth-card {
    max-width: 540px;
  }
}

.card-header {
  text-align: center;
}

.card-header h2 {
  margin: 0;
  color: var(--el-text-color-primary);
  font-size: 1.5rem;
}

@media (min-width: 768px) {
  .card-header h2 {
    font-size: 1.75rem;
  }
}

.auth-footer {
  width: 100%;
  text-align: center;
  display: flex;
  justify-content: center;
  gap: 8px;
}
</style>
