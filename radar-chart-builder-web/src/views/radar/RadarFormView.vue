<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Plus, Delete } from '@element-plus/icons-vue'
import { useRadarChartStore } from '@/stores/radarChart'
import type { Dimension, CreateRadarChartDto, UpdateRadarChartDto } from '@/types/models'
import RadarChartPreview from '@/components/RadarChartPreview.vue'

const router = useRouter()
const route = useRoute()
const radarChartStore = useRadarChartStore()

const isEditMode = computed(() => !!route.params.id && route.name === 'radar-edit')
const chartId = computed(() => route.params.id as string | undefined)

// Form data
const formData = ref({
  title: '',
  description: '',
})

const dimensions = ref<Omit<Dimension, 'id'>[]>([
  { name: '维度1', description: '', minValue: 0, maxValue: 100, orderIndex: 0 },
  { name: '维度2', description: '', minValue: 0, maxValue: 100, orderIndex: 1 },
  { name: '维度3', description: '', minValue: 0, maxValue: 100, orderIndex: 2 },
])

const loading = ref(false)
const saving = ref(false)

// 预览图表
const previewChart = computed(() => {
  console.log('previewChart computing:', {
    dimensionsLength: dimensions.value.length,
    title: formData.value.title
  })

  if (dimensions.value.length < 3) {
    console.log('previewChart: dimensions < 3, returning null')
    return null
  }

  // 创建默认数据系列，每个维度使用平均值
  const defaultSeries = {
    id: 'preview',
    name: '预览',
    color: '#409eff',
    displayOrder: 0,
    data: dimensions.value.map((d, i) => ({
      id: `preview-${i}`,
      dimensionId: String(i),
      seriesId: 'preview',
      value: (d.minValue + d.maxValue) / 2,
    })),
  }

  const result = {
    id: 'preview',
    title: formData.value.title || '雷达图预览',
    description: formData.value.description,
    dimensions: dimensions.value.map((d, i) => ({
      id: String(i),
      name: d.name,
      description: d.description ?? null,
      minValue: d.minValue,
      maxValue: d.maxValue,
      orderIndex: d.orderIndex,
    })),
    series: [defaultSeries],
    createTime: new Date().toISOString(),
    updateTime: new Date().toISOString(),
  }

  console.log('previewChart result:', result)
  return result
})

