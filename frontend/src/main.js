import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import axios from 'axios'

import App from './App.vue'
import router from './router'
import './style.css'
import { useAuthStore } from './stores/auth'

const app = createApp(App)

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 配置axios
axios.defaults.timeout = 10000

// 请求拦截器
axios.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
axios.interceptors.response.use(
  response => {
    return response
  },
  error => {
    if (error.response?.status === 401) {
      // token过期，清除认证信息
      const authStore = useAuthStore()
      authStore.logout()
      router.push('/login')
    }
    return Promise.reject(error)
  }
)

app.use(createPinia())
app.use(router)
app.use(ElementPlus, {
  locale: zhCn,
})

// 初始化认证状态
const pinia = createPinia()
app.use(pinia)

// 初始化认证状态
const authStore = useAuthStore()
authStore.initAuth()

// 在应用挂载后初始化认证状态
app.mount('#app')



























