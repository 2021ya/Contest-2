<template>
  <div class="map-page">
    <div class="container">
      <div class="page-header">
        <h1>旅游地图</h1>
        <p>查看您的旅游路线和景点分布</p>
      </div>

      <div class="map-container">
        <div class="map-controls">
          <el-card class="control-card">
            <template #header>
              <div class="card-header">
                <el-icon><Setting /></el-icon>
                <span>地图控制</span>
              </div>
            </template>
            
            <div class="control-group">
              <el-button-group>
                <el-button @click="showAllPlans" :type="showMode === 'all' ? 'primary' : ''">
                  显示所有计划
                </el-button>
                <el-button @click="showCurrentPlan" :type="showMode === 'current' ? 'primary' : ''">
                  当前计划
                </el-button>
                <el-button @click="showBaiduMarkers" :type="showMode === 'baidu' ? 'primary' : ''">
                  百度地图
                </el-button>
              </el-button-group>
            </div>

            <div class="control-group" v-if="currentPlan">
              <h4>当前计划：{{ currentPlan.title }}</h4>
              <el-select v-model="selectedDay" placeholder="选择查看天数" @change="onDayChange">
                <el-option
                  v-for="day in planDays"
                  :key="day"
                  :label="`第${day}天`"
                  :value="day"
                />
              </el-select>
            </div>

            <div class="control-group">
              <el-checkbox-group v-model="visibleLayers">
                <el-checkbox label="attractions">景点</el-checkbox>
                <el-checkbox label="hotels">酒店</el-checkbox>
                <el-checkbox label="restaurants">餐厅</el-checkbox>
                <el-checkbox label="shopping">购物</el-checkbox>
                <el-checkbox label="transport">交通</el-checkbox>
                <el-checkbox label="routes">路线</el-checkbox>
              </el-checkbox-group>
            </div>

            <div class="control-group">
              <el-button-group>
                <el-button @click="showPopularMarkers" :type="markerFilter === 'popular' ? 'primary' : ''">
                  热门景点
                </el-button>
                <el-button @click="showRecommendedMarkers" :type="markerFilter === 'recommended' ? 'primary' : ''">
                  推荐景点
                </el-button>
                <el-button @click="showFeaturedMarkers" :type="markerFilter === 'featured' ? 'primary' : ''">
                  精选景点
                </el-button>
                <el-button @click="showAllMarkers" :type="markerFilter === 'all' ? 'primary' : ''">
                  全部景点
                </el-button>
              </el-button-group>
            </div>

            <div class="control-group">
              <el-select v-model="selectedCity" placeholder="选择城市" @change="onCityChange" clearable>
                <el-option label="北京" value="北京" />
                <el-option label="上海" value="上海" />
                <el-option label="杭州" value="杭州" />
                <el-option label="广州" value="广州" />
                <el-option label="深圳" value="深圳" />
                <el-option label="成都" value="成都" />
                <el-option label="西安" value="西安" />
                <el-option label="南京" value="南京" />
              </el-select>
            </div>

            <div class="control-group">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索景点..."
                @keyup.enter="searchMarkers"
                clearable
              >
                <template #append>
                  <el-button @click="searchMarkers" :icon="Search" />
                </template>
              </el-input>
            </div>

            <div class="control-group">
              <el-button 
                type="primary" 
                @click="generateGuideMap" 
                :loading="isGeneratingGuide"
                class="guide-button"
              >
                <el-icon><Camera /></el-icon>
                一键生成导游图
              </el-button>
              <el-button 
                v-if="previewImage"
                type="success" 
                @click="showPreview = true"
                class="preview-button"
                style="margin-top: 10px; width: 100%;"
              >
                <el-icon><View /></el-icon>
                预览导游图
              </el-button>
            </div>
          </el-card>
        </div>

        <div class="map-wrapper">
          <div id="baiduMap" class="map"></div>
        </div>
      </div>

      <div class="plan-info" v-if="currentPlan">
        <el-card>
          <template #header>
            <div class="card-header">
              <el-icon><Document /></el-icon>
              <span>计划详情</span>
            </div>
          </template>
          
          <div class="plan-summary">
            <div class="summary-item">
              <span class="label">计划名称：</span>
              <span class="value">{{ currentPlan.title }}</span>
            </div>
            <div class="summary-item">
              <span class="label">出发日期：</span>
              <span class="value">{{ formatDate(currentPlan.startDate) }}</span>
            </div>
            <div class="summary-item">
              <span class="label">返回日期：</span>
              <span class="value">{{ formatDate(currentPlan.endDate) }}</span>
            </div>
            <div class="summary-item">
              <span class="label">预算：</span>
              <span class="value">¥{{ currentPlan.budget }}</span>
            </div>
            <div class="summary-item">
              <span class="label">目的地数量：</span>
              <span class="value">{{ currentPlan.destinations?.length || 0 }}个</span>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 导游图预览对话框 -->
    <el-dialog
      v-model="showPreview"
      title="导游图预览"
      width="90%"
      :before-close="closePreview"
    >
      <div class="preview-container" v-if="previewImage">
        <img :src="previewImage" alt="导游图预览" class="preview-image" />
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closePreview">关闭</el-button>
          <el-button type="primary" @click="downloadPreview">
            <el-icon><Download /></el-icon>
            下载图片
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Setting, Document, Search, Camera, View, Download } from '@element-plus/icons-vue'
import { useTravelPlanStore } from '@/stores/travelPlan'
import { useRoute } from 'vue-router'
import dayjs from 'dayjs'

// 百度地图相关变量
const baiduMap = ref(null)
const showMode = ref('all')
const currentPlan = ref(null)
const selectedDay = ref(null)
const planDays = ref([])
const visibleLayers = ref(['attractions', 'routes'])
const markerFilter = ref('all')
const selectedCity = ref('')
const searchKeyword = ref('')
const baiduMarkers = ref([])
const mapMarkers = ref([])
const mapLayers = ref([])
const mapRoutes = ref([])
const travelPlanStore = useTravelPlanStore()
const route = useRoute()

// 导游图生成相关变量
const isGeneratingGuide = ref(false)
const showPreview = ref(false)
const previewImage = ref('')

// 百度地图API密钥
const BAIDU_API_KEY = '7RT1Fu1UhXhvBUMYqNpaCdlk3pTBBaKn'

