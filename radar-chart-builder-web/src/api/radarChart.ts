import { http } from '@/utils/request'
import type { RadarChart, CreateRadarChartDto, UpdateRadarChartDto } from '@/types/models'

export const radarChartApi = {
  /**
   * Get user's radar charts
   * GET /api/radar-charts
   * Returns: RadarChart[] (array, not paginated)
   */
  getList: () =>
    http.get<RadarChart[]>('/radar-charts'),

  /**
   * Get radar chart by ID
   * GET /api/radar-charts/{id}
   * Returns: RadarChart
   */
  getById: (id: number) =>
    http.get<RadarChart>(`/radar-charts/${id}`),

  /**
   * Create a new radar chart
   * POST /api/radar-charts
   * Returns: RadarChart
   */
  create: (data: CreateRadarChartDto) =>
    http.post<RadarChart>('/radar-charts', data),

  /**
   * Update an existing radar chart
   * PUT /api/radar-charts/{id}
   * Returns: RadarChart
   */
  update: (id: number, data: UpdateRadarChartDto) =>
    http.put<RadarChart>(`/radar-charts/${id}`, data),

  /**
   * Delete a radar chart
   * DELETE /api/radar-charts/{id}
   * Returns: void
   */
  delete: (id: number) =>
    http.delete<void>(`/radar-charts/${id}`),
}
