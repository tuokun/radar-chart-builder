<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Plus, Delete } from '@element-plus/icons-vue'
import { useRadarChartStore } from '@/stores/radarChart'
import type { Dimension, DataSeries, CreateRadarChartDto, UpdateRadarChartDto } from '@/types/models'
import RadarChartPreview from '@/components/RadarChartPreview.vue'

const router = useRouter()
const route = useRoute()
const radarChartStore = useRadarChartStore()

const isEditMode = computed(() => route.params.id !== 'new' && route.params.id !== undefined)
const chartId = computed(() => route.params.id as string)

// Form data
const formData = ref({
  title: '',
  description: '',
})

const dimensions = ref<Omit<Dimension, 'id'>[]>([
  { name: '维度1', description: '', maxValue: 100, displayOrder: 0 },
  { name: '维度2', description: '', maxValue: 100, displayOrder: 1 },
  { name: '维度3', description: '', maxValue: 100, displayOrder: 2 },
])

const series = ref<DataSeries[]>([
  {
    id: Date.now(),
    name: '系列1',
    color: '#409eff',
    displayOrder: 0,
    data: [],
  },
])

// Generate preview data
const previewChart = computed(() => {
  return {
    id: 0,
    title: formData.value.title || '未命名雷达图',
    description: formData.value.description,
    dimensions: dimensions.value.map((d, i) => ({
      id: i,
      name: d.name,
      description: d.description,
      maxValue: d.maxValue,
      displayOrder: d.displayOrder,
    })),
    series: series.value.map((s) => ({
      id: s.id,
      name: s.name ?? '',
      color: s.color ?? '',
      displayOrder: s.displayOrder,
      data: dimensions.value.map((d, dimIndex) => {
        const existingData = s.data?.find((sd) => sd.dimensionId === dimIndex)
        return {
          id: dimIndex,
          dimensionId: dimIndex,
          seriesId: s.id ?? 0,
          value: existingData?.value ?? 50,
        }
      }),
    })),
    createTime: new Date().toISOString(),
    updateTime: new Date().toISOString(),
  }
})

const loading = ref(false)
const saving = ref(false)

// Load chart data for edit mode
onMounted(async () => {
  if (isEditMode.value) {
    loading.value = true
    try {
      const chart = await radarChartStore.fetchById(Number(chartId.value))
      if (chart) {
        formData.value = {
          title: chart.title,
          description: chart.description ?? '',
        }
        dimensions.value = chart.dimensions.map((d) => ({
          name: d.name,
          description: d.description ?? '',
          maxValue: d.maxValue,
          displayOrder: d.displayOrder,
        }))
        series.value = chart.series.map((s) => ({
          id: s.id,
          name: s.name,
          color: s.color,
          displayOrder: s.displayOrder,
          data: s.data?.map((sd) => ({
            id: sd.id,
            value: sd.value,
            dimensionId: dimensions.value.findIndex((d) => d.displayOrder === sd.dimensionId),
            seriesId: s.id,
          })) ?? [],
        }))
      }
    } finally {
      loading.value = false
    }
  }
})

// Add dimension
const addDimension = () => {
  const newOrder = dimensions.value.length
  dimensions.value.push({
    name: `维度${dimensions.value.length + 1}`,
    description: '',
    maxValue: 100,
    displayOrder: newOrder,
  })
}

// Remove dimension
const removeDimension = (index: number) => {
  if (dimensions.value.length <= 3) {
    ElMessage.warning('至少需要3个维度')
    return
  }
  dimensions.value.splice(index, 1)
  // Update display orders
  dimensions.value.forEach((d, i) => (d.displayOrder = i))
  // Update series data
  series.value = series.value.map((s) => {
    if (!s.data) return s
    return {
      ...s,
      data: s.data
        .filter((sd) => sd.dimensionId !== index)
        .map((sd) => ({
          ...sd,
          dimensionId: sd.dimensionId > index ? sd.dimensionId - 1 : sd.dimensionId,
        })),
    }
  })
}

// Add series
const addSeries = () => {
  const colors: string[] = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399']
  const colorIndex = series.value.length % colors.length
  series.value.push({
    id: Date.now(),
    name: `系列${series.value.length + 1}` as string,
    color: colors[colorIndex] ?? '#409eff',
    displayOrder: series.value.length,
    data: [],
  })
}

// Remove series
const removeSeries = (index: number) => {
  if (series.value.length <= 1) {
    ElMessage.warning('至少需要1个数据系列')
    return
  }
  series.value.splice(index, 1)
}

// Update series data value
const updateSeriesData = (seriesIndex: number, dimensionIndex: number, value: number) => {
  const s = series.value[seriesIndex]
  if (!s) return

  const existingIndex = s.data?.findIndex((sd) => sd.dimensionId === dimensionIndex) ?? -1

  const newDataPoint = {
    id: Date.now(),
    dimensionId: dimensionIndex,
    seriesId: s.id ?? 0,
    value,
  }

  if (!s.data) {
    s.data = [newDataPoint]
  } else if (existingIndex >= 0 && s.data[existingIndex]) {
    const existing = s.data[existingIndex]
    s.data[existingIndex] = {
      id: existing.id,
      value: value,
      dimensionId: existing.dimensionId,
      seriesId: existing.seriesId,
    }
  } else {
    s.data.push(newDataPoint)
  }
}

// Get series data value
const getSeriesDataValue = (seriesIndex: number, dimensionIndex: number): number => {
  const s = series.value[seriesIndex]
  if (!s?.data) return 50
  const data = s.data.find((sd) => sd.dimensionId === dimensionIndex)
  return data?.value ?? 50
}