onMounted(async () => {
  // 添加全局错误处理
  window.addEventListener('error', (event) => {
    if (event.error && event.error.message && 
        (event.error.message.includes('trim') || 
         event.error.message.includes('content_scripts') ||
         event.error.message.includes('Cannot read properties of null'))) {
      console.warn('检测到浏览器扩展错误，已忽略:', event.error)
      event.preventDefault()
      return false
    }
  })

  window.addEventListener('unhandledrejection', (event) => {
    if (event.reason && event.reason.message && 
        (event.reason.message.includes('trim') || 
         event.reason.message.includes('content_scripts') ||
         event.reason.message.includes('Cannot read properties of null'))) {
      console.warn('检测到浏览器扩展Promise错误，已忽略:', event.reason)
      event.preventDefault()
    }
  })

  // 添加更全面的错误处理
  const originalConsoleError = console.error
  console.error = function(...args) {
    const message = args.join(' ')
    if (message.includes('content_scripts') || message.includes('trim')) {
      console.warn('过滤浏览器扩展错误:', ...args)
      return
    }
    originalConsoleError.apply(console, args)
  }

  await nextTick()
  await initBaiduMap()
  await loadPlans()
  await loadBaiduMapData()
  
  // 处理查询参数
  if (route.query.show === 'attractions') {
    // 从首页景点推荐跳转过来，显示所有景点
    showBaiduMarkers()
    visibleLayers.value = ['attractions']
  } else if (route.query.planId) {
    // 从计划页面跳转过来，显示特定计划
    const planId = parseInt(route.query.planId)
    await loadSpecificPlan(planId)
  }
})

onUnmounted(() => {
  if (baiduMap.value) {
    baiduMap.value.clearOverlays()
  }
})

const initBaiduMap = () => {
  return new Promise((resolve) => {
    // 动态加载百度地图API
    if (window.BMap) {
      initMapInstance()
      resolve()
    } else {
      const script = document.createElement('script')
      script.src = `https://api.map.baidu.com/api?v=3.0&ak=${BAIDU_API_KEY}&callback=initBaiduMapCallback`
      script.async = true
      window.initBaiduMapCallback = () => {
        initMapInstance()
        resolve()
      }
      document.head.appendChild(script)
    }
  })
}

const initMapInstance = () => {
  // 初始化百度地图，默认显示北京
  baiduMap.value = new BMap.Map('baiduMap')
  const point = new BMap.Point(116.4074, 39.9042) // 北京坐标
  baiduMap.value.centerAndZoom(point, 10)
  
  // 启用滚轮缩放
  baiduMap.value.enableScrollWheelZoom(true)
  
  // 添加地图控件
  baiduMap.value.addControl(new BMap.NavigationControl())
  baiduMap.value.addControl(new BMap.ScaleControl())
  baiduMap.value.addControl(new BMap.OverviewMapControl())
  baiduMap.value.addControl(new BMap.MapTypeControl())
}

const loadPlans = async () => {
  try {
    const plans = await travelPlanStore.getTravelPlans()
    if (plans && plans.length > 0) {
      currentPlan.value = plans[0] // 默认显示第一个计划
      updatePlanDays()
      showCurrentPlan()
    }
  } catch (error) {
    console.error('加载计划失败:', error)
    ElMessage.error('加载计划失败')
  }
}

const loadSpecificPlan = async (planId) => {
  try {
    const plan = await travelPlanStore.getTravelPlan(planId)
    if (plan) {
      currentPlan.value = plan
      updatePlanDays()
      showCurrentPlan()
    }
  } catch (error) {
    console.error('加载特定计划失败:', error)
    ElMessage.error('加载计划失败')
  }
}

// 加载百度地图数据
const loadBaiduMapData = async () => {
  try {
    console.log('尝试加载百度地图数据...')
    
    // 检查API是否可用
    const controller = new AbortController()
    const timeoutId = setTimeout(() => controller.abort(), 5000) // 5秒超时
    
    const response = await fetch('http://localhost:8080/api/baidu-map/markers', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      },
      signal: controller.signal
    })
    
    clearTimeout(timeoutId)
    
    if (response.ok) {
      const data = await response.json()
      if (Array.isArray(data)) {
        baiduMarkers.value = data
        addBaiduMarkersToMap()
        console.log('百度地图数据加载成功')
      } else {
        throw new Error('API返回数据格式错误')
      }
    } else {
      throw new Error(`API响应失败: ${response.status} ${response.statusText}`)
    }
  } catch (error) {
    if (error.name === 'AbortError') {
      console.warn('API请求超时，使用模拟数据')
    } else {
      console.warn('百度地图API不可用，使用模拟数据:', error.message)
    }
    // 使用模拟数据
    loadMockBaiduMapData()
  }
}

// 加载模拟百度地图数据
const loadMockBaiduMapData = () => {
  baiduMarkers.value = [
    {
      id: 1,
      name: '北京天安门',
      latitude: 39.9042,
      longitude: 116.4074,
      type: 'monument',
      description: '世界最大的城市广场'
    },
    {
      id: 2,
      name: '上海外滩',
      latitude: 31.2397,
      longitude: 121.4998,
      type: 'monument',
      description: '上海最著名的观光景点'
    },
    {
      id: 3,
      name: '杭州西湖',
      latitude: 30.2741,
      longitude: 120.1551,
      type: 'park',
      description: '世界文化遗产'
    }
  ]
  addBaiduMarkersToMap()
}

// 添加百度地图标记
const addBaiduMarkersToMap = () => {
  try {
    if (!baiduMap.value) {
      console.warn('百度地图未初始化，无法添加标记')
      return
    }
    
    if (!baiduMarkers.value || !Array.isArray(baiduMarkers.value)) {
      console.warn('标记数据无效')
      return
    }
    
    // 清除现有标记
    baiduMap.value.clearOverlays()
    
    baiduMarkers.value.forEach((marker, index) => {
      try {
        // 检查标记数据完整性
        if (!marker || typeof marker.longitude !== 'number' || typeof marker.latitude !== 'number') {
          console.warn(`标记 ${index} 数据不完整，跳过`)
          return
        }
        
        const point = new BMap.Point(marker.longitude, marker.latitude)
        const markerIcon = createBaiduMarkerIcon(marker)
        const baiduMarker = new BMap.Marker(point, { icon: markerIcon })
        
        // 添加信息窗口
        const infoWindow = createBaiduInfoWindow(marker)
        baiduMarker.addEventListener('click', () => {
          baiduMap.value.openInfoWindow(infoWindow, point)
        })
        
        baiduMap.value.addOverlay(baiduMarker)
      } catch (error) {
        console.error(`添加标记 ${index} 时发生错误:`, error)
      }
    })
  } catch (error) {
    console.error('添加百度地图标记时发生错误:', error)
  }
}

