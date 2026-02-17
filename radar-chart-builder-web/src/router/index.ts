import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/radar/RadarListView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/auth/LoginView.vue'),
      meta: { guest: true },
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/auth/RegisterView.vue'),
      meta: { guest: true },
    },
    {
      path: '/radar/new',
      name: 'radar-create',
      component: () => import('@/views/radar/RadarEditView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/radar/:id',
      name: 'radar-edit',
      component: () => import('@/views/radar/RadarEditView.vue'),
      meta: { requiresAuth: true },
    },
  ],
})

// Navigation guards
router.beforeEach((to, _from, next) => {
  const authStore = useAuthStore()

  // Initialize auth state
  authStore.initAuth()

  const requiresAuth = to.meta.requiresAuth
  const isGuestRoute = to.meta.guest

  if (requiresAuth && !authStore.isAuthenticated) {
    // Redirect to login if trying to access protected route
    next({ name: 'login', query: { redirect: to.fullPath } })
  } else if (isGuestRoute && authStore.isAuthenticated) {
    // Redirect to home if already authenticated
    next({ name: 'home' })
  } else {
    next()
  }
})

export default router
