<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Search, Plus, MoreFilled, Edit, Delete } from '@element-plus/icons-vue'
import { useRadarChartStore } from '@/stores/radarChart'
import { useAuthStore } from '@/stores/auth'
import type { RadarChart } from '@/types/models'
import RadarChartPreview from '@/components/RadarChartPreview.vue'

const router = useRouter()
const radarChartStore = useRadarChartStore()
const authStore = useAuthStore()

const searchQuery = ref('')

const filteredCharts = computed(() => {
  if (!searchQuery.value) {
    return radarChartStore.charts
  }
  const query = searchQuery.value.toLowerCase()
  return radarChartStore.charts.filter(
    (chart) =>
      chart.title.toLowerCase().includes(query) ||
      chart.description?.toLowerCase().includes(query)
  )
})

const handleCreate = () => {
  router.push('/radar/new')
}

const handleEdit = (chart: RadarChart) => {
  router.push(`/radar/${chart.id}`)
}

const handleDelete = async (chart: RadarChart) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除雷达图 "${chart.title}" 吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger',
      }
    )
    const success = await radarChartStore.remove(chart.id)
    if (success) {
      ElMessage.success('雷达图删除成功')
    }
  } catch {
    // User cancelled
  }
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

onMounted(async () => {
  await radarChartStore.fetchList()
})
</script>

<template>
  <div class="radar-list-container">
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <h1>雷达图生成器</h1>
        </div>
        <div class="header-right">
          <span class="username">{{ authStore.user?.username }}</span>
          <el-button @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>

      <el-main class="main">
        <div class="toolbar">
          <el-input
            v-model="searchQuery"
            placeholder="搜索雷达图..."
            clearable
            style="width: 300px"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" @click="handleCreate">
            <el-icon><Plus /></el-icon>
            创建雷达图
          </el-button>
        </div>

        <el-empty v-if="filteredCharts.length === 0" description="暂无雷达图，点击上方按钮创建">
          <el-button type="primary" @click="handleCreate">创建雷达图</el-button>
        </el-empty>

        <el-row v-else :gutter="20" class="chart-grid">
          <el-col
            v-for="chart in filteredCharts"
            :key="chart.id"
            :xs="24"
            :sm="12"
            :md="8"
            :lg="6"
          >
            <el-card class="chart-card" shadow="hover">
              <template #header>
                <div class="card-header">
                  <span class="chart-title">{{ chart.title }}</span>
                  <el-dropdown trigger="click">
                    <el-icon class="more-icon"><MoreFilled /></el-icon>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item @click="handleEdit(chart)">
                          <el-icon><Edit /></el-icon>
                          编辑
                        </el-dropdown-item>
                        <el-dropdown-item divided @click="handleDelete(chart)">
                          <el-icon><Delete /></el-icon>
                          删除
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>
              </template>

              <div class="chart-preview">
                <RadarChartPreview :chart="chart" :height="200" />
              </div>

              <div class="chart-info">
                <p class="chart-description">{{ chart.description || '无描述' }}</p>
                <div class="chart-meta">
                  <el-tag size="small">{{ chart.dimensions.length }} 个维度</el-tag>
                  <el-tag size="small" type="success">{{ chart.series.length }} 个系列</el-tag>
                </div>
                <p class="chart-date">更新于 {{ formatDate(chart.updateTime) }}</p>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<style scoped>
.radar-list-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 20px;
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
}

.main {
  padding: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.chart-grid {
  margin-bottom: 20px;
}

.chart-card {
  margin-bottom: 20px;
  height: calc(100% - 20px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-title {
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.more-icon {
  cursor: pointer;
  color: #909399;
}

.more-icon:hover {
  color: #409eff;
}

.chart-preview {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;
}

.chart-info {
  text-align: center;
}

.chart-description {
  margin: 0 0 8px 0;
  color: #606266;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.chart-meta {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-bottom: 8px;
}

.chart-date {
  margin: 0;
  color: #909399;
  font-size: 12px;
}
</style>