// 创建百度地图自定义图标
const createBaiduMarkerIcon = (marker) => {
  const iconColor = marker.color || '#FF6B6B'
  const iconSize = marker.size === 'LARGE' ? 30 : marker.size === 'MEDIUM' ? 25 : 20
  
  // 使用encodeURIComponent和btoa的组合来处理中文字符
  const svgContent = `
    <svg width="${iconSize}" height="${iconSize}" xmlns="http://www.w3.org/2000/svg">
      <circle cx="${iconSize/2}" cy="${iconSize/2}" r="${iconSize/2-2}" fill="${iconColor}" stroke="white" stroke-width="2"/>
      <text x="${iconSize/2}" y="${iconSize/2+4}" text-anchor="middle" fill="white" font-size="12" font-weight="bold">${getIconSymbol(marker.type)}</text>
    </svg>
  `
  
  try {
    // 先使用encodeURIComponent处理中文字符，然后使用btoa
    const encodedSvg = btoa(unescape(encodeURIComponent(svgContent)))
    return new BMap.Icon(`data:image/svg+xml;base64,${encodedSvg}`, new BMap.Size(iconSize, iconSize), {
      anchor: new BMap.Size(iconSize/2, iconSize/2)
    })
  } catch (error) {
    console.warn('创建自定义图标失败，使用默认图标:', error)
    // 如果自定义图标创建失败，使用默认图标
    try {
      return new BMap.Icon('http://api.map.baidu.com/img/markers.png', new BMap.Size(23, 25), {
        anchor: new BMap.Size(10, 25)
      })
    } catch (iconError) {
      console.warn('默认图标加载失败，使用简单圆形图标:', iconError)
      // 创建简单的圆形图标作为最后备用
      const simpleIcon = new BMap.Icon(`data:image/svg+xml;base64,${btoa(`
        <svg width="20" height="20" xmlns="http://www.w3.org/2000/svg">
          <circle cx="10" cy="10" r="8" fill="#FF6B6B" stroke="white" stroke-width="2"/>
        </svg>
      `)}`, new BMap.Size(20, 20), {
        anchor: new BMap.Size(10, 10)
      })
      return simpleIcon
    }
  }
}

// 创建百度地图信息窗口
const createBaiduInfoWindow = (marker) => {
  const content = `
    <div class="baidu-info-window">
      <h3>${marker.name}</h3>
      <p>${marker.description || ''}</p>
      <div class="marker-info">
        <p><strong>类型:</strong> ${getTypeName(marker.type)}</p>
        <p><strong>城市:</strong> ${marker.city}</p>
        ${marker.rating ? `<p><strong>评分:</strong> ${marker.rating}/5.0</p>` : ''}
        ${marker.priceLevel ? `<p><strong>价格:</strong> ${getPriceLevelName(marker.priceLevel)}</p>` : ''}
        ${marker.openingHours ? `<p><strong>开放时间:</strong> ${marker.openingHours}</p>` : ''}
        ${marker.phone ? `<p><strong>电话:</strong> ${marker.phone}</p>` : ''}
        ${marker.website ? `<p><strong>网站:</strong> <a href="${marker.website}" target="_blank">访问网站</a></p>` : ''}
      </div>
    </div>
  `
  return new BMap.InfoWindow(content, {
    width: 250,
    height: 200
  })
}

// 获取图标符号
const getIconSymbol = (type) => {
  const symbols = {
    'CULTURE': 'C',
    'HISTORY': 'H',
    'NATURE': 'N',
    'HOTEL': 'H',
    'RESTAURANT': 'R',
    'SHOPPING': 'S',
    'TRANSPORT': 'T',
    'ATTRACTION': 'A',
    'monument': 'M',
    'park': 'P',
    'museum': 'M',
    'default': '★'
  }
  return symbols[type] || '★'
}

// 创建标记弹窗
const createMarkerPopup = (marker) => {
  return `
    <div class="marker-popup">
      <h3>${marker.name}</h3>
      <p>${marker.description || ''}</p>
      <div class="marker-info">
        <p><strong>类型:</strong> ${getTypeName(marker.type)}</p>
        <p><strong>城市:</strong> ${marker.city}</p>
        ${marker.rating ? `<p><strong>评分:</strong> ${marker.rating}/5.0</p>` : ''}
        ${marker.priceLevel ? `<p><strong>价格:</strong> ${getPriceLevelName(marker.priceLevel)}</p>` : ''}
        ${marker.openingHours ? `<p><strong>开放时间:</strong> ${marker.openingHours}</p>` : ''}
        ${marker.phone ? `<p><strong>电话:</strong> ${marker.phone}</p>` : ''}
        ${marker.website ? `<p><strong>网站:</strong> <a href="${marker.website}" target="_blank">访问网站</a></p>` : ''}
      </div>
    </div>
  `
}

// 获取类型名称
const getTypeName = (type) => {
  const typeNames = {
    'CULTURE': '文化景点',
    'HISTORY': '历史遗迹',
    'NATURE': '自然景观',
    'HOTEL': '酒店',
    'RESTAURANT': '餐厅',
    'SHOPPING': '购物中心',
    'TRANSPORT': '交通枢纽',
    'ATTRACTION': '景点'
  }
  return typeNames[type] || type
}

// 获取价格等级名称
const getPriceLevelName = (priceLevel) => {
  const priceNames = {
    'FREE': '免费',
    'LOW': '低价',
    'MEDIUM': '中等',
    'HIGH': '高价',
    'LUXURY': '豪华'
  }
  return priceNames[priceLevel] || priceLevel
}

// 获取图层类型
const getLayerType = (markerType) => {
  const typeMapping = {
    'CULTURE': 'attractions',
    'HISTORY': 'attractions',
    'NATURE': 'attractions',
    'ATTRACTION': 'attractions',
    'HOTEL': 'hotels',
    'RESTAURANT': 'restaurants',
    'SHOPPING': 'shopping',
    'TRANSPORT': 'transport'
  }
  return typeMapping[markerType] || 'attractions'
}

