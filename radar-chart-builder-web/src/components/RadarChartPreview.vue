<script setup lang="ts">
import { computed, ref, onBeforeUnmount, watch } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { RadarChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
} from 'echarts/components'
import type { RadarChart as RadarChartModel } from '@/types/models'
import type { EChartsOption } from 'echarts'

use([
  CanvasRenderer,
  RadarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
])

interface Props {
  chart: RadarChartModel
  height?: number
}

const props = withDefaults(defineProps<Props>(), {
  height: 400,
})

const isReady = ref(false)
const chartKey = computed(() => `chart-${props.chart.id}-${Date.now()}`)

// 确保 chart 数据有效后才渲染
watch(() => props.chart, (newChart) => {
  console.log('RadarChartPreview watch:', {
    hasChart: !!newChart,
    hasDimensions: !!newChart?.dimensions,
    dimensionsLength: newChart?.dimensions?.length,
    hasSeries: !!newChart?.series,
    seriesLength: newChart?.series?.length,
    firstSeriesData: newChart?.series?.[0]?.data,
    isReady: !!(newChart && newChart.dimensions && newChart.dimensions.length >= 3)
  })
  // 只需要有维度信息就可以渲染，系列数据可选
  isReady.value = !!(newChart && newChart.dimensions && newChart.dimensions.length >= 3)
}, { immediate: true, deep: true })

const option = computed<EChartsOption>(() => {
  if (!isReady.value) {
    return {}
  }

  // Sort dimensions by order index
  const sortedDimensions = [...props.chart.dimensions].sort(
    (a, b) => a.orderIndex - b.orderIndex
  )

  // Sort series by display order
  const sortedSeries = [...props.chart.series].sort((a, b) => a.displayOrder - b.displayOrder)

  // 如果没有系列数据，生成一个默认的预览系列（使用各维度平均值）
  const displaySeries = sortedSeries.length > 0 ? sortedSeries : [{
    id: 'preview',
    name: '预览',
    color: '#409eff',
    displayOrder: 0,
    data: sortedDimensions.map((d) => ({
      id: `preview-${d.id}`,
      dimensionId: d.id,
      seriesId: 'preview',
      value: (d.minValue + d.maxValue) / 2,
    })),
  }]

  return {
    tooltip: {
      trigger: 'item',
      confine: true,
    },
    legend: displaySeries.length > 1 ? {
      data: displaySeries.map((s) => s.name),
      top: 'bottom',
    } : undefined,
    radar: {
      indicator: sortedDimensions.map((d) => ({
        name: d.name,
        min: d.minValue ?? 0,
        max: d.maxValue,
      })),
      radius: '60%',
    },
    series: [
      {
        name: props.chart.title,
        type: 'radar',
        data: displaySeries.map((series) => {
          const values = sortedDimensions.map((dim) => {
            const dataPoint = series.data?.find((d) => d.dimensionId === dim.id)
            return dataPoint?.value ?? ((dim.minValue + dim.maxValue) / 2)
          })

          return {
            value: values,
            name: series.name,
            itemStyle: {
              color: series.color,
            },
            areaStyle: {
              color: series.color,
            },
          }
        }),
      },
    ],
  }
})

onBeforeUnmount(() => {
  isReady.value = false
})
</script>

<template>
  <div class="chart-container" :style="{ height: `${height}px`, width: '100%' }" v-if="isReady">
    <v-chart :option="option" autoresize :style="{ width: '100%', height: '100%' }" />
  </div>
</template>

<style scoped>
.chart-container {
  min-width: 300px;
  width: 100%;
}
</style>
