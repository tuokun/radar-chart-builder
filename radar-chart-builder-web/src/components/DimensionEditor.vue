<script setup lang="ts">
import { ref, watch } from 'vue'
import type { Dimension } from '@/types/models'

interface Props {
  dimensions: Omit<Dimension, 'id'>[]
  modelValue: Omit<Dimension, 'id'>[]
}

interface Emits {
  (e: 'update:modelValue', value: Omit<Dimension, 'id'>[]): void
  (e: 'add'): void
  (e: 'remove', index: number): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const localDimensions = ref<Omit<Dimension, 'id'>[]>([
  { name: '维度1', description: '', maxValue: 100, displayOrder: 0 },
  { name: '维度2', description: '', maxValue: 100, displayOrder: 1 },
  { name: '维度3', description: '', maxValue: 100, displayOrder: 2 },
])

watch(
  () => props.modelValue,
  (newVal: Omit<Dimension, 'id'>[]) => {
    localDimensions.value = newVal.map((d) => ({
      name: d.name,
      description: d.description ?? null,
      maxValue: d.maxValue,
      displayOrder: d.displayOrder,
    }))
  },
  { immediate: true, deep: true }
)

const updateDimension = (index: number, field: keyof Omit<Dimension, 'id'>, value: string | number) => {
  if (index < 0 || index >= localDimensions.value.length) return
  const current = localDimensions.value[index]
  if (!current) return

  const updated: Omit<Dimension, 'id'> = {
    name: current.name,
    description: current.description,
    maxValue: current.maxValue,
    displayOrder: current.displayOrder,
    [field]: value,
  }
  localDimensions.value = [
    ...localDimensions.value.slice(0, index),
    updated,
    ...localDimensions.value.slice(index + 1),
  ]
  emit('update:modelValue', localDimensions.value)
}

const addDimension = () => {
  emit('add')
}

const removeDimension = (index: number) => {
  emit('remove', index)
}
</script>

<template>
  <div class="dimension-editor">
    <div
      v-for="(dimension, index) in localDimensions"
      :key="index"
      class="dimension-item"
    >
      <div class="dimension-header">
        <span class="dimension-label">维度 {{ index + 1 }}</span>
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
            :model-value="dimension.name"
            placeholder="名称"
            size="small"
            @update:model-value="updateDimension(index, 'name', $event as string)"
          />
        </el-col>
        <el-col :span="10">
          <el-input-number
            :model-value="dimension.maxValue"
            :min="1"
            size="small"
            controls-position="right"
            style="width: 100%"
            @update:model-value="updateDimension(index, 'maxValue', $event as number)"
          />
        </el-col>
      </el-row>
    </div>
    <el-button type="primary" size="small" @click="addDimension">
      <el-icon><Plus /></el-icon>
      添加维度
    </el-button>
  </div>
</template>

<style scoped>
.dimension-editor {
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.dimension-item {
  background-color: #fff;
  border-radius: 4px;
  padding: 10px;
  margin-bottom: 10px;
}

.dimension-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.dimension-label {
  font-weight: 500;
  font-size: 14px;
}
</style>