// 显示热门标记
const showPopularMarkers = () => {
  markerFilter.value = 'popular'
  loadPopularMarkers()
}

// 显示推荐标记
const showRecommendedMarkers = () => {
  markerFilter.value = 'recommended'
  loadRecommendedMarkers()
}

// 显示所有标记
const showAllMarkers = () => {
  markerFilter.value = 'all'
  loadBaiduMapData()
}

// 加载热门标记
const loadPopularMarkers = async () => {
  try {
    console.log('尝试加载热门标记...')
    const response = await fetch('http://localhost:8080/api/baidu-map/markers/popular', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      },
      timeout: 3000
    })
    
    if (response.ok) {
      baiduMarkers.value = await response.json()
      addBaiduMarkersToMap()
      console.log('热门标记加载成功')
    } else {
      console.warn('热门标记API响应失败:', response.status, response.statusText)
      // 使用模拟数据
      loadMockBaiduMapData()
    }
  } catch (error) {
    console.error('加载热门标记失败:', error)
    console.log('使用模拟热门标记数据')
    // 使用模拟数据
    loadMockBaiduMapData()
  }
}

// 加载推荐标记
const loadRecommendedMarkers = async () => {
  try {
    console.log('尝试加载推荐标记...')
    const response = await fetch('http://localhost:8080/api/baidu-map/markers/recommended', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      },
      timeout: 3000
    })
    
    if (response.ok) {
      baiduMarkers.value = await response.json()
      addBaiduMarkersToMap()
      console.log('推荐标记加载成功')
    } else {
      console.warn('推荐标记API响应失败:', response.status, response.statusText)
      // 使用模拟数据
      loadMockBaiduMapData()
    }
  } catch (error) {
    console.error('加载推荐标记失败:', error)
    console.log('使用模拟推荐标记数据')
    // 使用模拟数据
    loadMockBaiduMapData()
  }
}

// 城市变化处理
const onCityChange = async (city) => {
  if (city) {
    // 加载特定城市的标记
    try {
      console.log(`尝试加载${city}城市标记...`)
      const response = await fetch(`http://localhost:8080/api/baidu-map/markers/city/${encodeURIComponent(city)}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        },
        timeout: 3000
      })
      
      if (response.ok) {
        baiduMarkers.value = await response.json()
        addBaiduMarkersToMap()
        console.log(`${city}城市标记加载成功`)
      } else {
        console.warn(`${city}城市标记API响应失败:`, response.status, response.statusText)
        // 使用模拟数据
        loadMockBaiduMapData()
      }
    } catch (error) {
      console.error(`加载${city}城市标记失败:`, error)
      console.log(`使用模拟${city}城市标记数据`)
      // 使用模拟数据
      loadMockBaiduMapData()
    }
  } else {
    // 加载所有标记
    loadBaiduMapData()
  }
}

const updatePlanDays = () => {
  if (currentPlan.value?.destinations) {
    const days = [...new Set(currentPlan.value.destinations.map(d => d.dayOrder))].sort()
    planDays.value = days
    if (days.length > 0) {
      selectedDay.value = days[0]
    }
  }
}

const showAllPlans = () => {
  showMode.value = 'all'
  clearMap()
  // 显示所有计划的景点
  loadAllPlans()
}

const showCurrentPlan = () => {
  showMode.value = 'current'
  clearMap()
  if (currentPlan.value) {
    loadCurrentPlan()
  }
}

const showBaiduMarkers = () => {
  showMode.value = 'baidu'
  clearMap()
  addBaiduMarkersToMap()
}

const showFeaturedMarkers = () => {
  markerFilter.value = 'featured'
  loadFeaturedMarkers()
}

const loadFeaturedMarkers = async () => {
  try {
    console.log('尝试加载精选标记...')
    const response = await fetch('http://localhost:8080/api/baidu-map/markers/featured', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      },
      timeout: 3000
    })
    
    if (response.ok) {
      baiduMarkers.value = await response.json()
      addBaiduMarkersToMap()
      console.log('精选标记加载成功')
    } else {
      console.warn('精选标记API响应失败:', response.status, response.statusText)
      // 使用模拟数据
      loadMockBaiduMapData()
    }
  } catch (error) {
    console.error('加载精选标记失败:', error)
    console.log('使用模拟精选标记数据')
    // 使用模拟数据
    loadMockBaiduMapData()
  }
}

const searchMarkers = async () => {
  if (!searchKeyword.value || !searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  
  try {
    const controller = new AbortController()
    const timeoutId = setTimeout(() => controller.abort(), 5000)
    
    const response = await fetch(`http://localhost:8080/api/baidu-map/markers/search?keyword=${encodeURIComponent(searchKeyword.value.trim())}`, {
      signal: controller.signal
    })
    
    clearTimeout(timeoutId)
    
    if (response.ok) {
      const data = await response.json()
      if (Array.isArray(data)) {
        baiduMarkers.value = data
        addBaiduMarkersToMap()
        ElMessage.success(`找到 ${data.length} 个相关景点`)
      } else {
        throw new Error('搜索返回数据格式错误')
      }
    } else {
      throw new Error(`搜索失败: ${response.status}`)
    }
  } catch (error) {
    if (error.name === 'AbortError') {
      ElMessage.error('搜索请求超时，请重试')
    } else {
      console.error('搜索标记失败:', error)
      ElMessage.error('搜索失败，请稍后重试')
      // 使用模拟数据进行搜索
      loadMockBaiduMapData()
    }
  }
}

const loadAllPlans = async () => {
  try {
    const plans = await travelPlanStore.getTravelPlans()
    console.log('加载所有计划，计划数量:', plans.length)
    
    plans.forEach((plan, planIndex) => {
      if (plan.destinations && plan.destinations.length > 0) {
        console.log(`计划 ${plan.title} 有 ${plan.destinations.length} 个目的地`)
        
        // 添加目的地标记
        plan.destinations.forEach((destination, destIndex) => {
          addDestinationMarker(destination, plan.title, destIndex)
        })
        
        // 为每个计划添加路线
        if (plan.destinations.length > 1) {
          console.log(`为计划 ${plan.title} 添加路线`)
          addRoute(plan.destinations)
        }
      }
    })
  } catch (error) {
    console.error('加载所有计划失败:', error)
  }
}

