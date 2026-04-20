// API接口文件
import { mockUsers, mockTravelPlans, mockAttractions, mockStatistics, mockTopAttractions, mockTopUsers } from './mockData.js'

// 模拟API延迟
const delay = (ms = 500) => new Promise(resolve => setTimeout(resolve, ms))

// 用户相关API
export const userAPI = {
  // 获取用户列表
  getUsers: async (params = {}) => {
    await delay()
    let users = [...mockUsers]
    
    // 模拟搜索过滤
    if (params.username) {
      users = users.filter(user => 
        user.username.toLowerCase().includes(params.username.toLowerCase())
      )
    }
    if (params.email) {
      users = users.filter(user => 
        user.email.toLowerCase().includes(params.email.toLowerCase())
      )
    }
    if (params.status) {
      users = users.filter(user => user.status === params.status)
    }
    
    return {
      data: users,
      total: users.length,
      success: true
    }
  },

  // 获取用户详情
  getUser: async (id) => {
    await delay()
    const user = mockUsers.find(u => u.id === parseInt(id))
    return {
      data: user,
      success: !!user
    }
  },

  // 创建用户
  createUser: async (userData) => {
    await delay()
    const newUser = {
      id: Date.now(),
      ...userData,
      avatar: '',
      createdAt: new Date().toLocaleString(),
      lastLogin: '从未登录'
    }
    mockUsers.unshift(newUser)
    return {
      data: newUser,
      success: true
    }
  },

  // 更新用户
  updateUser: async (id, userData) => {
    await delay()
    const index = mockUsers.findIndex(u => u.id === parseInt(id))
    if (index > -1) {
      mockUsers[index] = { ...mockUsers[index], ...userData }
      return {
        data: mockUsers[index],
        success: true
      }
    }
    return {
      data: null,
      success: false,
      message: '用户不存在'
    }
  },

  // 删除用户
  deleteUser: async (id) => {
    await delay()
    const index = mockUsers.findIndex(u => u.id === parseInt(id))
    if (index > -1) {
      mockUsers.splice(index, 1)
      return { success: true }
    }
    return { success: false, message: '用户不存在' }
  }
}

// 旅游计划相关API
export const travelPlanAPI = {
  // 获取旅游计划列表
  getTravelPlans: async (params = {}) => {
    await delay()
    let plans = [...mockTravelPlans]
    
    // 模拟搜索过滤
    if (params.title) {
      plans = plans.filter(plan => 
        plan.title.toLowerCase().includes(params.title.toLowerCase())
      )
    }
    if (params.destination) {
      plans = plans.filter(plan => {
        const destination = plan.destinations?.[0]?.name || ''
        return destination.toLowerCase().includes(params.destination.toLowerCase())
      })
    }
    if (params.status) {
      plans = plans.filter(plan => plan.status === params.status)
    }
    
    return {
      data: plans,
      total: plans.length,
      success: true
    }
  },

  // 获取旅游计划详情
  getTravelPlan: async (id) => {
    await delay()
    const plan = mockTravelPlans.find(p => p.id === parseInt(id))
    return {
      data: plan,
      success: !!plan
    }
  },

  // 创建旅游计划
  createTravelPlan: async (planData) => {
    await delay()
    const newPlan = {
      id: Date.now(),
      ...planData,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }
    mockTravelPlans.unshift(newPlan)
    return {
      data: newPlan,
      success: true
    }
  },

  // 更新旅游计划
  updateTravelPlan: async (id, planData) => {
    await delay()
    const index = mockTravelPlans.findIndex(p => p.id === parseInt(id))
    if (index > -1) {
      mockTravelPlans[index] = { 
        ...mockTravelPlans[index], 
        ...planData,
        updatedAt: new Date().toISOString()
      }
      return {
        data: mockTravelPlans[index],
        success: true
      }
    }
    return {
      data: null,
      success: false,
      message: '旅游计划不存在'
    }
  },

  // 删除旅游计划
  deleteTravelPlan: async (id) => {
    await delay()
    const index = mockTravelPlans.findIndex(p => p.id === parseInt(id))
    if (index > -1) {
      mockTravelPlans.splice(index, 1)
      return { success: true }
    }
    return { success: false, message: '旅游计划不存在' }
  }
}

