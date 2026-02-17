// Authentication types
// Backend fields: createTime, updateTime

export interface User {
  id: number
  username: string
  email: string
  nickname?: string | null
  createTime: string
  updateTime?: string
}

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  email: string
  password: string
}

export interface AuthResponse {
  user: User
  token: string
}

export interface AuthState {
  user: User | null
  token: string | null
  isAuthenticated: boolean
}
