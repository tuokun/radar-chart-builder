import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      redirect: '/radar',
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
      path: '/',
      component: () => import('@/components/MainLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: 'radar',
          name: 'radar-list',
          component: () => import('@/views/radar/RadarListView.vue'),
        },
        {
          path: 'radar/new',
          name: 'radar-create',
          component: () => import('@/views/radar/RadarFormView.vue'),
        },
        {
          path: 'radar/:id/edit',
          name: 'radar-edit',
          component: () => import('@/views/radar/RadarFormView.vue'),
        },
        {
          path: 'series/:id/preview',
          name: 'series-preview',
          component: () => import('@/views/series/SeriesPreviewView.vue'),
        },
        {
          path: 'series/:id',
          name: 'series-detail',
          component: () => import('@/views/series/SeriesDetailView.vue'),
        },
      ],
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
    next({ name: 'radar-list' })
  } else {
    next()
  }
})

export default router
