// Data model types
// Backend fields: createTime, updateTime
// Frontend fields: createTime, updateTime (consistent with backend)
// Note: IDs are strings to avoid JavaScript number precision loss

export interface RadarChart {
  id: string
  title: string
  description: string | null
  dimensions: Dimension[]
  series: DataSeries[]
  createTime: string
  updateTime: string
}

export interface Dimension {
  id: string
  name: string
  description: string | null
  minValue: number
  maxValue: number
  orderIndex: number  // Backend uses orderIndex
}

export interface DataSeries {
  id: string
  name: string
  color: string  // Frontend only - for UI display
  displayOrder: number  // Frontend only - for UI ordering
  data: SeriesData[]
  createTime?: string  // Backend returns this
}

export interface SeriesData {
  id: string
  value: number
  dimensionId: string
  seriesId: string
}

// Create/Update DTOs
export interface CreateRadarChartDto {
  title: string
  description?: string
  dimensions: CreateDimensionDto[]
  series: CreateDataSeriesDto[]
}

export interface UpdateRadarChartDto {
  title?: string
  description?: string
  dimensions?: CreateDimensionDto[]
  series?: UpdateDataSeriesDto[]
}

export interface CreateDimensionDto {
  name: string
  description?: string
  minValue: number
  maxValue: number
  orderIndex: number  // Backend uses orderIndex
}

export interface CreateDataSeriesDto {
  name: string
  color: string
  displayOrder: number
  data: Omit<SeriesData, 'id' | 'seriesId'>[]
}

export interface UpdateDataSeriesDto {
  id: string
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