const loadCurrentPlan = () => {
  if (!currentPlan.value?.destinations) {
    console.log('没有当前计划或目的地数据')
    return
  }
  
  const destinations = selectedDay.value 
    ? currentPlan.value.destinations.filter(d => d.dayOrder === selectedDay.value)
    : currentPlan.value.destinations
  
  console.log('加载当前计划，目的地数量:', destinations.length)
  
  destinations.forEach((destination, index) => {
    addDestinationMarker(destination, currentPlan.value.title, index)
  })
  
  // 添加路线
  if (destinations.length > 1) {
    console.log('添加路线，目的地数量:', destinations.length)
    addRoute(destinations)
  } else {
    console.log('目的地数量不足，无法显示路线')
  }
  
  // 调整地图视野
  if (destinations.length > 0 && baiduMap.value) {
    const points = destinations.map(d => new BMap.Point(d.longitude, d.latitude))
    baiduMap.value.setViewport(points)
  }
}

const addDestinationMarker = (destination, planTitle, index = 0) => {
  if (!baiduMap.value) return
  
  const point = new BMap.Point(destination.longitude, destination.latitude)
  
  let popupContent = `
    <div class="marker-popup">
      <h4>${destination.name}</h4>
      <p>${destination.description || '暂无描述'}</p>
      <p><strong>城市：</strong>${destination.city}</p>
      <p><strong>类型：</strong>${destination.type}</p>
      <p><strong>评分：</strong>${destination.rating || 'N/A'}</p>
      <p><strong>计划：</strong>${planTitle}</p>
  `
  
  if (destination.attractions && destination.attractions.length > 0) {
    popupContent += '<h5>景点：</h5><ul>'
    destination.attractions.forEach(attraction => {
      popupContent += `<li>${attraction.name} (评分: ${attraction.rating})</li>`
    })
    popupContent += '</ul>'
  }
  
  popupContent += '</div>'
  
  // 创建自定义图标
  const iconColor = getIconColor(destination.type)
  const icon = new BMap.Icon(`data:image/svg+xml;base64,${btoa(`
    <svg width="30" height="30" xmlns="http://www.w3.org/2000/svg">
      <circle cx="15" cy="15" r="13" fill="${iconColor}" stroke="white" stroke-width="2"/>
      <text x="15" y="19" text-anchor="middle" fill="white" font-size="12" font-weight="bold">${index + 1}</text>
    </svg>
  `)}`, new BMap.Size(30, 30), {
    anchor: new BMap.Size(15, 15)
  })
  
  const marker = new BMap.Marker(point, { icon: icon })
  
  // 添加信息窗口
  const infoWindow = new BMap.InfoWindow(popupContent, {
    width: 250,
    height: 200
  })
  
  marker.addEventListener('click', () => {
    baiduMap.value.openInfoWindow(infoWindow, point)
  })
  
  baiduMap.value.addOverlay(marker)
}

const addRoute = (destinations) => {
  if (destinations.length < 2 || !baiduMap.value) return
  
  const routePoints = destinations.map(d => new BMap.Point(d.longitude, d.latitude))
  
  // 百度地图折线样式配置
  const polylineStyle = {
    strokeColor: '#667eea',
    strokeWeight: 4,
    strokeOpacity: 0.8,
    strokeStyle: 'dashed'
  }
  
  const polyline = new BMap.Polyline(routePoints, polylineStyle)
  
  baiduMap.value.addOverlay(polyline)
  
  console.log('添加路线:', destinations.length, '个点')
}

const getIconColor = (type) => {
  const colors = {
    CITY: '#3498db',
    ATTRACTION: '#e74c3c',
    RESTAURANT: '#f39c12',
    HOTEL: '#9b59b6',
    SHOPPING: '#1abc9c',
    ENTERTAINMENT: '#e67e22',
    NATURE: '#27ae60',
    CULTURE: '#8e44ad',
    HISTORY: '#34495e'
  }
  return colors[type] || '#95a5a6'
}

const clearMap = () => {
  if (baiduMap.value) {
    baiduMap.value.clearOverlays()
  }
}

const onDayChange = () => {
  if (showMode.value === 'current') {
    clearMap()
    loadCurrentPlan()
  }
}

const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD')
}

// 生成导游图功能
const generateGuideMap = async () => {
  if (!baiduMap.value) {
    ElMessage.error('地图未加载完成，请稍后再试')
    return
  }

  // 检查是否有景点数据
  const attractions = getCurrentAttractions()
  if (attractions.length === 0) {
    ElMessage.warning('当前没有景点数据，建议先搜索或选择景点后再生成导游图')
    return
  }

  isGeneratingGuide.value = true
  
  try {
    // 显示生成提示
    ElMessage.info({
      message: '正在生成导游图，请稍候...',
      duration: 0, // 不自动关闭
      showClose: false
    })
    
    // 等待一小段时间确保地图渲染完成
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 获取当前地图的截图
    const mapCanvas = await captureMapScreenshot()
    
    if (mapCanvas) {
      // 创建导游图
      const guideImage = await createGuideImage(mapCanvas)
      
      if (guideImage) {
        // 保存图片
        const saveResult = await saveGuideImage(guideImage)
        
        if (saveResult) {
          ElMessage.closeAll() // 关闭之前的提示
          ElMessage.success({
            message: '导游图生成成功！图片已自动下载',
            duration: 3000,
            showClose: true
          })
        } else {
          ElMessage.closeAll()
          ElMessage.error('保存导游图失败，请重试')
        }
      } else {
        ElMessage.closeAll()
        ElMessage.error('创建导游图失败，请重试')
      }
    } else {
      ElMessage.closeAll()
      ElMessage.error('地图截图失败，请确保地图已完全加载后重试')
    }
  } catch (error) {
    console.error('生成导游图失败:', error)
    ElMessage.closeAll()
    
    // 根据错误类型显示不同的错误信息
    if (error.message && error.message.includes('html2canvas')) {
      ElMessage.error('截图功能暂时不可用，请刷新页面后重试')
    } else if (error.message && error.message.includes('canvas')) {
      ElMessage.error('画布渲染失败，请重试')
    } else {
      ElMessage.error('生成导游图失败，请重试')
    }
  } finally {
    isGeneratingGuide.value = false
  }
}

