import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'
import { useAuthStore } from './auth'

export const useTravelPlanStore = defineStore('travelPlan', () => {
  const plans = ref([])
  const currentPlan = ref(null)
  const loading = ref(false)

  // API基础URL
  const API_BASE_URL = '/api'

  // 生成旅游计划
  const generateTravelPlan = async (planData) => {
    loading.value = true
    try {
      console.log('发送到后端的原始数据:', planData)
      
      // 确保数据格式正确
      const formattedData = {
        title: String(planData.title || ''),
        destination: String(planData.destination || ''),
        startDate: planData.startDate,
        endDate: planData.endDate,
        budget: Number(planData.budget) || 0,
        days: Number(planData.days) || 1,
        travelStyle: String(planData.travelStyle || ''),
        interests: Array.isArray(planData.interests) ? planData.interests : [],
        groupSize: Number(planData.groupSize) || 1,
        accommodationType: String(planData.accommodationType || ''),
        transportation: String(planData.transportation || ''),
        description: String(planData.description || '')
      }
      
      console.log('格式化后的数据:', formattedData)
      
      try {
        // 获取当前用户信息
        const authStore = useAuthStore()
        const currentUser = authStore.userInfo
        
        const response = await axios.post(`/api/travel-plans/generate`, formattedData, {
          headers: {
            'Content-Type': 'application/json',
            'X-User-ID': currentUser?.id || 1 // 传递用户ID
          }
        })
        
        console.log('后端响应:', response.data)
        const newPlan = response.data
        plans.value.unshift(newPlan)
        currentPlan.value = newPlan
        // 保存到本地存储
        saveTravelPlan(newPlan)
        return newPlan
      } catch (apiError) {
        console.warn('API调用失败，使用模拟数据:', apiError)
        
        // 如果API调用失败，使用模拟数据
        const mockPlan = generateMockPlan(formattedData)
        plans.value.unshift(mockPlan)
        currentPlan.value = mockPlan
        // 保存到本地存储
        saveTravelPlan(mockPlan)
        return mockPlan
      }
    } catch (error) {
      console.error('生成旅游计划失败:', error)
      console.error('错误详情:', error.response?.data)
      throw error
    } finally {
      loading.value = false
    }
  }
  
  // 生成模拟计划数据
  const generateMockPlan = (planData) => {
    const planId = Date.now()
    const destination = planData.destination || '北京'
    
    // 根据目的地生成景点数据
    const attractions = getMockAttractions(destination, planData.travelStyle)
    
    return {
      id: planId,
      title: planData.title,
      description: `AI智能推荐的${destination}旅游计划，${getTravelStyleDescription(planData.travelStyle)}`,
      startDate: planData.startDate,
      endDate: planData.endDate,
      budget: planData.budget,
      status: 'CONFIRMED',
      aiGenerated: true,
      userId: 1,
      destinations: [{
        id: planId + 1,
        name: destination,
        description: `AI智能推荐的${destination}旅游目的地`,
        latitude: getCityLatitude(destination),
        longitude: getCityLongitude(destination),
        address: `${destination}市中心`,
        city: destination,
        country: '中国',
        type: 'CITY',
        rating: 4.5,
        visitDuration: planData.days * 480, // 每天8小时
        estimatedCost: planData.budget * 0.6,
        dayOrder: 1,
        timeOrder: 1,
        attractions: attractions
      }],
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }
  }
  
  // 获取模拟景点数据
  const getMockAttractions = (destination, travelStyle) => {
    const cityAttractions = {
      '北京': [
        { name: '故宫博物院', description: '明清两朝的皇家宫殿，世界文化遗产', type: 'MUSEUM', rating: 4.8, visitDuration: 180, ticketPrice: 60 },
        { name: '天安门广场', description: '世界最大的城市广场', type: 'MONUMENT', rating: 4.6, visitDuration: 60, ticketPrice: 0 },
        { name: '颐和园', description: '中国古典园林之首，皇家园林', type: 'PARK', rating: 4.7, visitDuration: 120, ticketPrice: 30 },
        { name: '八达岭长城', description: '万里长城最著名的一段', type: 'MONUMENT', rating: 4.9, visitDuration: 240, ticketPrice: 40 }
      ],
      '上海': [
        { name: '外滩', description: '上海最著名的观光景点，万国建筑博览群', type: 'MONUMENT', rating: 4.7, visitDuration: 90, ticketPrice: 0 },
        { name: '东方明珠', description: '上海地标性建筑，468米高塔', type: 'BUILDING', rating: 4.5, visitDuration: 120, ticketPrice: 160 },
        { name: '豫园', description: '明代私人花园，江南古典园林', type: 'GARDEN', rating: 4.4, visitDuration: 90, ticketPrice: 30 },
        { name: '上海博物馆', description: '中国古代艺术博物馆', type: 'MUSEUM', rating: 4.6, visitDuration: 150, ticketPrice: 0 },
        { name: '南京路步行街', description: '上海最著名的商业街', type: 'SHOPPING', rating: 4.3, visitDuration: 120, ticketPrice: 0 },
        { name: '田子坊', description: '上海最具创意和艺术气息的弄堂', type: 'CULTURAL', rating: 4.2, visitDuration: 90, ticketPrice: 0 },
        { name: '新天地', description: '上海时尚地标，石库门建筑群', type: 'SHOPPING', rating: 4.4, visitDuration: 120, ticketPrice: 0 },
        { name: '朱家角古镇', description: '上海保存最完整的江南水乡古镇', type: 'HISTORIC', rating: 4.3, visitDuration: 180, ticketPrice: 0 },
        { name: '上海科技馆', description: '中国重要的科普教育基地', type: 'MUSEUM', rating: 4.5, visitDuration: 180, ticketPrice: 60 },
        { name: '迪士尼乐园', description: '中国大陆首座迪士尼主题乐园', type: 'ENTERTAINMENT', rating: 4.6, visitDuration: 480, ticketPrice: 399 },
        { name: '陆家嘴金融区', description: '上海最现代化的金融中心', type: 'BUILDING', rating: 4.5, visitDuration: 120, ticketPrice: 0 },
        { name: '上海中心大厦', description: '中国第一高楼，632米', type: 'BUILDING', rating: 4.6, visitDuration: 120, ticketPrice: 180 },
        { name: '环球金融中心', description: '上海第二高楼，492米', type: 'BUILDING', rating: 4.4, visitDuration: 90, ticketPrice: 120 },
        { name: '金茂大厦', description: '上海第三高楼，420米', type: 'BUILDING', rating: 4.3, visitDuration: 90, ticketPrice: 100 },
        { name: '上海海洋水族馆', description: '亚洲最大的海洋水族馆之一', type: 'MUSEUM', rating: 4.4, visitDuration: 180, ticketPrice: 160 }
      ],
      '杭州': [
        { name: '西湖', description: '世界文化遗产，中国最著名的湖泊', type: 'PARK', rating: 4.8, visitDuration: 240, ticketPrice: 0 },
        { name: '雷峰塔', description: '白蛇传说的发源地', type: 'MONUMENT', rating: 4.5, visitDuration: 90, ticketPrice: 40 },
        { name: '灵隐寺', description: '江南著名古刹', type: 'RELIGIOUS', rating: 4.6, visitDuration: 120, ticketPrice: 45 },
        { name: '三潭印月', description: '西湖十景之一', type: 'PARK', rating: 4.7, visitDuration: 60, ticketPrice: 20 }
      ]
    }
    
    const attractions = cityAttractions[destination] || cityAttractions['北京']
    
    // 根据旅游风格筛选景点，放宽筛选条件以显示更多景点
    let filteredAttractions = attractions.filter(attraction => {
      switch (travelStyle) {
        case 'relax':
          return attraction.type === 'PARK' || attraction.type === 'GARDEN' || attraction.type === 'HISTORIC'
        case 'culture':
          return attraction.type === 'MUSEUM' || attraction.type === 'MONUMENT' || attraction.type === 'RELIGIOUS' || attraction.type === 'CULTURAL' || attraction.type === 'HISTORIC'
        case 'food':
          return attraction.type === 'HISTORIC' || attraction.type === 'FOOD' || attraction.type === 'SHOPPING'
        case 'nature':
          return attraction.type === 'PARK' || attraction.type === 'MONUMENT' || attraction.type === 'GARDEN'
        case 'adventure':
          return attraction.type === 'MONUMENT' || attraction.type === 'ENTERTAINMENT' || attraction.type === 'PARK'
        case 'shopping':
          return attraction.type === 'BUILDING' || attraction.type === 'SHOPPING' || attraction.type === 'HISTORIC'
        default:
          return true
      }
    })
    
    // 如果筛选后的景点太少，补充一些通用景点
    if (filteredAttractions.length < 6) {
      const additionalAttractions = attractions.filter(attraction => 
        !filteredAttractions.includes(attraction) && 
        (attraction.type === 'PARK' || attraction.type === 'MONUMENT' || attraction.type === 'MUSEUM')
      )
      filteredAttractions = [...filteredAttractions, ...additionalAttractions.slice(0, 6 - filteredAttractions.length)]
    }
    
    // 根据天数决定景点数量，每天3-4个景点，增加总景点数量
    const maxAttractions = Math.min(filteredAttractions.length, 15) // 最多15个景点
    return filteredAttractions.slice(0, maxAttractions)
  }
  
  // 获取旅游风格描述
  const getTravelStyleDescription = (travelStyle) => {
    const descriptions = {
      'relax': '适合休闲度假，享受慢生活',
      'culture': '深度体验当地历史文化',
      'food': '品尝地道美食，感受当地饮食文化',
      'nature': '亲近自然，欣赏美丽风光',
      'adventure': '挑战自我，体验刺激活动',
      'shopping': '购物娱乐，享受都市生活'
    }
    return descriptions[travelStyle] || '个性化旅游体验'
  }
  
  // 获取城市坐标
  const getCityLatitude = (city) => {
    const coordinates = {
      '北京': 39.9042,
      '上海': 31.2304,
      '杭州': 30.2741,
      '苏州': 31.2989,
      '成都': 30.5728,
      '西安': 34.3416,
      '广州': 23.1291,
      '深圳': 22.5431
    }
    return coordinates[city] || 39.9042
  }
  
  const getCityLongitude = (city) => {
    const coordinates = {
      '北京': 116.4074,
      '上海': 121.4737,
      '杭州': 120.1551,
      '苏州': 120.5853,
      '成都': 104.0668,
      '西安': 108.9398,
      '广州': 113.2644,
      '深圳': 114.0579
    }
    return coordinates[city] || 116.4074
  }

  // 获取所有旅游计划
  const getTravelPlans = async () => {
    loading.value = true
    try {
      // 获取当前用户信息
      const authStore = useAuthStore()
      const currentUser = authStore.userInfo
      
      console.log('当前用户信息:', currentUser)
      console.log('用户ID:', currentUser?.id || 1)
      
      const response = await axios.get(`/api/travel-plans`, {
        headers: {
          'X-User-ID': currentUser?.id || 1 // 传递用户ID
        }
      })
      
      console.log('API响应:', response.data)
      plans.value = response.data
      return response.data
    } catch (error) {
      console.error('获取旅游计划失败:', error)
      console.log('API错误详情:', error.response?.data || error.message)
      
      // 如果API调用失败，先从本地存储加载用户生成的计划
      const localPlans = loadTravelPlansFromStorage()
      if (localPlans.length > 0) {
        console.log('从本地存储加载计划:', localPlans)
        return localPlans
      }
      
      // 如果本地存储也没有，返回模拟数据
      const mockData = getMockPlans()
      plans.value = mockData
      return mockData
    } finally {
      loading.value = false
    }
  }

  // 获取特定旅游计划
  const getTravelPlan = async (id) => {
    loading.value = true
    try {
      const response = await axios.get(`/api/travel-plans/${id}`)
      currentPlan.value = response.data
      return response.data
    } catch (error) {
      console.error('获取旅游计划失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 更新旅游计划
  const updateTravelPlan = async (id, planData) => {
    loading.value = true
    try {
      const response = await axios.put(`/api/travel-plans/${id}`, planData)
      const updatedPlan = response.data
      const index = plans.value.findIndex(plan => plan.id === id)
      if (index !== -1) {
        plans.value[index] = updatedPlan
      }
      if (currentPlan.value && currentPlan.value.id === id) {
        currentPlan.value = updatedPlan
      }
      return updatedPlan
    } catch (error) {
      console.error('更新旅游计划失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 删除旅游计划
  const deleteTravelPlan = async (id) => {
    loading.value = true
    try {
      await axios.delete(`/api/travel-plans/${id}`)
      plans.value = plans.value.filter(plan => plan.id !== id)
      if (currentPlan.value && currentPlan.value.id === id) {
        currentPlan.value = null
      }
    } catch (error) {
      console.error('删除旅游计划失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 保存旅游计划到本地存储
  const saveTravelPlan = (plan) => {
    const savedPlans = JSON.parse(localStorage.getItem('travelPlans') || '[]')
    savedPlans.unshift(plan)
    localStorage.setItem('travelPlans', JSON.stringify(savedPlans))
    plans.value = savedPlans
  }

  // 从本地存储加载旅游计划
  const loadTravelPlansFromStorage = () => {
    const savedPlans = JSON.parse(localStorage.getItem('travelPlans') || '[]')
    plans.value = savedPlans
    return savedPlans
  }

  // 获取模拟数据
  const getMockPlans = () => {
    const mockPlans = [
      {
        id: 1,
        title: '北京三日游',
        description: '探索北京的历史文化和现代风貌',
        startDate: '2024-03-15T00:00:00',
        endDate: '2024-03-17T23:59:59',
        budget: 3000,
        status: 'CONFIRMED',
        aiGenerated: true,
        userId: 1,
        destinations: [
          {
            id: 1,
            name: '北京',
            description: '首都北京，历史文化名城',
            latitude: 39.9042,
            longitude: 116.4074,
            address: '北京市',
            city: '北京',
            country: '中国',
            type: 'CITY',
            rating: 4.8,
            visitDuration: 480,
            estimatedCost: 1000,
            dayOrder: 1,
            timeOrder: 1,
            attractions: [
              {
                id: 1,
                name: '故宫博物院',
                description: '明清两朝的皇家宫殿',
                latitude: 39.9163,
                longitude: 116.3972,
                address: '北京市东城区景山前街4号',
                type: 'MUSEUM',
                rating: 4.8,
                visitDuration: 180,
                ticketPrice: 60,
                openingHours: '08:30-17:00',
                phone: '010-85007421',
                website: 'https://www.dpm.org.cn',
                imageUrl: 'https://example.com/forbidden-city.jpg'
              },
              {
                id: 2,
                name: '天安门广场',
                description: '世界最大的城市广场',
                latitude: 39.9042,
                longitude: 116.4074,
                address: '北京市东城区东长安街',
                type: 'MONUMENT',
                rating: 4.6,
                visitDuration: 60,
                ticketPrice: 0,
                openingHours: '全天开放',
                phone: '',
                website: '',
                imageUrl: 'https://example.com/tiananmen.jpg'
              }
            ]
          }
        ],
        createdAt: '2024-03-10T10:00:00',
        updatedAt: '2024-03-10T10:00:00'
      },
      {
        id: 2,
        title: '上海周末游',
        description: '体验上海的现代都市魅力',
        startDate: '2024-03-22T00:00:00',
        endDate: '2024-03-24T23:59:59',
        budget: 2500,
        status: 'PLANNING',
        aiGenerated: true,
        userId: 1,
        destinations: [
          {
            id: 2,
            name: '上海',
            description: '国际化大都市',
            latitude: 31.2304,
            longitude: 121.4737,
            address: '上海市',
            city: '上海',
            country: '中国',
            type: 'CITY',
            rating: 4.7,
            visitDuration: 360,
            estimatedCost: 800,
            dayOrder: 1,
            timeOrder: 1,
            attractions: [
              {
                id: 3,
                name: '外滩',
                description: '上海最著名的观光景点',
                latitude: 31.2397,
                longitude: 121.4998,
                address: '上海市黄浦区中山东一路',
                type: 'MONUMENT',
                rating: 4.7,
                visitDuration: 90,
                ticketPrice: 0,
                openingHours: '全天开放',
                phone: '',
                website: '',
                imageUrl: 'https://example.com/bund.jpg'
              }
            ]
          }
        ],
        createdAt: '2024-03-12T14:30:00',
        updatedAt: '2024-03-12T14:30:00'
      }
    ]
    
    // 保存到本地存储
    localStorage.setItem('travelPlans', JSON.stringify(mockPlans))
    plans.value = mockPlans
    return mockPlans
  }

  // 初始化时从本地存储加载数据
  loadTravelPlansFromStorage()

  return {
    plans,
    currentPlan,
    loading,
    generateTravelPlan,
    getTravelPlans,
    getTravelPlan,
    updateTravelPlan,
    deleteTravelPlan,
    saveTravelPlan,
    loadTravelPlansFromStorage
  }
})



























