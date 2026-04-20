import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/views/Layout.vue'
import Dashboard from '@/views/Dashboard.vue'
import UserManagement from '@/views/UserManagement.vue'
import TravelPlanManagement from '@/views/TravelPlanManagement.vue'
import AttractionManagement from '@/views/AttractionManagement.vue'
import NewsManagement from '@/views/NewsManagement.vue'
import NewsPublish from '@/views/NewsPublish.vue'
import Statistics from '@/views/Statistics.vue'
import Login from '@/views/Login.vue'
import { initTheme } from '@/config/theme.js'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: { title: '仪表盘' }
      },
      {
        path: 'users',
        name: 'UserManagement',
        component: UserManagement,
        meta: { title: '用户管理' }
      },
      {
        path: 'travel-plans',
        name: 'TravelPlanManagement',
        component: TravelPlanManagement,
        meta: { title: '旅游计划管理' }
      },
      {
        path: 'attractions',
        name: 'AttractionManagement',
        component: AttractionManagement,
        meta: { title: '景点管理' }
      },
      {
        path: 'news',
        name: 'NewsManagement',
        component: NewsManagement,
        meta: { title: '新闻管理' }
      },
      {
        path: 'news/publish',
        name: 'NewsPublish',
        component: NewsPublish,
        meta: { title: '发布新闻' }
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: Statistics,
        meta: { title: '数据统计' }
      },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const isLoggedIn = localStorage.getItem('admin_token')
  
  // 初始化主题色
  initTheme()
  
  if (to.path === '/login') {
    if (isLoggedIn) {
      next('/')
    } else {
      next()
    }
  } else {
    if (isLoggedIn) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router


