// API Response types
// Note: Backend returns data directly without wrapping in { code, message, data }

export interface ApiError {
  code?: number
  message?: string
  details?: string
}

export interface PaginationParams {
  page?: number
  size?: number
  sort?: string
}

// Pagination response from backend (if using pagination)
export interface PageResult<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
  first: boolean
  last: boolean
}
