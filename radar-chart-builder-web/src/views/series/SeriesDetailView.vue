<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Plus, Delete, Edit } from '@element-plus/icons-vue'
import { useRadarChartStore } from '@/stores/radarChart'
import type { Dimension, DataSeries, UpdateRadarChartDto } from '@/types/models'
import RadarChartPreview from '@/components/RadarChartPreview.vue'

const router = useRouter()
const route = useRoute()
const radarChartStore = useRadarChartStore()

const chartId = computed(() => route.params.id as string)

const chart = ref<any>(null)
const dimensions = ref<Dimension[]>([])
const series = ref<DataSeries[]>([])

const loading = ref(false)
const saving = ref(false)

// Load chart data
onMounted(async () => {
  loading.value = true
  try {
    const data = await radarChartStore.fetchById(chartId.value)
    if (data) {
      chart.value = data
      dimensions.value = data.dimensions
      series.value = data.series.map((s) => ({
        id: s.id,
        name: s.name,
        color: s.color ?? '#409eff', // 默认颜色
        displayOrder: 0, // 前端排序
        data: s.data && Array.isArray(s.data)
          ? (s.data as any[]).map((sd: any) => ({
              id: String(sd.dimensionId), // 使用dimensionId作为id
              value: sd.value,
              dimensionId: sd.dimensionId,
              seriesId: s.id,
            }))
          : [],
      }))
    }
  } finally {
    loading.value = false
  }
})

// Preview chart
const previewChart = computed(() => {
  if (!chart.value) return null
  return {
    id: chart.value.id,
    title: chart.value.title,
    description: chart.value.description,
    dimensions: dimensions.value,
    series: series.value.map((s) => ({
      id: s.id,
      name: s.name ?? '',
      color: s.color ?? '',
      displayOrder: s.displayOrder,
      data: dimensions.value.map((d) => {
        const dataPoint = s.data?.find((sd) => sd.dimensionId === d.id)
        const defaultValue = (d.minValue + d.maxValue) / 2
        return {
          id: `${s.id}-${d.id}`,
          dimensionId: d.id,
          seriesId: s.id,
          value: dataPoint?.value ?? defaultValue,
        }
      }),
    })),
    createTime: chart.value.createTime,
    updateTime: chart.value.updateTime,
  }
})

// Add series
const addSeries = () => {
  const colors: string[] = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399']
  const colorIndex = series.value.length % colors.length
  const newSeries = {
    id: `new-${Date.now()}`,
    name: `数据组${series.value.length + 1}`,
    color: colors[colorIndex],
    displayOrder: 0,
    data: dimensions.value.map((d) => ({
      id: `new-${Date.now()}-${d.id}`,
      value: (d.minValue + d.maxValue) / 2,
      dimensionId: d.id,
      seriesId: `new-${Date.now()}`,
    })),
  }
  // 添加到最前面
  series.value.unshift(newSeries)
  // 更新所有 displayOrder
  series.value.forEach((s, i) => (s.displayOrder = i))
}

