import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRadarChartStore } from '@/stores/radarChart'
import type { CreateRadarChartDto, UpdateRadarChartDto } from '@/types/models'

export function useChart() {
  const radarChartStore = useRadarChartStore()
  const router = useRouter()
  const route = useRoute()

  const fetchList = async () => {
    await radarChartStore.fetchList()
  }

  const fetchById = async (id: number) => {
    const chart = await radarChartStore.fetchById(id)
    return chart
  }

  const createChart = async (data: CreateRadarChartDto) => {
    const chart = await radarChartStore.create(data)
    if (chart) {
      ElMessage.success('雷达图创建成功')
      router.push('/')
    }
    return chart
  }

  const updateChart = async (id: number, data: UpdateRadarChartDto) => {
    const chart = await radarChartStore.update(id, data)
    if (chart) {
      ElMessage.success('雷达图更新成功')
    }
    return chart
  }

  const deleteChart = async (id: number) => {
    try {
      await ElMessageBox.confirm('确定要删除这个雷达图吗？', '确认删除', {
        type: 'warning',
      })
      const success = await radarChartStore.remove(id)
      if (success) {
        ElMessage.success('雷达图删除成功')
      }
      return success
    } catch {
      // User cancelled
      return false
    }
  }

  const editChart = (id: number) => {
    router.push(`/radar/${id}`)
  }

  const createNew = () => {
    router.push('/radar/new')
  }

  const getCurrentChartId = (): number | string => {
    return route.params.id as string
  }

  const isEditMode = computed(() => {
    return route.params.id !== 'new' && !!route.params.id
  })

  return {
    charts: computed(() => radarChartStore.charts),
    currentChart: computed(() => radarChartStore.currentChart),
    loading: computed(() => radarChartStore.loading),
    fetchList,
    fetchById,
    createChart,
    updateChart,
    deleteChart,
    editChart,
    createNew,
    getCurrentChartId,
    isEditMode,
  }
}