// Load chart data for edit mode
onMounted(async () => {
  if (isEditMode.value && chartId.value) {
    loading.value = true
    try {
      const chart = await radarChartStore.fetchById(chartId.value)
      if (chart) {
        formData.value = {
          title: chart.title,
          description: chart.description ?? '',
        }
        dimensions.value = chart.dimensions.map((d) => ({
          name: d.name,
          description: d.description ?? '',
          minValue: d.minValue ?? 0,
          maxValue: d.maxValue,
          orderIndex: d.orderIndex,
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
    minValue: 0,
    maxValue: 100,
    orderIndex: newOrder,
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
  dimensions.value.forEach((d, i) => (d.orderIndex = i))
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

  saving.value = true
  try {
    if (isEditMode.value) {
      const updateData: UpdateRadarChartDto = {
        title: formData.value.title,
        description: formData.value.description || undefined,
        dimensions: dimensions.value,
      }
      await radarChartStore.update(chartId.value!, updateData)
      ElMessage.success('保存成功')
    } else {
      const createData: CreateRadarChartDto = {
        title: formData.value.title,
        description: formData.value.description || undefined,
        dimensions: dimensions.value.map((d) => ({
          name: d.name,
          description: d.description || undefined,
          minValue: d.minValue,
          maxValue: d.maxValue,
          orderIndex: d.orderIndex,
        })),
        // 不自动生成系列，让用户后续添加
      }
      console.log('Creating radar chart with data:', createData)
      const result = await radarChartStore.create(createData)
      console.log('Create result:', result)
      console.log('Create result ID:', result?.id)
      console.log('Create result ID type:', typeof result?.id)

      if (!result || !result.id) {
        ElMessage.error('创建雷达图失败，未返回有效ID')
        return
      }

      ElMessage.success('创建成功')

      // 跳转到数据系列详情页
      await nextTick()
      const targetUrl = `/series/${result.id}`
      console.log('Navigating to:', targetUrl)
      router.push(targetUrl)
    }
  } catch (error) {
    console.error('Save error:', error)
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const cancel = async () => {
  try {
    await nextTick()
    router.push('/radar')
  } catch (error) {
    console.error('路由跳转失败:', error)
    window.location.href = '/radar'
  }
}

const goToSeries = (chartId: string) => {
  router.push(`/series/${chartId}/preview`)
}
</script>

<template>
  <div class="radar-form-container">
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-button @click="cancel" :icon="ArrowLeft">返回</el-button>
          <h1>{{ isEditMode ? '编辑雷达图' : '创建雷达图' }}</h1>
        </div>
        <div class="header-right">
          <el-button v-if="isEditMode" type="primary" plain @click="goToSeries(chartId!)">
            查看数据
          </el-button>
          <el-button type="primary" :loading="saving" :disabled="saving" @click="saveChart">
            {{ isEditMode ? '保存' : '下一步' }}
          </el-button>
        </div>
      </el-header>

      <el-main class="main" v-loading="loading">
        <el-row :gutter="20">
          <el-col :xs="24" :md="16" :lg="12">
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
                      v-if="dimensions.length > 3"
                      type="danger"
                      size="small"
                      text
                      @click="removeDimension(index)"
                    >
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </div>
                  <el-row :gutter="8" align="bottom">
                    <el-col :span="8">
                      <span class="input-label">&nbsp;</span>
                      <el-input
                        v-model="dimension.name"
                        placeholder="名称"
                        size="small"
                      />
                    </el-col>
                    <el-col :span="7">
                      <span class="input-label">最小值</span>
                      <el-input-number
                        v-model="dimension.minValue"
                        placeholder="最小值"
                        size="small"
                        controls-position="right"
                        style="width: 100%"
                      />
                    </el-col>
                    <el-col :span="9">
                      <span class="input-label">最大值</span>
                      <el-input-number
                        v-model="dimension.maxValue"
                        placeholder="最大值"
                        :min="dimension.minValue + 1"
                        size="small"
                        controls-position="right"
                        style="width: 100%"
                      />
                    </el-col>
                  </el-row>
                </div>
              </div>
            </el-card>
          </el-col>

          <el-col :xs="24" :md="8" :lg="12">
            <el-card class="preview-card">
              <template #header>
                <h3>实时预览</h3>
              </template>
              <div v-if="previewChart" class="chart-wrapper">
                <RadarChartPreview :chart="previewChart" :height="400" />
              </div>
              <el-empty v-else description="请填写标题并添加至少3个维度以查看预览" />
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<style scoped>
.radar-form-container {
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

.form-card {
  margin-bottom: 16px;
}

@media (min-width: 768px) {
  .form-card {
    margin-bottom: 20px;
  }
}

.form-card h3 {
  margin: 0;
  font-size: 15px;
  color: #303133;
}

@media (min-width: 768px) {
  .form-card h3 {
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

.dimension-list {
  max-height: 500px;
  overflow-y: auto;
}

.dimension-item {
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 8px;
}

@media (min-width: 768px) {
  .dimension-item {
    padding: 12px;
    margin-bottom: 10px;
  }
}

.dimension-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-weight: 500;
  font-size: 14px;
}

@media (min-width: 768px) {
  .dimension-header {
    margin-bottom: 10px;
    font-size: 15px;
  }
}

.input-label {
  display: block;
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.info-card h3,
.preview-card h3 {
  margin: 0;
  font-size: 15px;
  color: #303133;
}

@media (min-width: 768px) {
  .info-card h3,
  .preview-card h3 {
    font-size: 16px;
  }
}

.info-content p {
  margin: 0 0 12px 0;
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
}

@media (min-width: 768px) {
  .info-content p {
    font-size: 15px;
  }
}

.preview-card {
  margin-bottom: 16px;
  width: 100%;
}

.preview-card :deep(.el-card__body) {
  width: 100%;
}

@media (min-width: 768px) {
  .preview-card {
    margin-bottom: 20px;
  }
}

.chart-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  width: 100%;
  min-width: 300px;
}
</style>
