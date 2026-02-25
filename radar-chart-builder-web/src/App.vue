<script setup lang="ts">
import { onErrorCaptured } from 'vue'

onErrorCaptured((error, instance, info) => {
  console.error('Vue Error:', error)
  console.error('Component:', instance)
  console.error('Info:', info)
  // 返回 false 阻止错误继续传播
  return false
})
</script>

<template>
  <RouterView />
</template>

<style>
/* Global styles */
:root {
  --el-color-primary: #409eff;
  --el-border-radius-base: 4px;
}

* {
  box-sizing: border-box;
}

html,
body {
  margin: 0;
  padding: 0;
  height: 100%;
}

#app {
  height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue',
    Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.el-card {
  border-radius: 8px;
}

.el-card__header {
  padding: 16px 20px;
  border-bottom: 1px solid #e4e7ed;
}

.el-card__body {
  padding: 20px;
}

.el-button {
  border-radius: 4px;
}

.el-input__wrapper {
  border-radius: 4px;
}
</style>