// Save chart
const saveChart = async () => {
  if (!formData.value.title.trim()) {
    ElMessage.error('请输入标题')
    return
  }

  if (dimensions.value.length < 3) {
    ElMessage.error('至少需要3个维度')
    return
  }

  if (series.value.length < 1) {
    ElMessage.error('至少需要1个数据系列')
    return
  }

  saving.value = true
  try {
    const seriesData = series.value.map((s, sIndex) => ({
      name: s.name ?? '',
      color: s.color ?? '',
      displayOrder: s.displayOrder,
      data: dimensions.value.map((d, i) => ({
        dimensionId: i,
        value: getSeriesDataValue(sIndex, i),
      })),
    }))

    if (isEditMode.value) {
      const updateData: UpdateRadarChartDto = {
        title: formData.value.title,
        description: formData.value.description || undefined,
        dimensions: dimensions.value,
        series: seriesData.map((s, i) => ({
          id: series.value[i]?.id ?? 0,
          ...s,
        })),
      }
      await radarChartStore.update(Number(chartId.value), updateData)
    } else {
      const createData: CreateRadarChartDto = {
        title: formData.value.title,
        description: formData.value.description || undefined,
        dimensions: dimensions.value,
        series: seriesData,
      }
      await radarChartStore.create(createData)
    }

    router.push('/')
  } finally {
    saving.value = false
  }
}

const cancel = () => {
  router.push('/')
}
</script>

<template>
  <div class="radar-edit-container">
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-button @click="cancel" :icon="ArrowLeft">返回</el-button>
          <h1>{{ isEditMode ? '编辑雷达图' : '创建雷达图' }}</h1>
        </div>
        <div class="header-right">
          <el-button type="primary" :loading="saving" @click="saveChart">
            {{ isEditMode ? '保存' : '创建' }}
          </el-button>
        </div>
      </el-header>

      <el-main class="main" v-loading="loading">
        <el-row :gutter="20">
          <el-col :xs="24" :md="12">
            <el-card class="form-card">
              <template #header>
                <h3>基本信息</h3>
              </template>

              <el-form label-width="80px">
                <el-form-item label="标题" required>
                  <el-input
                    v-model="formData.title"
                    placeholder="请输入标题"
                    maxlength="100"
                    show-word-limit
                  />
                </el-form-item>

                <el-form-item label="描述">
                  <el-input
                    v-model="formData.description"
                    type="textarea"
                    placeholder="请输入描述"
                    :rows="3"
                    maxlength="500"
                    show-word-limit
                  />
                </el-form-item>
              </el-form>
            </el-card>

            <el-card class="form-card">
              <template #header>
                <div class="card-header">
                  <h3>维度设置</h3>
                  <el-button type="primary" size="small" @click="addDimension">
                    <el-icon><Plus /></el-icon>
                    添加维度
                  </el-button>
                </div>
              </template>

              <div class="dimension-list">
                <div
                  v-for="(dimension, index) in dimensions"
                  :key="index"
                  class="dimension-item"
                >
                  <div class="dimension-header">
                    <span>维度 {{ index + 1 }}</span>
                    <el-button
                      type="danger"
                      size="small"
                      text
                      @click="removeDimension(index)"
                    >
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </div>
                  <el-row :gutter="10">
                    <el-col :span="10">
                      <el-input
                        v-model="dimension.name"
                        placeholder="名称"
                        size="small"
                      />
                    </el-col>
                    <el-col :span="10">
                      <el-input-number
                        v-model="dimension.maxValue"
                        :min="1"
                        size="small"
                        controls-position="right"
                        style="width: 100%"
                      />
                    </el-col>
                  </el-row>
                </div>
              </div>
            </el-card>

            <el-card class="form-card">
              <template #header>
                <div class="card-header">
                  <h3>数据系列</h3>
                  <el-button type="primary" size="small" @click="addSeries">
                    <el-icon><Plus /></el-icon>
                    添加系列
                  </el-button>
                </div>
              </template>

              <div class="series-list">
                <div
                  v-for="(s, seriesIndex) in series"
                  :key="s.id"
                  class="series-item"
                >
                  <div class="series-header">
                    <el-color-picker v-model="s.color" size="small" />
                    <el-input
                      v-model="s.name"
                      placeholder="系列名称"
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
                        :max="dimension.maxValue"
                        @update:model-value="
                          (v: number) => updateSeriesData(seriesIndex, dimIndex, v)
                        "
                        style="flex: 1; margin: 0 10px"
                      />
                      <el-input-number
                        :model-value="getSeriesDataValue(seriesIndex, dimIndex)"
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

          <el-col :xs="24" :md="12">
            <el-card class="preview-card">
              <template #header>
                <h3>实时预览</h3>
              </template>
              <RadarChartPreview :chart="previewChart" :height="500" />
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<style scoped>
.radar-edit-container {
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

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-left h1 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}

.main {
  padding: 20px;
}

.form-card {
  margin-bottom: 20px;
}

.form-card h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dimension-list,
.series-list {
  max-height: 400px;
  overflow-y: auto;
}

.dimension-item {
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 10px;
}

.dimension-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-weight: 500;
}

.series-item {
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 16px;
}

.series-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.series-data {
  background-color: #fff;
  padding: 12px;
  border-radius: 4px;
}

.data-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.data-row:last-child {
  margin-bottom: 0;
}

.data-label {
  min-width: 80px;
  font-size: 14px;
  color: #606266;
}

.preview-card {
  position: sticky;
  top: 20px;
}

.preview-card h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
}
</style>
