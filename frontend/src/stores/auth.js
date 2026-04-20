import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const user = ref(null)
  const token = ref(localStorage.getItem('token') || null)
  const isAuthenticated = ref(false)
  const loading = ref(false)

  // 计算属性
  const isLoggedIn = computed(() => isAuthenticated.value && !!token.value)
  const userInfo = computed(() => user.value)

  // 初始化认证状态
  const initAuth = () => {
    const savedToken = localStorage.getItem('token')
    const savedUser = localStorage.getItem('user')
    
    if (savedToken && savedUser) {
      token.value = savedToken
      user.value = JSON.parse(savedUser)
      isAuthenticated.value = true
      
      // 设置axios默认请求头
      axios.defaults.headers.common['Authorization'] = `Bearer ${savedToken}`
    } else {
      // 如果没有保存的用户信息，设置默认用户
      const defaultUser = {
        id: 1,
        username: 'admin',
        email: 'admin@travel.com',
        nickname: '管理员',
        role: 'ADMIN'
      }
      
      user.value = defaultUser
      isAuthenticated.value = true
      
      // 保存默认用户信息
      localStorage.setItem('user', JSON.stringify(defaultUser))
      localStorage.setItem('token', 'default-token')
      
      console.log('使用默认用户:', defaultUser)
    }
  }

  // 登录
  const login = async (username, password) => {
    try {
      loading.value = true
      
      // 模拟API调用 - 实际项目中应该调用真实的后端API
      const response = await mockLoginAPI(username, password)
      
      if (response.success) {
        // 保存认证信息
        token.value = response.token
        user.value = response.user
        isAuthenticated.value = true
        
        // 保存到本地存储
        localStorage.setItem('token', response.token)
        localStorage.setItem('user', JSON.stringify(response.user))
        
        // 设置axios默认请求头
        axios.defaults.headers.common['Authorization'] = `Bearer ${response.token}`
        
        return { success: true, user: response.user }
      } else {
        return { success: false, message: response.message }
      }
    } catch (error) {
      console.error('登录错误:', error)
      return { success: false, message: '登录过程中发生错误' }
    } finally {
      loading.value = false
    }
  }

  // 登出
  const logout = () => {
    // 清除状态
    user.value = null
    token.value = null
    isAuthenticated.value = false
    
    // 清除本地存储
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    localStorage.removeItem('rememberMe')
    localStorage.removeItem('username')
    
    // 清除axios默认请求头
    delete axios.defaults.headers.common['Authorization']
  }

  // 更新用户信息
  const updateUser = (userData) => {
    user.value = { ...user.value, ...userData }
    localStorage.setItem('user', JSON.stringify(user.value))
  }

  // 检查token是否有效
  const checkToken = async () => {
    if (!token.value) return false
    
    try {
      // 这里应该调用后端API验证token
      // 暂时返回true
      return true
    } catch (error) {
      logout()
      return false
    }
  }

  // 模拟登录API - 实际项目中应该替换为真实的API调用
  const mockLoginAPI = async (username, password) => {
    // 模拟网络延迟
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 模拟用户数据
    const users = {
      'admin': {
        id: 1,
        username: 'admin',
        email: 'admin@travel.com',
        nickname: '管理员',
        avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face',
        role: 'ADMIN'
      },
      'alice': {
        id: 2,
        username: 'alice',
        email: 'alice@travel.com',
        nickname: '爱丽丝',
        avatar: 'https://images.unsplash.com/photo-1494790108755-2616b612b786?w=150&h=150&fit=crop&crop=face',
        role: 'USER'
      },
      'bob': {
        id: 3,
        username: 'bob',
        email: 'bob@travel.com',
        nickname: '鲍勃',
        avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face',
        role: 'USER'
      }
    }
    
    // 检查用户名和密码
    if (users[username] && password === 'admin123') {
      return {
        success: true,
        token: `mock_token_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
        user: users[username]
      }
    } else {
      return {
        success: false,
        message: '用户名或密码错误'
      }
    }
  }

  // 真实API调用示例（注释掉，实际使用时取消注释）
  /*
  const realLoginAPI = async (username, password) => {
    try {
      const response = await axios.post('/api/auth/login', {
        username,
        password
      })
      
      return {
        success: true,
        token: response.data.token,
        user: response.data.user
      }
    } catch (error) {
      return {
        success: false,
        message: error.response?.data?.message || '登录失败'
      }
    }
  }
  */

  return {
    // 状态
    user,
    token,
    isAuthenticated,
    loading,
    
    // 计算属性
    isLoggedIn,
    userInfo,
    
    // 方法
    initAuth,
    login,
    logout,
    updateUser,
    checkToken
  }
})
