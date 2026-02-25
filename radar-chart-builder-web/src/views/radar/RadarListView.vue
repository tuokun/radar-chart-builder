<script setup lang="ts">
import { ref, onMounted, watch, nextTick } from 'vue'
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
const searchInputRef = ref()
const filteredCharts = ref<RadarChart[]>([])

// 初始化时显示所有图表
watch(() => radarChartStore.charts, (charts) => {
  filteredCharts.value = charts
}, { immediate: true })

const handleCreate = async () => {
  try {
    // 使用 nextTick 确保 DOM 更新完成
    await nextTick()
    await router.push('/radar/new')
  } catch (error) {
    console.error('路由跳转失败:', error)
    // 如果路由跳转失败，使用原生跳转
    window.location.href = '/radar/new'
  }
}

const handleDropdownCommand = (command: string, chart: RadarChart) => {
  console.log('Dropdown command:', command, 'chart:', chart.title)
  switch (command) {
    case 'edit':
      handleEdit(chart)
      break
    case 'delete':
      handleDelete(chart)
      break
  }
}

const handleSearch = () => {
  console.log('handleSearch called, searchQuery:', searchQuery.value)
  if (!searchQuery.value.trim()) {
    filteredCharts.value = radarChartStore.charts
  } else {
    const query = searchQuery.value.toLowerCase().trim()
    filteredCharts.value = radarChartStore.charts.filter(
      (chart) =>
        chart.title.toLowerCase().includes(query) ||
        chart.description?.toLowerCase().includes(query)
    )
  }
  if (searchInputRef.value) {
    searchInputRef.value.blur()
  }
}

const handleEdit = async (chart: RadarChart) => {
  try {
    await nextTick()
    await router.push(`/series/${chart.id}/preview`)
  } catch (error) {
    console.error('路由跳转失败:', error)
    window.location.href = `/series/${chart.id}/preview`
  }
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
    const success = await radarChartStore.remove(String(chart.id))
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
  <div class="radar-list-content">
    <div class="toolbar">
      <h2 class="page-title">我的雷达图</h2>
      <div class="toolbar-actions">
        <el-input
          v-model="searchQuery"
          placeholder="搜索雷达图..."
          clearable
          class="search-input"
          @keyup.enter="handleSearch"
          ref="searchInputRef"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          创建雷达图
        </el-button>
      </div>
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
        <el-card class="chart-card" shadow="hover" @click="handleEdit(chart)">
          <template #header>
            <div class="card-header">
              <span class="chart-title">{{ chart.title }}</span>
              <el-dropdown trigger="click" @command="(cmd) => handleDropdownCommand(cmd, chart)">
                <el-icon class="more-icon" @click.stop><MoreFilled /></el-icon>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="edit">
                      <el-icon><Edit /></el-icon>
                      编辑数据
                    </el-dropdown-item>
                    <el-dropdown-item command="delete" divided>
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
              <el-tag size="small" type="success">{{ chart.series.length }} 组数据</el-tag>
            </div>
            <p class="chart-date">更新于 {{ formatDate(chart.updateTime) }}</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.radar-list-content {
  padding: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 16px;
}

.page-title {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

@media (min-width: 768px) {
  .page-title {
    font-size: 20px;
  }
}

.toolbar-actions {
  display: flex;
  gap: 12px;
  align-items: center;
  flex: 1;
  justify-content: flex-end;
}

.search-input {
  width: 100%;
  max-width: 200px;
}

@media (min-width: 768px) {
  .search-input {
    max-width: 280px;
  }
}

.chart-grid {
  margin-bottom: 20px;
}

.chart-card {
  margin-bottom: 20px;
  height: calc(100% - 20px);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.chart-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
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
  font-size: 14px;
}

@media (min-width: 768px) {
  .chart-title {
    font-size: 15px;
  }
}

.more-icon {
  cursor: pointer;
  color: #909399;
  padding: 8px;
  border-radius: 4px;
  transition: background-color 0.2s;
  font-size: 20px !important;
  width: 36px;
  height: 36px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.more-icon :deep(.el-icon) {
  font-size: 20px !important;
}

.more-icon :deep(svg) {
  width: 20px !important;
  height: 20px !important;
}

.more-icon:hover {
  color: #409eff;
  background-color: rgba(64, 158, 255, 0.1);
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
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

@media (min-width: 768px) {
  .chart-description {
    font-size: 14px;
  }
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
  font-size: 11px;
}

@media (min-width: 768px) {
  .chart-date {
    font-size: 12px;
  }
}
</style>
