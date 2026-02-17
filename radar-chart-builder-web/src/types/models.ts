// Data model types
// Backend fields: createTime, updateTime
// Frontend fields: createdAt, updatedAt (transformed in API layer)

export interface RadarChart {
  id: number
  title: string
  description: string | null
  dimensions: Dimension[]
  series: DataSeries[]
  createTime: string
  updateTime: string
}

export interface Dimension {
  id: number
  name: string
  description: string | null
  maxValue: number
  displayOrder: number
}

export interface DataSeries {
  id: number
  name: string
  color: string
  displayOrder: number
  data: SeriesData[]
}

export interface SeriesData {
  id: number
  value: number
  dimensionId: number
  seriesId: number
}

// Create/Update DTOs
export interface CreateRadarChartDto {
  title: string
  description?: string
  dimensions: Omit<Dimension, 'id'>[]
  series: CreateDataSeriesDto[]
}

export interface UpdateRadarChartDto {
  title?: string
  description?: string
  dimensions?: Omit<Dimension, 'id'>[]
  series?: UpdateDataSeriesDto[]
}

export interface CreateDataSeriesDto {
  name: string
  color: string
  displayOrder: number
  data: Omit<SeriesData, 'id' | 'seriesId'>[]
}

export interface UpdateDataSeriesDto {
  id: number
  name?: string
  color?: string
  displayOrder?: number
  data?: Omit<SeriesData, 'id' | 'seriesId'>[]
}

// EChart option types
export interface EChartRadarOption {
  title: {
    text: string
    left: 'center'
  }
  tooltip: {
    trigger: 'item'
  }
  legend: {
    data: string[]
    top: 'bottom'
  }
  radar: {
    indicator: Array<{
      name: string
      max: number
    }>
  }
  series: Array<{
    name: string
    type: 'radar'
    data: Array<{
      value: number[]
      name: string
    }>
  }>
}
