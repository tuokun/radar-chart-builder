<script setup lang="ts">
import { computed } from 'vue'
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

const option = computed<EChartsOption>(() => {
  // Sort dimensions by display order
  const sortedDimensions = [...props.chart.dimensions].sort(
    (a, b) => a.displayOrder - b.displayOrder
  )

  // Sort series by display order
  const sortedSeries = [...props.chart.series].sort((a, b) => a.displayOrder - b.displayOrder)

  return {
    title: {
      text: props.chart.title,
      left: 'center',
      textStyle: {
        fontSize: 16,
      },
    },
    tooltip: {
      trigger: 'item',
      confine: true,
    },
    legend: {
      data: sortedSeries.map((s) => s.name),
      top: 'bottom',
    },
    radar: {
      indicator: sortedDimensions.map((d) => ({
        name: d.name,
        max: d.maxValue,
      })),
      radius: '60%',
    },
    series: [
      {
        name: props.chart.title,
        type: 'radar',
        data: sortedSeries.map((series) => {
          const values = sortedDimensions.map((dim) => {
            const dataPoint = series.data.find((d) => d.dimensionId === dim.id)
            return dataPoint?.value ?? 0
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
</script>

<template>
  <div :style="{ height: `${height}px` }">
    <v-chart :option="option" autoresize />
  </div>
</template>