// 捕获地图截图
const captureMapScreenshot = async () => {
  return new Promise((resolve) => {
    // 由于百度地图的限制，我们使用html2canvas来截图
    import('html2canvas').then(html2canvas => {
      const mapElement = document.getElementById('baiduMap')
      if (!mapElement) {
        console.error('地图元素未找到')
        resolve(null)
        return
      }

      // 等待地图完全加载
      setTimeout(() => {
        html2canvas.default(mapElement, {
          useCORS: true,
          allowTaint: true,
          backgroundColor: '#ffffff',
          scale: 2, // 提高图片质量
          width: mapElement.offsetWidth,
          height: mapElement.offsetHeight,
          logging: false, // 禁用日志
          foreignObjectRendering: true, // 启用外部对象渲染
          removeContainer: true, // 移除容器
          imageTimeout: 15000, // 增加图片加载超时时间
          onclone: (clonedDoc) => {
            // 确保克隆的文档中的地图元素正确渲染
            const clonedMap = clonedDoc.getElementById('baiduMap')
            if (clonedMap) {
              clonedMap.style.visibility = 'visible'
              clonedMap.style.display = 'block'
            }
          }
        }).then(canvas => {
          if (canvas && canvas.width > 0 && canvas.height > 0) {
            console.log('地图截图成功，尺寸:', canvas.width, 'x', canvas.height)
            resolve(canvas)
          } else {
            console.error('截图失败：画布尺寸无效')
            resolve(null)
          }
        }).catch(error => {
          console.error('截图失败:', error)
          // 提供更详细的错误信息
          if (error.name === 'SecurityError') {
            console.error('安全错误：可能是跨域问题')
          } else if (error.name === 'NotSupportedError') {
            console.error('不支持的操作：浏览器可能不支持html2canvas')
          } else {
            console.error('截图过程中发生未知错误')
          }
          resolve(null)
        })
      }, 2000) // 等待2秒确保地图完全渲染
    }).catch(error => {
      console.error('加载html2canvas失败:', error)
      resolve(null)
    })
  })
}

// 创建导游图
const createGuideImage = async (mapCanvas) => {
  return new Promise((resolve) => {
    const canvas = document.createElement('canvas')
    const ctx = canvas.getContext('2d')
    
    // 设置导游图尺寸 (A4比例)
    const guideWidth = 1400
    const guideHeight = 1000
    canvas.width = guideWidth
    canvas.height = guideHeight
    
    // 绘制背景
    const gradient = ctx.createLinearGradient(0, 0, 0, guideHeight)
    gradient.addColorStop(0, '#667eea')
    gradient.addColorStop(0.5, '#764ba2')
    gradient.addColorStop(1, '#f093fb')
    ctx.fillStyle = gradient
    ctx.fillRect(0, 0, guideWidth, guideHeight)
    
    // 添加装饰性图案
    ctx.fillStyle = 'rgba(255, 255, 255, 0.1)'
    for (let i = 0; i < 20; i++) {
      const x = Math.random() * guideWidth
      const y = Math.random() * guideHeight
      const radius = Math.random() * 3 + 1
      ctx.beginPath()
      ctx.arc(x, y, radius, 0, 2 * Math.PI)
      ctx.fill()
    }
    
    // 绘制地图
    const mapWidth = guideWidth - 400 // 为右侧信息留出空间
    const mapHeight = guideHeight - 200
    const mapX = 40
    const mapY = 100
    
    ctx.drawImage(mapCanvas, mapX, mapY, mapWidth, mapHeight)
    
    // 添加地图边框和阴影效果
    ctx.shadowColor = 'rgba(0, 0, 0, 0.3)'
    ctx.shadowBlur = 10
    ctx.shadowOffsetX = 5
    ctx.shadowOffsetY = 5
    ctx.strokeStyle = '#ffffff'
    ctx.lineWidth = 4
    ctx.strokeRect(mapX, mapY, mapWidth, mapHeight)
    
    // 重置阴影
    ctx.shadowColor = 'transparent'
    ctx.shadowBlur = 0
    ctx.shadowOffsetX = 0
    ctx.shadowOffsetY = 0
    
    // 添加标题
    ctx.fillStyle = '#ffffff'
    ctx.font = 'bold 36px Arial'
    ctx.textAlign = 'center'
    ctx.shadowColor = 'rgba(0, 0, 0, 0.5)'
    ctx.shadowBlur = 3
    ctx.shadowOffsetX = 2
    ctx.shadowOffsetY = 2
    ctx.fillText('旅游导游图', guideWidth / 2, 50)
    
    // 添加副标题
    ctx.font = '20px Arial'
    ctx.fillText('Travel Guide Map', guideWidth / 2, 80)
    
    // 重置阴影
    ctx.shadowColor = 'transparent'
    ctx.shadowBlur = 0
    ctx.shadowOffsetX = 0
    ctx.shadowOffsetY = 0
    
    // 绘制路线和景点信息
    drawRouteAndAttractions(ctx, mapX, mapY, mapWidth, mapHeight, guideWidth, guideHeight)
    
    // 添加生成时间
    const now = new Date()
    const timeString = now.toLocaleString('zh-CN')
    ctx.font = '14px Arial'
    ctx.textAlign = 'right'
    ctx.fillText(`生成时间: ${timeString}`, guideWidth - 40, guideHeight - 20)
    
    resolve(canvas)
  })
}

