import { http } from '@/utils/request'
import type { LoginRequest, RegisterRequest, User } from '@/types/auth'

// Auth API - backend returns data directly, no wrapper
export const authApi = {
  /**
   * User login
   * POST /api/auth/login
   * Returns: { token: string, user: User }
   */
  login: (data: LoginRequest) =>
    http.post<{ token: string; user: User }>('/auth/login', data),

  /**
   * User registration
   * POST /api/auth/register
   * Returns: { token: string, user: User }
   */
  register: (data: RegisterRequest) =>
    http.post<{ token: string; user: User }>('/auth/register', data),

  /**
   * Get current user info
   * GET /api/auth/me
   * Returns: User
   */
  getCurrentUser: () =>
    http.get<User>('/auth/me'),

  /**
   * Refresh token
   * POST /api/auth/refresh
   * Returns: { token: string }
   */
  refreshToken: () =>
    http.post<{ token: string }>('/auth/refresh'),
}
