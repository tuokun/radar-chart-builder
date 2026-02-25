<script setup lang="ts">
import { ref, watch } from 'vue'
import type { DataSeries, Dimension } from '@/types/models'

interface Props {
  series: DataSeries[]
  dimensions: Dimension[]
  modelValue: DataSeries[]
}

interface Emits {
  (e: 'update:modelValue', value: DataSeries[]): void
  (e: 'add'): void
  (e: 'remove', index: number): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const localSeries = ref<DataSeries[]>([
  {
    id: Date.now(),
    name: '数据组1',
    color: '#409eff',
    displayOrder: 0,
    data: [],
  },
])

watch(
  () => props.modelValue,
  (newVal: DataSeries[]) => {
    localSeries.value = newVal.map((s) => ({
      id: s.id,
      name: s.name,
      color: s.color,
      displayOrder: s.displayOrder,
      data: [...(s.data ?? [])],
    }))
  },
  { immediate: true, deep: true }
)

const updateSeries = (index: number, field: keyof DataSeries, value: string | number) => {
  if (index < 0 || index >= localSeries.value.length) return
  const current = localSeries.value[index]
  if (!current) return

  const updated: DataSeries = {
    id: current.id,
    name: current.name,
    color: current.color,
    displayOrder: current.displayOrder,
    data: [...(current.data ?? [])],
    [field]: value,
  }
  localSeries.value = [
    ...localSeries.value.slice(0, index),
    updated,
    ...localSeries.value.slice(index + 1),
  ]
  emit('update:modelValue', localSeries.value)
}

const getSeriesDataValue = (seriesIndex: number, dimensionId: number): number => {
  const series = localSeries.value[seriesIndex]
  if (!series || !series.data) return 50
  const dataPoint = series.data.find((d) => d.dimensionId === dimensionId)
  return dataPoint?.value ?? 50
}

const updateSeriesData = (seriesIndex: number, dimensionId: number, value: number) => {
  if (seriesIndex < 0 || seriesIndex >= localSeries.value.length) return

  const series = localSeries.value[seriesIndex]
  if (!series) return

  const dataIndex = series.data?.findIndex((d) => d.dimensionId === dimensionId) ?? -1
  const newDataPoint: import('@/types/models').SeriesData = {
    id: Date.now(),
    dimensionId,
    seriesId: series.id,
    value,
  }

  const updatedData = [...(series.data ?? [])]
  if (dataIndex >= 0) {
    updatedData[dataIndex] = newDataPoint
  } else {
    updatedData.push(newDataPoint)
  }

  const updatedSeries: DataSeries = {
    id: series.id,
    name: series.name,
    color: series.color,
    displayOrder: series.displayOrder,
    data: updatedData,
  }

  localSeries.value = [
    ...localSeries.value.slice(0, seriesIndex),
    updatedSeries,
    ...localSeries.value.slice(seriesIndex + 1),
  ]
  emit('update:modelValue', localSeries.value)
}

const addSeries = () => {
  emit('add')
}

const removeSeries = (index: number) => {
  emit('remove', index)
}
</script>

<template>
  <div class="series-editor">
    <div
      v-for="(seriesItem, seriesIndex) in localSeries"
      :key="seriesItem.id"
      class="series-item"
    >
      <div class="series-header">
        <el-color-picker
          :model-value="seriesItem.color"
          size="small"
          @update:model-value="updateSeries(seriesIndex, 'color', $event as string)"
        />
        <el-input
          :model-value="seriesItem.name"
          placeholder="数据组名称"
          size="small"
          style="flex: 1; margin: 0 10px"
          @update:model-value="updateSeries(seriesIndex, 'name', $event as string)"
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
          v-for="dimension in dimensions"
          :key="dimension.id"
          class="data-row"
        >
          <span class="data-label">{{ dimension.name }}:</span>
          <el-slider
            :model-value="getSeriesDataValue(seriesIndex, dimension.id)"
            :max="dimension.maxValue"
            @update:model-value="
              (v: number) => updateSeriesData(seriesIndex, dimension.id, v)
            "
            style="flex: 1; margin: 0 10px"
          />
          <el-input-number
            :model-value="getSeriesDataValue(seriesIndex, dimension.id)"
            :max="dimension.maxValue"
            size="small"
            controls-position="right"
            @update:model-value="
              (v: number) => updateSeriesData(seriesIndex, dimension.id, v)
            "
          />
        </div>
      </div>
    </div>
    <el-button type="primary" size="small" @click="addSeries">
      <el-icon><Plus /></el-icon>
      添加数据
    </el-button>
  </div>
</template>

<style scoped>
.series-editor {
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.series-item {
  background-color: #fff;
  border-radius: 4px;
  padding: 12px;
  margin-bottom: 16px;
}

.series-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.series-data {
  background-color: #f5f7fa;
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
</style>