// 绘制路线和景点信息
const drawRouteAndAttractions = (ctx, mapX, mapY, mapWidth, mapHeight, guideWidth, guideHeight) => {
  // 获取当前显示的景点数据
  const attractions = getCurrentAttractions()
  const routes = getCurrentRoutes()
  
  // 右侧信息面板位置
  const infoPanelX = mapX + mapWidth + 20
  const infoPanelY = mapY
  const infoPanelWidth = guideWidth - infoPanelX - 40
  
  // 绘制信息面板背景
  ctx.fillStyle = 'rgba(255, 255, 255, 0.95)'
  ctx.fillRect(infoPanelX, infoPanelY, infoPanelWidth, mapHeight)
  
  // 绘制信息面板边框
  ctx.strokeStyle = '#ddd'
  ctx.lineWidth = 2
  ctx.strokeRect(infoPanelX, infoPanelY, infoPanelWidth, mapHeight)
  
  let currentY = infoPanelY + 30
  
  // 标题
  ctx.fillStyle = '#333'
  ctx.font = 'bold 20px Arial'
  ctx.textAlign = 'left'
  ctx.fillText('旅游路线信息', infoPanelX + 20, currentY)
  currentY += 40
  
  // 添加当前城市信息
  if (selectedCity.value) {
    ctx.font = 'bold 16px Arial'
    ctx.fillStyle = '#667eea'
    ctx.fillText(`📍 当前城市: ${selectedCity.value}`, infoPanelX + 20, currentY)
    currentY += 30
  }
  
  // 绘制路线信息
  if (routes.length > 0) {
    ctx.font = 'bold 16px Arial'
    ctx.fillText('路线详情:', infoPanelX + 20, currentY)
    currentY += 25
    
    ctx.font = '14px Arial'
    routes.forEach((route, index) => {
      const routeInfo = `${index + 1}. ${route.from} → ${route.to}`
      ctx.fillText(routeInfo, infoPanelX + 20, currentY)
      currentY += 20
      
      if (route.distance) {
        ctx.fillStyle = '#666'
        ctx.fillText(`距离: ${route.distance}km`, infoPanelX + 40, currentY)
        currentY += 20
        ctx.fillStyle = '#333'
      }
    })
    currentY += 20
  }
  
  // 绘制景点信息
  if (attractions.length > 0) {
    ctx.font = 'bold 16px Arial'
    ctx.fillText('景点详情:', infoPanelX + 20, currentY)
    currentY += 25
    
    // 限制显示的景点数量，避免信息过多
    const maxAttractions = Math.min(attractions.length, 8)
    
    attractions.slice(0, maxAttractions).forEach((attraction, index) => {
      // 景点名称
      ctx.fillStyle = '#333'
      ctx.font = 'bold 14px Arial'
      const attractionName = attraction.name.length > 20 
        ? attraction.name.substring(0, 20) + '...' 
        : attraction.name
      ctx.fillText(`${index + 1}. ${attractionName}`, infoPanelX + 20, currentY)
      currentY += 20
      
      // 景点详细信息
      ctx.font = '12px Arial'
      ctx.fillStyle = '#666'
      
      if (attraction.city) {
        ctx.fillText(`📍 城市: ${attraction.city}`, infoPanelX + 20, currentY)
        currentY += 16
      }
      
      if (attraction.type) {
        ctx.fillText(`🏷️ 类型: ${getTypeName(attraction.type)}`, infoPanelX + 20, currentY)
        currentY += 16
      }
      
      if (attraction.rating) {
        ctx.fillText(`⭐ 评分: ${attraction.rating}/5.0`, infoPanelX + 20, currentY)
        currentY += 16
      }
      
      if (attraction.priceLevel) {
        ctx.fillText(`💰 价格: ${getPriceLevelName(attraction.priceLevel)}`, infoPanelX + 20, currentY)
        currentY += 16
      }
      
      if (attraction.openingHours) {
        const hours = attraction.openingHours.length > 30 
          ? attraction.openingHours.substring(0, 30) + '...' 
          : attraction.openingHours
        ctx.fillText(`🕒 开放时间: ${hours}`, infoPanelX + 20, currentY)
        currentY += 16
      }
      
      if (attraction.description) {
        const description = attraction.description.length > 40 
          ? attraction.description.substring(0, 40) + '...' 
          : attraction.description
        ctx.fillText(`📝 ${description}`, infoPanelX + 20, currentY)
        currentY += 16
      }
      
      currentY += 8 // 景点间距
    })
    
    // 如果景点数量超过限制，显示省略信息
    if (attractions.length > maxAttractions) {
      ctx.font = 'italic 12px Arial'
      ctx.fillStyle = '#999'
      ctx.fillText(`... 还有 ${attractions.length - maxAttractions} 个景点`, infoPanelX + 20, currentY)
      currentY += 20
    }
  } else {
    // 没有景点时的提示
    ctx.font = '14px Arial'
    ctx.fillStyle = '#999'
    ctx.fillText('暂无景点信息', infoPanelX + 20, currentY)
    currentY += 20
  }
  
  // 绘制路线距离标注（在地图上）
  if (routes.length > 0) {
    drawRouteDistancesOnMap(ctx, mapX, mapY, mapWidth, mapHeight, routes)
  }
}

// 获取当前显示的景点数据
const getCurrentAttractions = () => {
  try {
    if (showMode.value === 'baidu' && baiduMarkers.value && baiduMarkers.value.length > 0) {
      return baiduMarkers.value.slice(0, 10) // 最多显示10个景点
    } else if (currentPlan.value?.destinations && Array.isArray(currentPlan.value.destinations)) {
      return currentPlan.value.destinations.slice(0, 10)
    }
    return []
  } catch (error) {
    console.error('获取景点数据时发生错误:', error)
    return []
  }
}

// 获取当前路线数据
const getCurrentRoutes = () => {
  const routes = []
  
  try {
    if (currentPlan.value?.destinations && Array.isArray(currentPlan.value.destinations) && currentPlan.value.destinations.length > 1) {
      const destinations = currentPlan.value.destinations
      for (let i = 0; i < destinations.length - 1; i++) {
        const from = destinations[i]
        const to = destinations[i + 1]
        
        // 检查坐标是否存在且有效
        if (from && to && 
            typeof from.latitude === 'number' && typeof from.longitude === 'number' &&
            typeof to.latitude === 'number' && typeof to.longitude === 'number' &&
            from.name && to.name) {
          const distance = calculateDistance(from.latitude, from.longitude, to.latitude, to.longitude)
          
          routes.push({
            from: from.name,
            to: to.name,
            distance: distance.toFixed(1)
          })
        }
      }
    }
  } catch (error) {
    console.error('获取路线数据时发生错误:', error)
  }
  
  return routes
}

// 计算两点间距离（公里）
const calculateDistance = (lat1, lon1, lat2, lon2) => {
  const R = 6371 // 地球半径（公里）
  const dLat = (lat2 - lat1) * Math.PI / 180
  const dLon = (lon2 - lon1) * Math.PI / 180
  const a = Math.sin(dLat/2) * Math.sin(dLat/2) +
    Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
    Math.sin(dLon/2) * Math.sin(dLon/2)
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a))
  return R * c
}

