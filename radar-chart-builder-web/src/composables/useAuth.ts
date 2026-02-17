import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import type { LoginRequest, RegisterRequest } from '@/types/auth'

export function useAuth() {
  const authStore = useAuthStore()
  const router = useRouter()

  const login = async (credentials: LoginRequest) => {
    await authStore.login(credentials)
    router.push('/')
  }

  const register = async (data: RegisterRequest) => {
    await authStore.register(data)
    router.push('/')
  }

  const logout = () => {
    authStore.logout()
    router.push('/login')
  }

  const checkAuth = () => {
    if (!authStore.isAuthenticated) {
      router.push('/login')
      return false
    }
    return true
  }

  return {
    user: computed(() => authStore.user),
    isAuthenticated: computed(() => authStore.isAuthenticated),
    login,
    register,
    logout,
    checkAuth,
  }
}
