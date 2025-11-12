import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/RegisterView.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/home',
      name: 'home',
      component: () => import('../views/HomeView.vue'),
      meta: { requiresAuth: true, role: 'user' }
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('../views/AdminView.vue'),
      meta: { requiresAuth: true, role: 'admin' }
    },
    // 兜底：未知路径重定向到登录
    { path: '/:pathMatch(.*)*', redirect: '/login' }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  // 如果需要认证
  if (to.meta.requiresAuth) {
    if (!userStore.isLoggedIn) {
      // 未登录，跳转到登录页（避免重复跳转同一路径）
      if (to.path !== '/login') return next('/login')
      return next()
    } else if (to.meta.role && userStore.userRole !== to.meta.role) {
      // 角色不匹配，跳转到对应角色的首页
      if (userStore.userRole === 'admin') {
        if (to.path !== '/admin') return next('/admin')
        return next()
      } else {
        if (to.path !== '/home') return next('/home')
        return next()
      }
    } else {
      next()
    }
  } else {
    // 如果已登录，访问登录/注册页时重定向到首页
    if (userStore.isLoggedIn && (to.path === '/login' || to.path === '/register')) {
      if (userStore.userRole === 'admin') {
        if (to.path !== '/admin') return next('/admin')
        return next()
      } else {
        if (to.path !== '/home') return next('/home')
        return next()
      }
    } else {
      next()
    }
  }
})

export default router