// Remove series
const removeSeries = async (index: number) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除数据组 "${series.value[index].name}" 吗？`,
      '确认删除',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    // Remove from local array (will be saved when user saves)
    series.value.splice(index, 1)
    series.value.forEach((s, i) => (s.displayOrder = i))
  } catch {
    // User cancelled
  }
}

// Update series data value
const updateSeriesData = (seriesIndex: number, dimensionIndex: number, value: number) => {
  const s = series.value[seriesIndex]
  if (!s) return

  const dim = dimensions.value[dimensionIndex]
  if (!dim) return

  const existingIndex = s.data?.findIndex((sd) => sd.dimensionId === dim.id) ?? -1

  const newDataPoint = {
    id: `${s.id}-${dim.id}`,
    dimensionId: dim.id,
    seriesId: s.id,
    value,
  }

  if (!s.data) {
    s.data = [newDataPoint]
  } else if (existingIndex >= 0 && s.data[existingIndex]) {
    s.data[existingIndex] = {
      ...s.data[existingIndex],
      value: value,
    }
  } else {
    s.data.push(newDataPoint)
  }
}

// Get series data value
const getSeriesDataValue = (seriesIndex: number, dimensionIndex: number): number => {
  const s = series.value[seriesIndex]
  const dim = dimensions.value[dimensionIndex]
  const defaultValue = dim ? (dim.minValue + dim.maxValue) / 2 : 50
  if (!s?.data || !dim) return defaultValue
  const data = s.data.find((sd) => sd.dimensionId === dim.id)
  return data?.value ?? defaultValue
}

// Save all changes
const saveChanges = async () => {
  saving.value = true
  try {
    const updateData: UpdateRadarChartDto = {
      series: series.value.map((s) => ({
        id: s.id,
        name: s.name,
        color: s.color,
        displayOrder: s.displayOrder,
        data: dimensions.value.map((d) => ({
          dimensionId: d.id,
          value: getSeriesDataValue(series.value.findIndex((sv) => sv.id === s.id), dimensions.value.findIndex((dd) => dd.id === d.id)),
        })),
      })),
    }
    await radarChartStore.update(chartId.value, updateData)
    ElMessage.success('保存成功')
  } finally {
    saving.value = false
  }
}

// Go to radar form edit
const editRadar = () => {
  router.push(`/radar/${chartId.value}/edit`)
}

// Go back to list
const goToList = async () => {
  try {
    await nextTick()
    router.push('/radar')
  } catch (error) {
    console.error('路由跳转失败:', error)
    window.location.href = '/radar'
  }
}
</script>

<template>
  <div class="series-detail-container">
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-button @click="goToList" :icon="ArrowLeft">返回</el-button>
          <h1>{{ chart?.title || '雷达图详情' }}</h1>
        </div>
        <div class="header-right">
          <el-button @click="editRadar" :icon="Edit">编辑雷达</el-button>
          <el-button type="primary" :loading="saving" @click="saveChanges">保存</el-button>
        </div>
      </el-header>

      <el-main class="main" v-loading="loading">
        <el-row :gutter="20">
          <!-- 左侧：图表预览 -->
          <el-col :xs="24" :md="12">
            <el-card class="preview-card">
              <template #header>
                <h3>图表预览</h3>
              </template>
              <div v-if="previewChart && series.length > 0" class="chart-wrapper">
                <RadarChartPreview :chart="previewChart" :height="500" />
              </div>
              <el-empty v-else description="暂未添加数据组，无法预览图表" />
            </el-card>
          </el-col>

          <!-- 右侧：数据组编辑 -->
          <el-col :xs="24" :md="12">
            <el-card class="series-card">
              <template #header>
                <div class="card-header">
                  <h3>数据组</h3>
                  <el-button type="primary" size="small" @click="addSeries">
                    <el-icon><Plus /></el-icon>
                    添加数据
                  </el-button>
                </div>
              </template>

              <el-empty v-if="series.length === 0" description="暂未添加数据组" />

              <div v-else class="series-list">
                <div
                  v-for="(s, seriesIndex) in series"
                  :key="s.id"
                  class="series-item"
                >
                  <div class="series-header">
                    <el-color-picker v-model="s.color" size="small" />
                    <el-input
                      v-model="s.name"
                      placeholder="数据组名称"
                      size="small"
                      style="flex: 1; margin: 0 10px"
                    />
                    <el-button
                      type="danger"
                      size="small"
                      text
                      @click="removeSeries(seriesIndex)"
                    >
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </div>

                  <div class="series-data">
                    <div
                      v-for="(dimension, dimIndex) in dimensions"
                      :key="dimIndex"
                      class="data-row"
                    >
                      <span class="data-label">{{ dimension.name }}:</span>
                      <el-slider
                        :model-value="getSeriesDataValue(seriesIndex, dimIndex)"
                        :min="dimension.minValue ?? 0"
                        :max="dimension.maxValue"
                        @update:model-value="
                          (v: number) => updateSeriesData(seriesIndex, dimIndex, v)
                        "
                        style="flex: 1; margin: 0 10px"
                      />
                      <el-input-number
                        :model-value="getSeriesDataValue(seriesIndex, dimIndex)"
                        :min="dimension.minValue ?? 0"
                        :max="dimension.maxValue"
                        size="small"
                        controls-position="right"
                        @update:model-value="
                          (v: number) => updateSeriesData(seriesIndex, dimIndex, v)
                        "
                      />
                    </div>
                  </div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<style scoped>
.series-detail-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 16px;
  height: 60px;
  flex-wrap: wrap;
  gap: 8px;
}

@media (min-width: 768px) {
  .header {
    padding: 0 20px;
    height: auto;
    flex-wrap: nowrap;
  }
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.header-left h1 {
  margin: 0;
  font-size: 16px;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

@media (min-width: 768px) {
  .header-left h1 {
    font-size: 18px;
  }
}

.header-right {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.main {
  padding: 16px;
}

@media (min-width: 768px) {
  .main {
    padding: 20px;
  }
}

.preview-card,
.series-card {
  margin-bottom: 16px;
}

@media (min-width: 768px) {
  .preview-card,
  .series-card {
    margin-bottom: 20px;
  }
}

.preview-card h3,
.series-card h3 {
  margin: 0;
  font-size: 15px;
  color: #303133;
}

@media (min-width: 768px) {
  .preview-card h3,
  .series-card h3 {
    font-size: 16px;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

@media (min-width: 768px) {
  .card-header {
    gap: 16px;
  }
}

.chart-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 500px;
}

.series-list {
  max-height: 600px;
  overflow-y: auto;
}

.series-item {
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 12px;
}

@media (min-width: 768px) {
  .series-item {
    padding: 16px;
    margin-bottom: 16px;
  }
}

.series-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  gap: 8px;
  min-height: 40px;
}

@media (min-width: 768px) {
  .series-header {
    margin-bottom: 12px;
    gap: 10px;
    min-height: 44px;
  }
}

/* 统一高度 - 颜色选择器 */
.series-header .el-color-picker {
  height: 36px;
}

.series-header .el-color-picker :deep(.el-color-picker__trigger) {
  height: 36px !important;
  width: 36px !important;
  padding: 2px !important;
}

/* 统一高度 - 输入框 */
.series-header .el-input {
  height: 36px;
  flex: 0 0 150px;
  max-width: 150px;
}

.series-header .el-input :deep(.el-input__wrapper) {
  height: 36px !important;
}

.series-header .el-input :deep(.el-input__inner) {
  font-size: 13px;
}

@media (min-width: 768px) {
  .series-header .el-input {
    flex: 0 0 180px;
    max-width: 180px;
  }

  .series-header .el-input :deep(.el-input__inner) {
    font-size: 14px;
  }
}

/* 统一高度 - 删除按钮 */
.series-header .el-button {
  height: 36px !important;
  width: 36px !important;
  padding: 0 !important;
  margin-left: auto;
}

.series-header .el-button :deep(.el-icon) {
  font-size: 16px;
}

.series-data {
  background-color: #fff;
  padding: 10px;
  border-radius: 4px;
}

@media (min-width: 768px) {
  .series-data {
    padding: 12px;
  }
}

.data-row {
  display: flex;
  align-items: center;
  margin-bottom: 6px;
  gap: 6px;
}

@media (min-width: 768px) {
  .data-row {
    margin-bottom: 8px;
    gap: 10px;
  }
}

.data-row:last-child {
  margin-bottom: 0;
}

.data-label {
  min-width: 60px;
  font-size: 13px;
  color: #606266;
}

@media (min-width: 768px) {
  .data-label {
    min-width: 80px;
    font-size: 14px;
  }
}
</style>
