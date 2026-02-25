<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowLeft, Edit } from '@element-plus/icons-vue'
import { useRadarChartStore } from '@/stores/radarChart'
import type { Dimension, DataSeries } from '@/types/models'
import RadarChartPreview from '@/components/RadarChartPreview.vue'

const router = useRouter()
const route = useRoute()
const radarChartStore = useRadarChartStore()

const chartId = computed(() => route.params.id as string)

const chart = ref<any>(null)
const dimensions = ref<Dimension[]>([])
const series = ref<DataSeries[]>([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const data = await radarChartStore.fetchById(chartId.value)
    if (data) {
      chart.value = data
      dimensions.value = data.dimensions
      series.value = data.series.map((s) => ({
        id: s.id,
        name: s.name ?? '',
        color: s.color ?? '#409eff',
        displayOrder: s.displayOrder ?? 0,
        data: s.data && Array.isArray(s.data)
          ? (s.data as any[]).map((sd: any) => ({
              id: String(sd.dimensionId),
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

const getSeriesDataValue = (seriesIndex: number, dimensionIndex: number): number => {
  const s = series.value[seriesIndex]
  const dim = dimensions.value[dimensionIndex]
  const defaultValue = dim ? (dim.minValue + dim.maxValue) / 2 : 50
  if (!s?.data || !dim) return defaultValue
  const data = s.data.find((sd) => sd.dimensionId === dim.id)
  return data?.value ?? defaultValue
}

const editRadar = () => {
  router.push(`/series/${chartId.value}`)
}

const goToList = async () => {
  router.push('/radar')
}
</script>

<template>
  <div class="series-preview-container">
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-button @click="goToList" :icon="ArrowLeft">返回</el-button>
          <h1>{{ chart?.title || '雷达图详情' }}</h1>
        </div>
        <div class="header-right">
          <el-button type="primary" :icon="Edit" @click="editRadar">
            编辑数据
          </el-button>
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

          <!-- 右侧：数据组信息（只读） -->
          <el-col :xs="24" :md="12">
            <el-card class="series-card">
              <template #header>
                <h3>数据组</h3>
              </template>

              <el-empty v-if="series.length === 0" description="暂未添加数据组" />

              <div v-else class="series-grid">
                <div
                  v-for="(s, seriesIndex) in series"
                  :key="s.id"
                  class="series-item"
                >
                  <div class="series-header">
                    <div
                      class="color-preview"
                      :style="{ backgroundColor: s.color ?? '#409eff' }"
                    ></div>
                    <span class="series-name">{{ s.name }}</span>
                  </div>

                  <div class="series-data">
                    <div
                      v-for="(dimension, dimIndex) in dimensions"
                      :key="dimIndex"
                      class="data-row"
                    >
                      <span class="data-label">{{ dimension.name }}:</span>
                      <span class="data-value">{{ getSeriesDataValue(seriesIndex, dimIndex) }}</span>
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
.series-preview-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 24px;
  height: 60px;
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
  font-size: 18px;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

@media (min-width: 768px) {
  .header-left h1 {
    font-size: 20px;
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

.series-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
  max-height: 600px;
  overflow-y: auto;
}

@media (min-width: 768px) {
  .series-grid {
    grid-template-columns: 1fr 1fr;
    gap: 16px;
  }
}

.series-item {
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

@media (min-width: 768px) {
  .series-item {
    padding: 12px;
  }
}

.series-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  gap: 10px;
  min-height: 40px;
}

.color-preview {
  width: 36px;
  height: 36px;
  border-radius: 4px;
  flex-shrink: 0;
}

.series-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

@media (min-width: 768px) {
  .series-name {
    font-size: 15px;
  }
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

.data-value {
  flex: 1;
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

@media (min-width: 768px) {
  .data-value {
    font-size: 15px;
  }
}
</style>