// 在地图上绘制路线距离标注
const drawRouteDistancesOnMap = (ctx, mapX, mapY, mapWidth, mapHeight, routes) => {
  if (routes.length === 0) return
  
  ctx.fillStyle = '#ff6b6b'
  ctx.font = 'bold 14px Arial'
  ctx.textAlign = 'center'
  
  // 在路线中点绘制距离信息
  routes.forEach((route, index) => {
    const textY = mapY + mapHeight - 50 - (index * 30)
    const textX = mapX + mapWidth / 2
    
    // 绘制背景
    ctx.fillStyle = 'rgba(255, 107, 107, 0.9)'
    ctx.fillRect(textX - 60, textY - 15, 120, 25)
    
    // 绘制文字
    ctx.fillStyle = '#ffffff'
    ctx.fillText(`${route.distance}km`, textX, textY)
  })
}

// 保存导游图
const saveGuideImage = async (canvas) => {
  return new Promise((resolve) => {
    // 将canvas转换为blob
    canvas.toBlob((blob) => {
      if (!blob) {
        resolve(false)
        return
      }
      
      // 保存预览图片
      const url = URL.createObjectURL(blob)
      previewImage.value = url
      
      // 创建下载链接
      const link = document.createElement('a')
      link.href = url
      link.download = `导游图_${new Date().toISOString().slice(0, 19).replace(/:/g, '-')}.png`
      
      // 触发下载
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      
      // 清理URL
      setTimeout(() => URL.revokeObjectURL(url), 1000)
      
      resolve(true)
    }, 'image/png', 0.9)
  })
}

// 关闭预览
const closePreview = () => {
  showPreview.value = false
}

// 下载预览图片
const downloadPreview = () => {
  if (previewImage.value) {
    const link = document.createElement('a')
    link.href = previewImage.value
    link.download = `导游图_${new Date().toISOString().slice(0, 19).replace(/:/g, '-')}.png`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }
}
</script>

<style scoped>
@keyframes titleFlow {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}

.map-page {
  min-height: 100vh;
  background: #f5f7fa;
  background-image: url('@/tutu/login1.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-attachment: fixed;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 2.5rem;
  color: #2c3e50;
  margin-bottom: 10px;
  background: linear-gradient(90deg, rgba(44,62,80,0), #2c3e50 51.33%, rgba(44,62,80,0));
  background-size: 200% 100%;
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: titleFlow 10s ease-in-out infinite;
}

.page-header p {
  font-size: 1.1rem;
  color: #7f8c8d;
  background: linear-gradient(90deg, rgba(127,140,141,0), #7f8c8d 51.33%, rgba(127,140,141,0));
  background-size: 200% 100%;
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: titleFlow 10s ease-in-out infinite;
}

.map-container {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 20px;
  margin-bottom: 30px;
}

.map-controls {
  height: fit-content;
}

.control-card {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 1.1rem;
  font-weight: bold;
  color: #2c3e50;
}

.control-group {
  margin-bottom: 20px;
}

.control-group h4 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.map-wrapper {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.map {
  height: 600px;
  width: 100%;
}

.plan-info {
  margin-top: 30px;
}

.plan-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.summary-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.summary-item .label {
  font-weight: bold;
  color: #7f8c8d;
  font-size: 0.9rem;
}

.summary-item .value {
  color: #2c3e50;
  font-size: 1rem;
}

/* 弹窗样式 */
:deep(.leaflet-popup-content) {
  margin: 8px 12px;
  line-height: 1.4;
}

:deep(.marker-popup h4) {
  margin: 0 0 8px 0;
  color: #2c3e50;
}

:deep(.marker-popup h5) {
  margin: 10px 0 5px 0;
  color: #2c3e50;
}

:deep(.marker-popup ul) {
  margin: 0;
  padding-left: 20px;
}

:deep(.marker-popup li) {
  margin-bottom: 3px;
}

/* 自定义标记样式 */
:deep(.custom-marker) {
  background: transparent !important;
  border: none !important;
}

/* 增强的标记弹窗样式 */
:deep(.marker-popup) {
  min-width: 250px;
  max-width: 300px;
}

:deep(.marker-popup h3) {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 16px;
  font-weight: bold;
}

:deep(.marker-popup p) {
  margin: 5px 0;
  color: #666;
  font-size: 14px;
  line-height: 1.4;
}

:deep(.marker-popup .marker-info) {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #eee;
}

:deep(.marker-popup .marker-info p) {
  margin: 3px 0;
  font-size: 13px;
}

:deep(.marker-popup a) {
  color: #409EFF;
  text-decoration: none;
}

:deep(.marker-popup a:hover) {
  text-decoration: underline;
}

/* 百度地图信息窗口样式 */
:deep(.baidu-info-window) {
  min-width: 250px;
  max-width: 300px;
  padding: 10px;
}

:deep(.baidu-info-window h3) {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 16px;
  font-weight: bold;
}

:deep(.baidu-info-window p) {
  margin: 5px 0;
  color: #666;
  font-size: 14px;
  line-height: 1.4;
}

:deep(.baidu-info-window .marker-info) {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #eee;
}

:deep(.baidu-info-window .marker-info p) {
  margin: 3px 0;
  font-size: 13px;
}

:deep(.baidu-info-window a) {
  color: #409EFF;
  text-decoration: none;
}

:deep(.baidu-info-window a:hover) {
  text-decoration: underline;
}

/* 导游图按钮样式 */
.guide-button {
  width: 100%;
  height: 45px;
  font-size: 16px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 8px;
  color: white;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.guide-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.guide-button:active {
  transform: translateY(0);
}

.guide-button .el-icon {
  margin-right: 8px;
  font-size: 18px;
}

/* 预览相关样式 */
.preview-container {
  text-align: center;
  max-height: 70vh;
  overflow: auto;
}

.preview-image {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.preview-button {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  border: none;
  border-radius: 8px;
  color: white;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(82, 196, 26, 0.3);
}

.preview-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(82, 196, 26, 0.4);
}

.preview-button .el-icon {
  margin-right: 8px;
  font-size: 16px;
}

@media (max-width: 768px) {
  .map-container {
    grid-template-columns: 1fr;
  }
  
  .map-controls {
    order: 2;
  }
  
  .map-wrapper {
    order: 1;
  }
  
  .map {
    height: 400px;
  }
  
  .plan-summary {
    grid-template-columns: 1fr;
  }
}
</style>
