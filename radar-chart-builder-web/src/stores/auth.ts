import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'
import type { User, LoginRequest, RegisterRequest } from '@/types/auth'

export const useAuthStore = defineStore('auth', () => {
  // State
  const user = ref<User | null>(null)
  const token = ref<string | null>(null)

  // Getters
  const isAuthenticated = computed(() => !!token.value)
  const currentUser = computed(() => user.value)

  // Actions
  function initAuth() {
    const storedToken = localStorage.getItem('token')
    const storedUser = localStorage.getItem('user')

    if (storedToken) {
      token.value = storedToken
    }

    if (storedUser) {
      try {
        user.value = JSON.parse(storedUser)
      } catch {
        localStorage.removeItem('user')
      }
    }
  }

  async function login(credentials: LoginRequest): Promise<void> {
    const authData = await authApi.login(credentials)

    token.value = authData.token
    user.value = authData.user

    localStorage.setItem('token', authData.token)
    localStorage.setItem('user', JSON.stringify(authData.user))
  }

  async function register(data: RegisterRequest): Promise<void> {
    const authData = await authApi.register(data)

    token.value = authData.token
    user.value = authData.user

    localStorage.setItem('token', authData.token)
    localStorage.setItem('user', JSON.stringify(authData.user))
  }

  async function fetchCurrentUser(): Promise<void> {
    const userData = await authApi.getCurrentUser()
    user.value = userData
    localStorage.setItem('user', JSON.stringify(userData))
  }

  function logout(): void {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return {
    user,
    token,
    isAuthenticated,
    currentUser,
    initAuth,
    login,
    register,
    fetchCurrentUser,
    logout,
  }
})