// 景点相关API
export const attractionAPI = {
  // 获取景点列表
  getAttractions: async (params = {}) => {
    await delay()
    let attractions = [...mockAttractions]
    
    // 模拟搜索过滤
    if (params.name) {
      attractions = attractions.filter(attraction => 
        attraction.name.toLowerCase().includes(params.name.toLowerCase())
      )
    }
    if (params.city) {
      attractions = attractions.filter(attraction => 
        attraction.city.toLowerCase().includes(params.city.toLowerCase())
      )
    }
    if (params.type) {
      attractions = attractions.filter(attraction => attraction.type === params.type)
    }
    if (params.rating) {
      const minRating = parseFloat(params.rating)
      attractions = attractions.filter(attraction => attraction.rating >= minRating)
    }
    
    return {
      data: attractions,
      total: attractions.length,
      success: true
    }
  },

  // 获取景点详情
  getAttraction: async (id) => {
    await delay()
    const attraction = mockAttractions.find(a => a.id === parseInt(id))
    return {
      data: attraction,
      success: !!attraction
    }
  },

  // 创建景点
  createAttraction: async (attractionData) => {
    await delay()
    const newAttraction = {
      id: Date.now(),
      ...attractionData
    }
    mockAttractions.unshift(newAttraction)
    return {
      data: newAttraction,
      success: true
    }
  },

  // 更新景点
  updateAttraction: async (id, attractionData) => {
    await delay()
    const index = mockAttractions.findIndex(a => a.id === parseInt(id))
    if (index > -1) {
      mockAttractions[index] = { ...mockAttractions[index], ...attractionData }
      return {
        data: mockAttractions[index],
        success: true
      }
    }
    return {
      data: null,
      success: false,
      message: '景点不存在'
    }
  },

  // 删除景点
  deleteAttraction: async (id) => {
    await delay()
    const index = mockAttractions.findIndex(a => a.id === parseInt(id))
    if (index > -1) {
      mockAttractions.splice(index, 1)
      return { success: true }
    }
    return { success: false, message: '景点不存在' }
  }
}

// 统计数据相关API
export const statisticsAPI = {
  // 获取概览数据
  getOverview: async () => {
    await delay()
    return {
      data: mockStatistics.overview,
      success: true
    }
  },

  // 获取用户增长数据
  getUserGrowth: async (period = '30d') => {
    await delay()
    return {
      data: mockStatistics.userGrowth[period] || mockStatistics.userGrowth['30d'],
      success: true
    }
  },

  // 获取旅游计划状态分布
  getPlanStatus: async () => {
    await delay()
    return {
      data: mockStatistics.planStatus,
      success: true
    }
  },

  // 获取热门目的地排行
  getDestinationRanking: async () => {
    await delay()
    return {
      data: mockStatistics.destinationRanking,
      success: true
    }
  },

  // 获取收入数据
  getRevenue: async (period = '6m') => {
    await delay()
    return {
      data: mockStatistics.revenue[period] || mockStatistics.revenue['6m'],
      success: true
    }
  },

  // 获取用户活跃度数据
  getUserActivity: async () => {
    await delay()
    return {
      data: mockStatistics.userActivity,
      success: true
    }
  },

  // 获取景点类型分布
  getAttractionTypes: async () => {
    await delay()
    return {
      data: mockStatistics.attractionTypes,
      success: true
    }
  },

  // 获取热门景点排行
  getTopAttractions: async () => {
    await delay()
    return {
      data: mockTopAttractions,
      success: true
    }
  },

  // 获取用户活跃度排行
  getTopUsers: async () => {
    await delay()
    return {
      data: mockTopUsers,
      success: true
    }
  }
}

// 导出所有API
export default {
  user: userAPI,
  travelPlan: travelPlanAPI,
  attraction: attractionAPI,
  statistics: statisticsAPI
}


