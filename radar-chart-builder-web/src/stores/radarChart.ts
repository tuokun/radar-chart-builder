import { defineStore } from 'pinia'
import { ref } from 'vue'
import { radarChartApi } from '@/api/radarChart'
import type { RadarChart, CreateRadarChartDto, UpdateRadarChartDto } from '@/types/models'

export const useRadarChartStore = defineStore('radarChart', () => {
  // State
  const charts = ref<RadarChart[]>([])
  const currentChart = ref<RadarChart | null>(null)
  const loading = ref(false)

  // Actions
  async function fetchList(): Promise<void> {
    loading.value = true
    try {
      // Backend returns array directly, not paginated
      charts.value = await radarChartApi.getList()
    } finally {
      loading.value = false
    }
  }

  async function fetchById(id: number): Promise<RadarChart | null> {
    loading.value = true
    try {
      const chart = await radarChartApi.getById(id)
      currentChart.value = chart
      return chart
    } catch {
      return null
    } finally {
      loading.value = false
    }
  }

  async function create(data: CreateRadarChartDto): Promise<RadarChart | null> {
    loading.value = true
    try {
      const newChart = await radarChartApi.create(data)

      // Add to beginning of list
      charts.value.unshift(newChart)

      return newChart
    } catch {
      return null
    } finally {
      loading.value = false
    }
  }

  async function update(id: number, data: UpdateRadarChartDto): Promise<RadarChart | null> {
    loading.value = true
    try {
      const updatedChart = await radarChartApi.update(id, data)

      // Update in list
      const index = charts.value.findIndex((c) => c.id === id)
      if (index !== -1) {
        charts.value[index] = updatedChart
      }

      // Update current chart if it matches
      if (currentChart.value?.id === id) {
        currentChart.value = updatedChart
      }

      return updatedChart
    } catch {
      return null
    } finally {
      loading.value = false
    }
  }

  async function remove(id: number): Promise<boolean> {
    loading.value = true
    try {
      await radarChartApi.delete(id)

      // Remove from list
      charts.value = charts.value.filter((c) => c.id !== id)

      // Clear current chart if it matches
      if (currentChart.value?.id === id) {
        currentChart.value = null
      }

      return true
    } catch {
      return false
    } finally {
      loading.value = false
    }
  }

  function clearCurrentChart(): void {
    currentChart.value = null
  }

  return {
    charts,
    currentChart,
    loading,
    fetchList,
    fetchById,
    create,
    update,
    remove,
    clearCurrentChart,
  }
})
