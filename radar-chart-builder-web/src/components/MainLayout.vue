<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<template>
  <div class="main-layout">
    <el-header class="top-header">
      <div class="header-left">
        <h1>雷达图生成器</h1>
      </div>
      <div class="header-right">
        <span class="username">{{ authStore.user?.username }}</span>
        <el-button type="danger" plain @click="handleLogout">
          退出登录
        </el-button>
      </div>
    </el-header>
    <el-main class="main-content">
      <router-view />
    </el-main>
  </div>
</template>

<style scoped>
.main-layout {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.top-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 24px;
  height: 60px;
}

.header-left h1 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.username {
  color: #606266;
  font-size: 14px;
}

.main-content {
  padding: 0;
  overflow-y: auto;
}
</style>
