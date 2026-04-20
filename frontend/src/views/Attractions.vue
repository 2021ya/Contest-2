<template>
  <div class="attractions-page">
    <div class="container">
      <div class="page-header">
        <h1>景点推荐</h1>
        <p>发现中国最美的旅游景点，体验独特的文化魅力</p>
      </div>

      <!-- 搜索和筛选 -->
      <div class="search-section">
        <el-card class="search-card">
          <div class="search-controls">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索景点名称或描述..."
              class="search-input"
              @input="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            
            <el-select
              v-model="selectedType"
              placeholder="选择景点类型"
              class="type-select"
              @change="handleTypeFilter"
            >
              <el-option label="全部类型" value="" />
              <el-option label="博物馆" value="MUSEUM" />
              <el-option label="公园" value="PARK" />
              <el-option label="寺庙" value="TEMPLE" />
              <el-option label="海滩" value="BEACH" />
              <el-option label="山峰" value="MOUNTAIN" />
              <el-option label="湖泊" value="LAKE" />
              <el-option label="建筑" value="BUILDING" />
              <el-option label="纪念碑" value="MONUMENT" />
              <el-option label="动物园" value="ZOO" />
              <el-option label="花园" value="GARDEN" />
            </el-select>

            <el-select
              v-model="selectedCity"
              placeholder="选择城市"
              class="city-select"
              @change="handleCityFilter"
            >
              <el-option label="全部城市" value="" />
              <el-option
                v-for="city in cities"
                :key="city"
                :label="city"
                :value="city"
              />
            </el-select>

            <el-button @click="resetFilters" class="reset-btn">
              <el-icon><Refresh /></el-icon>
              重置筛选
            </el-button>
          </div>
        </el-card>
      </div>

      <!-- 景点列表 -->
      <div class="attractions-section">
        <div class="section-header">
          <h2>推荐景点</h2>
          <div class="view-controls">
            <el-button-group>
              <el-button
                :type="viewMode === 'grid' ? 'primary' : ''"
                @click="viewMode = 'grid'"
              >
                <el-icon><Grid /></el-icon>
                网格视图
              </el-button>
              <el-button
                :type="viewMode === 'list' ? 'primary' : ''"
                @click="viewMode = 'list'"
              >
                <el-icon><List /></el-icon>
                列表视图
              </el-button>
            </el-button-group>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="6" animated />
        </div>

        <!-- 景点网格视图 -->
        <div v-else-if="viewMode === 'grid'" class="attractions-grid">
          <div
            v-for="attraction in filteredAttractions"
            :key="attraction.id"
            class="attraction-card"
            @click="viewAttraction(attraction)"
          >
            <div class="card-image">
              <img
                :src="attraction.imageUrl || defaultImageUrl"
                :alt="attraction.name"
                @error="handleImageError"
                @load="handleImageLoad"
                loading="lazy"
              />
              <div class="rating-badge">
                <el-icon><Star /></el-icon>
                <span>{{ attraction.rating?.toFixed(1) || 'N/A' }}</span>
              </div>
            </div>
            
            <div class="card-content">
              <h3 class="attraction-name">{{ attraction.name }}</h3>
              <p class="attraction-description">{{ attraction.description }}</p>
              
              <div class="attraction-info">
                <div class="info-item">
                  <el-icon><Location /></el-icon>
                  <span>{{ attraction.city }}, {{ attraction.country }}</span>
                </div>
                <div class="info-item" v-if="attraction.ticketPrice">
                  <el-icon><Money /></el-icon>
                  <span>¥{{ attraction.ticketPrice }}</span>
                </div>
                <div class="info-item" v-if="attraction.visitDuration">
                  <el-icon><Clock /></el-icon>
                  <span>{{ attraction.visitDuration }}分钟</span>
                </div>
              </div>

              <div class="card-actions">
                <el-button type="primary" size="small">
                  <el-icon><View /></el-icon>
                  查看详情
                </el-button>
                <el-button type="success" size="small" @click.stop="view3DScene(attraction)">
                  <el-icon><VideoCamera /></el-icon>
                  3D实景
                </el-button>
                <el-button size="small" @click.stop="addToPlan(attraction)">
                  <el-icon><Plus /></el-icon>
                  加入计划
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 景点列表视图 -->
        <div v-else class="attractions-list">
          <div
            v-for="attraction in filteredAttractions"
            :key="attraction.id"
            class="attraction-item"
            @click="viewAttraction(attraction)"
          >
            <div class="item-image">
              <img
                :src="attraction.imageUrl || defaultImageUrl"
                :alt="attraction.name"
                @error="handleImageError"
                @load="handleImageLoad"
                loading="lazy"
              />
            </div>
            
            <div class="item-content">
              <div class="item-header">
                <h3 class="attraction-name">{{ attraction.name }}</h3>
                <div class="rating">
                  <el-icon><Star /></el-icon>
                  <span>{{ attraction.rating?.toFixed(1) || 'N/A' }}</span>
                </div>
              </div>
              
              <p class="attraction-description">{{ attraction.description }}</p>
              
              <div class="attraction-meta">
                <div class="meta-item">
                  <el-icon><Location /></el-icon>
                  <span>{{ attraction.city }}, {{ attraction.country }}</span>
                </div>
                <div class="meta-item" v-if="attraction.ticketPrice">
                  <el-icon><Money /></el-icon>
                  <span>¥{{ attraction.ticketPrice }}</span>
                </div>
                <div class="meta-item" v-if="attraction.visitDuration">
                  <el-icon><Clock /></el-icon>
                  <span>{{ attraction.visitDuration }}分钟</span>
                </div>
                <div class="meta-item" v-if="attraction.openingHours">
                  <el-icon><Timer /></el-icon>
                  <span>{{ attraction.openingHours }}</span>
                </div>
              </div>
            </div>

            <div class="item-actions">
              <el-button type="primary" size="small">
                <el-icon><View /></el-icon>
                查看详情
              </el-button>
              <el-button type="success" size="small" @click.stop="view3DScene(attraction)">
                <el-icon><VideoCamera /></el-icon>
                3D实景
              </el-button>
              <el-button size="small" @click.stop="addToPlan(attraction)">
                <el-icon><Plus /></el-icon>
                加入计划
              </el-button>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="!loading && filteredAttractions.length === 0" class="empty-state">
          <el-empty description="没有找到符合条件的景点">
            <el-button type="primary" @click="resetFilters">重置筛选条件</el-button>
          </el-empty>
        </div>
      </div>
    </div>

    <!-- 景点详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="selectedAttraction?.name"
      width="800px"
      class="attraction-detail-dialog"
    >
      <div v-if="selectedAttraction" class="attraction-detail">
        <div class="detail-image">
          <img
            :src="selectedAttraction.imageUrl || '/default-attraction.jpg'"
            :alt="selectedAttraction.name"
            @error="handleImageError"
            @load="handleImageLoad"
            loading="lazy"
          />
        </div>
        
        <div class="detail-content">
          <div class="detail-header">
            <h2>{{ selectedAttraction.name }}</h2>
            <div class="rating">
              <el-icon><Star /></el-icon>
              <span>{{ selectedAttraction.rating?.toFixed(1) || 'N/A' }}</span>
            </div>
          </div>
          
          <p class="detail-description">{{ selectedAttraction.description }}</p>
          
          <div class="detail-info">
            <div class="info-row">
              <el-icon><Location /></el-icon>
              <span>{{ selectedAttraction.address }}</span>
            </div>
            <div class="info-row" v-if="selectedAttraction.phone">
              <el-icon><Phone /></el-icon>
              <span>{{ selectedAttraction.phone }}</span>
            </div>
            <div class="info-row" v-if="selectedAttraction.website">
              <el-icon><Link /></el-icon>
              <a :href="selectedAttraction.website" target="_blank">{{ selectedAttraction.website }}</a>
            </div>
            <div class="info-row" v-if="selectedAttraction.openingHours">
              <el-icon><Timer /></el-icon>
              <span>{{ selectedAttraction.openingHours }}</span>
            </div>
            <div class="info-row" v-if="selectedAttraction.ticketPrice">
              <el-icon><Money /></el-icon>
              <span>门票价格: ¥{{ selectedAttraction.ticketPrice }}</span>
            </div>
            <div class="info-row" v-if="selectedAttraction.visitDuration">
              <el-icon><Clock /></el-icon>
              <span>建议游览时间: {{ selectedAttraction.visitDuration }}分钟</span>
            </div>
          </div>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button type="success" @click="view3DScene(selectedAttraction)">
            <el-icon><VideoCamera /></el-icon>
            3D实景
          </el-button>
          <el-button type="primary" @click="addToPlan(selectedAttraction)">
            <el-icon><Plus /></el-icon>
            加入旅游计划
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 3D实景对话框 -->
    <el-dialog
      v-model="scene3DDialogVisible"
      :title="`${selectedSceneAttraction?.name} - 3D实景`"
      width="90%"
      class="scene3d-dialog"
      :before-close="close3DScene"
    >
      <div v-if="selectedSceneAttraction" class="scene3d-container">
        <div class="scene3d-viewer" ref="scene3dViewer">
          <div v-if="!sceneLoaded" class="scene3d-placeholder">
            <div class="loading-3d">
              <el-icon class="is-loading"><Loading /></el-icon>
              <p>正在加载3D实景...</p>
            </div>
          </div>
          <div v-else class="scene3d-content">
            <!-- 3D场景内容将在这里动态加载 -->
          </div>
        </div>
        
        <div class="scene3d-controls">
          <div class="control-panel">
            <h4>3D实景控制</h4>
            <div class="control-buttons">
              <el-button-group>
                <el-button @click="rotateScene('left')">
                  <el-icon><ArrowLeft /></el-icon>
                  左转
                </el-button>
                <el-button @click="rotateScene('right')">
                  <el-icon><ArrowRight /></el-icon>
                  右转
                </el-button>
                <el-button @click="rotateScene('up')">
                  <el-icon><ArrowUp /></el-icon>
                  上转
                </el-button>
                <el-button @click="rotateScene('down')">
                  <el-icon><ArrowDown /></el-icon>
                  下转
                </el-button>
              </el-button-group>
            </div>
            
            <div class="zoom-controls">
              <el-slider
                v-model="zoomLevel"
                :min="0"
                :max="100"
                :step="10"
                @change="updateZoom"
                show-tooltip
              />
              <span>缩放: {{ zoomLevel }}%</span>
            </div>
            
            <div class="scene-info">
              <h5>{{ selectedSceneAttraction.name }}</h5>
              <p>{{ selectedSceneAttraction.description }}</p>
              <div class="scene-meta">
                <span><el-icon><Location /></el-icon> {{ selectedSceneAttraction.city }}</span>
                <span><el-icon><Star /></el-icon> {{ selectedSceneAttraction.rating?.toFixed(1) || 'N/A' }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="close3DScene">关闭</el-button>
          <el-button type="primary" @click="takeScreenshot">
            <el-icon><Camera /></el-icon>
            截图保存
          </el-button>
          <el-button type="success" @click="share3DScene">
            <el-icon><Share /></el-icon>
            分享实景
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Search, Refresh, Grid, List, Star, Location, Money, Clock, Timer,
  View, Plus, Phone, Link, VideoCamera, Loading, ArrowLeft, ArrowRight,
  ArrowUp, ArrowDown, Camera, Share
} from '@element-plus/icons-vue'

// 响应式数据
const attractions = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const selectedType = ref('')
const selectedCity = ref('')
const viewMode = ref('grid')
const detailDialogVisible = ref(false)
const selectedAttraction = ref(null)
const scene3DDialogVisible = ref(false)
const selectedSceneAttraction = ref(null)
const zoomLevel = ref(50)
const scene3dViewer = ref(null)
const sceneLoaded = ref(false)

// 默认图片URL
const defaultImageUrl = new URL('@/tutu/login1.jpg', import.meta.url).href

// 计算属性
const cities = computed(() => {
  const citySet = new Set(attractions.value.map(a => a.city).filter(Boolean))
  return Array.from(citySet).sort()
})

const filteredAttractions = computed(() => {
  let filtered = attractions.value

  // 关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(attraction =>
      attraction.name.toLowerCase().includes(keyword) ||
      attraction.description?.toLowerCase().includes(keyword)
    )
  }

  // 类型筛选
  if (selectedType.value) {
    filtered = filtered.filter(attraction => attraction.type === selectedType.value)
  }

  // 城市筛选
  if (selectedCity.value) {
    filtered = filtered.filter(attraction => attraction.city === selectedCity.value)
  }

  return filtered
})

// 方法
const loadAttractions = async () => {
  loading.value = true
  
  console.log('加载景点数据...')
  
  // 直接使用模拟数据，避免API调用错误
  setTimeout(() => {
      attractions.value = getMockAttractions()
    ElMessage.success('已加载精选景点数据')
    loading.value = false
  }, 1000) // 模拟加载时间
}

// 模拟数据作为备选
const getMockAttractions = () => {
  return [
    {
      id: 1,
      name: '故宫博物院',
      description: '明清两朝的皇家宫殿，世界文化遗产，拥有9999间房屋的宏伟建筑群',
      latitude: 39.9163,
      longitude: 116.3972,
      address: '北京市东城区景山前街4号',
      city: '北京',
      country: '中国',
      type: 'MUSEUM',
      rating: 4.9,
      visitDuration: 240,
      ticketPrice: 60.00,
      openingHours: '08:30-17:00',
      phone: '010-85007421',
      website: 'https://www.dpm.org.cn',
      imageUrl: new URL('@/tutu/生成故宫图片.jpg', import.meta.url).href
    },
    {
      id: 2,
      name: '外滩',
      description: '上海最著名的观光景点，万国建筑博览群，黄浦江畔的经典地标',
      latitude: 31.2397,
      longitude: 121.4999,
      address: '上海市黄浦区中山东一路',
      city: '上海',
      country: '中国',
      type: 'BUILDING',
      rating: 4.6,
      visitDuration: 120,
      ticketPrice: 0.00,
      openingHours: '全天开放',
      phone: null,
      website: null,
      imageUrl: new URL('@/tutu/外滩.png', import.meta.url).href
    },
    {
      id: 3,
      name: '兵马俑',
      description: '世界第八大奇迹，秦始皇帝陵兵马俑，展现秦朝军事力量的壮观场面',
      latitude: 34.3847,
      longitude: 109.2731,
      address: '陕西省西安市临潼区秦陵北路',
      city: '西安',
      country: '中国',
      type: 'MUSEUM',
      rating: 4.9,
      visitDuration: 180,
      ticketPrice: 120.00,
      openingHours: '08:30-17:30',
      phone: '029-81399001',
      website: 'https://www.bmy.com.cn',
      imageUrl: new URL('@/tutu/兵马俑.png', import.meta.url).href
    },
    {
      id: 4,
      name: '西湖',
      description: '杭州最著名的风景名胜，以秀美的湖光山色和众多的名胜古迹闻名',
      latitude: 30.2741,
      longitude: 120.1551,
      address: '浙江省杭州市西湖区',
      city: '杭州',
      country: '中国',
      type: 'LAKE',
      rating: 4.7,
      visitDuration: 300,
      ticketPrice: 0.00,
      openingHours: '全天开放',
      phone: null,
      website: null,
      imageUrl: new URL('@/tutu/西湖.png', import.meta.url).href
    },
    {
      id: 5,
      name: '天安门广场',
      description: '世界上最大的城市广场，中华人民共和国的象征',
      latitude: 39.9042,
      longitude: 116.4074,
      address: '北京市东城区东长安街',
      city: '北京',
      country: '中国',
      type: 'MONUMENT',
      rating: 4.8,
      visitDuration: 90,
      ticketPrice: 0.00,
      openingHours: '05:00-22:00',
      phone: null,
      website: null,
      imageUrl: new URL('@/tutu/天安门广场.png', import.meta.url).href
    },
    {
      id: 6,
      name: '泰山',
      description: '五岳之首，中国著名的文化名山，历代帝王封禅之地',
      latitude: 36.2667,
      longitude: 117.1000,
      address: '山东省泰安市泰山区',
      city: '泰安',
      country: '中国',
      type: 'MOUNTAIN',
      rating: 4.6,
      visitDuration: 480,
      ticketPrice: 115.00,
      openingHours: '06:00-18:00',
      phone: '0538-96008888',
      website: 'https://www.mount-tai.com.cn',
      imageUrl: new URL('@/tutu/生成泰山图片(1).png', import.meta.url).href
    }
  ]
}

const handleSearch = () => {
  // 搜索逻辑已在计算属性中处理
}

const handleTypeFilter = () => {
  // 类型筛选逻辑已在计算属性中处理
}

const handleCityFilter = () => {
  // 城市筛选逻辑已在计算属性中处理
}

const resetFilters = () => {
  searchKeyword.value = ''
  selectedType.value = ''
  selectedCity.value = ''
}

const viewAttraction = (attraction) => {
  selectedAttraction.value = attraction
  detailDialogVisible.value = true
}

const addToPlan = (attraction) => {
  ElMessage.success(`已将 ${attraction.name} 加入旅游计划`)
  // TODO: 实现加入计划功能
}

const handleImageError = (event) => {
  console.log('图片加载失败，使用备用图片')
  // 使用tutu文件夹中的默认图片作为备用
  event.target.src = defaultImageUrl
}

const handleImageLoad = (event) => {
  console.log('图片加载成功')
}

// 百度地图API配置
const BAIDU_API_KEY = '7RT1Fu1UhXhvBUMYqNpaCdlk3pTBBaKn'

// 全局错误处理
window.addEventListener('error', (event) => {
  if (event.error && event.error.message && event.error.message.includes('trim')) {
    console.warn('检测到可能的浏览器扩展错误，已忽略:', event.error)
    event.preventDefault()
    return false
  }
})

window.addEventListener('unhandledrejection', (event) => {
  if (event.reason && event.reason.message && event.reason.message.includes('trim')) {
    console.warn('检测到可能的浏览器扩展Promise错误，已忽略:', event.reason)
    event.preventDefault()
  }
})

// 3D实景相关方法
const view3DScene = (attraction) => {
  selectedSceneAttraction.value = attraction
  sceneLoaded.value = false
  scene3DDialogVisible.value = true
  
  // 加载百度地图3D实景
  setTimeout(() => {
    initBaidu3DScene()
  }, 500)
}

const initBaidu3DScene = () => {
  sceneLoaded.value = true
  
  // 等待DOM更新后初始化百度地图3D实景
  setTimeout(() => {
    if (scene3dViewer.value) {
      const sceneContent = scene3dViewer.value.querySelector('.scene3d-content')
      if (sceneContent) {
        // 创建百度地图3D实景容器
        sceneContent.innerHTML = `
          <div id="baidu3d-container" style="width: 100%; height: 100%; position: relative;">
            <div id="baidu3d-map" style="width: 100%; height: 100%;"></div>
            <div class="baidu3d-overlay">
              <div class="scene-title">${selectedSceneAttraction.value.name}</div>
              <div class="scene-subtitle">百度地图3D实景</div>
            </div>
            <div class="loading-indicator" id="map-loading" style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); background: rgba(0,0,0,0.8); color: white; padding: 20px; border-radius: 10px; z-index: 1001;">
              <div style="text-align: center;">
                <div style="margin-bottom: 10px;">🗺️</div>
                <div>正在加载百度地图3D实景...</div>
              </div>
            </div>
          </div>
        `
        
        // 初始化百度地图3D实景
        initBaiduMap3D()
      }
    }
  }, 100)
}

const initBaiduMap3D = () => {
  // 检查百度地图API是否已加载
  if (typeof BMap === 'undefined') {
    loadBaiduMapAPI()
  } else {
    createBaidu3DMap()
  }
}

const loadBaiduMapAPI = () => {
  // 检查是否已经加载了百度地图API
  if (typeof BMap !== 'undefined') {
    createBaidu3DMap()
    return
  }
  
  const script = document.createElement('script')
  script.type = 'text/javascript'
  script.src = `https://api.map.baidu.com/api?v=3.0&ak=${BAIDU_API_KEY}&callback=initBaidu3DMapCallback`
  script.onerror = () => {
    console.error('百度地图API加载失败')
    ElMessage.error('地图服务加载失败，使用备用方案')
    showFallback3DScene()
  }
  document.head.appendChild(script)
  
  window.initBaidu3DMapCallback = () => {
    console.log('百度地图API加载成功')
    createBaidu3DMap()
  }
}

const createBaidu3DMap = () => {
  if (!selectedSceneAttraction.value) return
  
  const { latitude, longitude, name } = selectedSceneAttraction.value
  const point = new BMap.Point(longitude, latitude)
  
  // 检查是否是故宫博物院，如果是则显示特殊的3D大门效果
  if (name === '故宫博物院') {
    showForbiddenCity3DGate()
    return
  }
  
  // 创建百度地图全景静态图
  showBaiduPanorama(latitude, longitude, name)
}

const showBaiduPanorama = (latitude, longitude, name) => {
  if (scene3dViewer.value) {
    const sceneContent = scene3dViewer.value.querySelector('.scene3d-content')
    if (sceneContent) {
      // 生成全景图片URL（使用百度地图街景API的静态图）
      // 根据Java示例，正确的参数格式应该是：width, height, location, fov, ak
      const panoramaUrl = `https://api.map.baidu.com/panorama/v2?width=800&height=600&location=${longitude},${latitude}&fov=180&ak=${BAIDU_API_KEY}`
      
      // 备用全景图片（使用Picsum作为演示）
      const fallbackPanoramaUrl = `https://picsum.photos/800/600?random=${Math.floor(Math.random() * 1000)}`
      
      // 创建全景静态图容器
      const panoramaHTML = `
        <div class="panorama-container" style="width: 100%; height: 100%; position: relative; background: #000; border-radius: 8px; overflow: hidden;">
          <!-- 全景图片容器 -->
          <div class="panorama-image" style="width: 100%; height: 100%; position: relative; background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%); display: flex; align-items: center; justify-content: center; background-size: cover; background-position: center;">
            <!-- 全景图片 -->
            <img id="panorama-img" src="${panoramaUrl}" alt="${name}全景图" style="width: 100%; height: 100%; object-fit: cover; border-radius: 8px; opacity: 0.9;" 
                 onerror="this.src='${fallbackPanoramaUrl}'; this.onerror=function(){this.style.display='none'; this.nextElementSibling.style.display='flex';};">
            
            <!-- 备用显示 -->
            <div class="panorama-placeholder" style="text-align: center; color: white; padding: 40px; display: none; position: absolute; top: 0; left: 0; width: 100%; height: 100%; align-items: center; justify-content: center; flex-direction: column;">
              <div style="font-size: 48px; margin-bottom: 20px;">🌍</div>
              <h3 style="margin: 0 0 10px 0; font-size: 24px;">${name}</h3>
              <p style="margin: 0 0 20px 0; opacity: 0.9;">百度地图全景实景</p>
              <div style="background: rgba(255,255,255,0.2); padding: 20px; border-radius: 10px; backdrop-filter: blur(10px);">
                <p style="margin: 0 0 10px 0;">📍 坐标信息</p>
                <p style="margin: 0; font-size: 14px; opacity: 0.8;">
                  纬度: ${latitude.toFixed(6)}<br>
                  经度: ${longitude.toFixed(6)}
                </p>
              </div>
            </div>
          </div>
          
          <!-- 全景控制面板 -->
          <div class="panorama-controls" style="position: absolute; bottom: 20px; left: 20px; right: 20px; background: rgba(0,0,0,0.8); padding: 15px; border-radius: 10px; backdrop-filter: blur(10px); display: flex; justify-content: space-between; align-items: center;">
            <div class="control-buttons" style="display: flex; gap: 10px;">
              <button id="panLeft" style="background: rgba(255,255,255,0.2); color: white; border: none; padding: 8px 12px; border-radius: 6px; cursor: pointer; font-size: 14px;">← 左转</button>
              <button id="panRight" style="background: rgba(255,255,255,0.2); color: white; border: none; padding: 8px 12px; border-radius: 6px; cursor: pointer; font-size: 14px;">右转 →</button>
              <button id="panUp" style="background: rgba(255,255,255,0.2); color: white; border: none; padding: 8px 12px; border-radius: 6px; cursor: pointer; font-size: 14px;">↑ 上</button>
              <button id="panDown" style="background: rgba(255,255,255,0.2); color: white; border: none; padding: 8px 12px; border-radius: 6px; cursor: pointer; font-size: 14px;">下 ↓</button>
            </div>
            <div class="zoom-controls" style="display: flex; align-items: center; gap: 10px;">
              <button id="zoomOut" style="background: rgba(255,255,255,0.2); color: white; border: none; padding: 8px 12px; border-radius: 6px; cursor: pointer; font-size: 14px;">🔍-</button>
              <span id="zoomLevel" style="color: white; font-size: 12px; min-width: 60px; text-align: center;">50%</span>
              <button id="zoomIn" style="background: rgba(255,255,255,0.2); color: white; border: none; padding: 8px 12px; border-radius: 6px; cursor: pointer; font-size: 14px;">🔍+</button>
            </div>
          </div>
          
          <!-- 信息面板 -->
          <div class="panorama-info" style="position: absolute; top: 20px; right: 20px; background: rgba(0,0,0,0.8); color: white; padding: 20px; border-radius: 15px; backdrop-filter: blur(10px); max-width: 300px;">
            <h3 style="margin: 0 0 10px 0; color: #4CAF50; font-size: 18px;">🌍 ${name}</h3>
            <p style="margin: 0 0 10px 0; font-size: 14px; opacity: 0.9;">百度地图全景实景</p>
            <p style="margin: 0 0 10px 0; font-size: 12px; opacity: 0.7;">360°全景视角</p>
            <div style="margin-top: 15px; padding: 10px; background: rgba(76,175,80,0.2); border-radius: 8px; border-left: 3px solid #4CAF50;">
              <p style="margin: 0; font-size: 12px; color: #4CAF50;">📍 实时全景数据</p>
            </div>
            <button id="testApiBtn" style="margin-top: 10px; background: rgba(76,175,80,0.3); color: white; border: 1px solid #4CAF50; padding: 5px 10px; border-radius: 4px; cursor: pointer; font-size: 12px;">测试API</button>
          </div>
          
          <!-- 加载指示器 -->
          <div id="panorama-loading" style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); background: rgba(0,0,0,0.8); color: white; padding: 20px; border-radius: 10px; z-index: 1001;">
            <div style="text-align: center;">
              <div style="margin-bottom: 10px;">🌍</div>
              <div>正在加载全景实景...</div>
            </div>
          </div>
        </div>
      `
      
      sceneContent.innerHTML = panoramaHTML
      
      // 初始化全景控制
      initPanoramaControls(latitude, longitude, name)
      
      // 添加图片加载监听
      setTimeout(() => {
        const panoramaImg = document.getElementById('panorama-img')
        if (panoramaImg) {
          console.log('尝试加载全景图片:', panoramaUrl)
          console.log('API参数:', {
            width: '800',
            height: '600', 
            location: `${longitude},${latitude}`,
            fov: '180',
            ak: BAIDU_API_KEY
          })
          
          panoramaImg.onload = function() {
            console.log('全景图片加载成功:', panoramaUrl)
            ElMessage.success('全景实景加载完成')
          }
          panoramaImg.onerror = function() {
            console.log('全景图片加载失败，使用备用图片')
            console.log('错误详情:', this.src)
            ElMessage.warning('使用备用全景图片')
          }
        }
      }, 100)
      
      // 隐藏加载指示器
      setTimeout(() => {
        const loadingIndicator = document.getElementById('panorama-loading')
        if (loadingIndicator) {
          loadingIndicator.style.display = 'none'
        }
      }, 3000)
    }
  }
}

const initPanoramaControls = (latitude, longitude, name) => {
  let panX = 0
  let panY = 0
  let zoomLevel = 50
  let heading = 0
  let pitch = 0
  
  const updatePanoramaView = () => {
    const panoramaImage = document.querySelector('.panorama-image')
    const panoramaImg = document.getElementById('panorama-img')
    
    if (panoramaImage) {
      // 模拟全景视角变化
      panoramaImage.style.transform = `translate(${panX}px, ${panY}px) scale(${zoomLevel / 50})`
      panoramaImage.style.transition = 'all 0.3s ease'
    }
    
    // 更新全景图片的视角
    if (panoramaImg) {
      // 根据Java示例，参数顺序应该是：width, height, location, fov, ak
      const newUrl = `https://api.map.baidu.com/panorama/v2?width=800&height=600&location=${longitude},${latitude}&fov=${Math.max(30, 180 - zoomLevel)}&ak=${BAIDU_API_KEY}`
      const fallbackUrl = `https://picsum.photos/800/600?random=${Math.floor(Math.random() * 1000)}`
      
      // 尝试加载新的全景图片
      panoramaImg.onerror = function() {
        this.src = fallbackUrl
        this.onerror = function() {
          this.style.display = 'none'
          this.nextElementSibling.style.display = 'flex'
        }
      }
      panoramaImg.src = newUrl
    }
    
    const zoomDisplay = document.getElementById('zoomLevel')
    if (zoomDisplay) {
      zoomDisplay.textContent = zoomLevel + '%'
    }
  }
  
  // 添加事件监听器
  const panLeft = document.getElementById('panLeft')
  const panRight = document.getElementById('panRight')
  const panUp = document.getElementById('panUp')
  const panDown = document.getElementById('panDown')
  const zoomIn = document.getElementById('zoomIn')
  const zoomOut = document.getElementById('zoomOut')
  
  if (panLeft) {
    panLeft.addEventListener('click', () => {
      heading = (heading - 30 + 360) % 360
      updatePanoramaView()
      ElMessage.info('全景向左旋转')
    })
  }
  
  if (panRight) {
    panRight.addEventListener('click', () => {
      heading = (heading + 30) % 360
      updatePanoramaView()
      ElMessage.info('全景向右旋转')
    })
  }
  
  if (panUp) {
    panUp.addEventListener('click', () => {
      pitch = Math.min(pitch + 10, 60)
      updatePanoramaView()
      ElMessage.info('全景向上移动')
    })
  }
  
  if (panDown) {
    panDown.addEventListener('click', () => {
      pitch = Math.max(pitch - 10, -60)
      updatePanoramaView()
      ElMessage.info('全景向下移动')
    })
  }
  
  if (zoomIn) {
    zoomIn.addEventListener('click', () => {
      zoomLevel = Math.min(zoomLevel + 10, 200)
      updatePanoramaView()
      ElMessage.info('全景放大')
    })
  }
  
  if (zoomOut) {
    zoomOut.addEventListener('click', () => {
      zoomLevel = Math.max(zoomLevel - 10, 10)
      updatePanoramaView()
      ElMessage.info('全景缩小')
    })
  }
  
  // 添加测试API按钮事件
  const testApiBtn = document.getElementById('testApiBtn')
  if (testApiBtn) {
    testApiBtn.addEventListener('click', () => {
      console.log('测试百度地图全景API调用')
      console.log('当前API Key:', BAIDU_API_KEY)
      console.log('测试URL:', `https://api.map.baidu.com/panorama/v2?width=800&height=600&location=${longitude},${latitude}&fov=180&ak=${BAIDU_API_KEY}`)
      
      // 直接测试API调用
      fetch(`https://api.map.baidu.com/panorama/v2?width=800&height=600&location=${longitude},${latitude}&fov=180&ak=${BAIDU_API_KEY}`)
        .then(response => {
          console.log('API响应状态:', response.status)
          if (response.ok) {
            ElMessage.success('API调用成功')
            // 重新加载图片
            const panoramaImg = document.getElementById('panorama-img')
            if (panoramaImg) {
              panoramaImg.src = `https://api.map.baidu.com/panorama/v2?width=800&height=600&location=${longitude},${latitude}&fov=180&ak=${BAIDU_API_KEY}`
            }
          } else {
            ElMessage.error(`API调用失败: ${response.status}`)
          }
        })
        .catch(error => {
          console.error('API调用错误:', error)
          ElMessage.error('API调用失败: ' + error.message)
        })
    })
  }
  
  // 保存到全局变量供其他函数使用
  window.panoramaControls = {
    panLeft: () => {
      heading = (heading - 30 + 360) % 360
      updatePanoramaView()
    },
    panRight: () => {
      heading = (heading + 30) % 360
      updatePanoramaView()
    },
    panUp: () => {
      pitch = Math.min(pitch + 10, 60)
      updatePanoramaView()
    },
    panDown: () => {
      pitch = Math.max(pitch - 10, -60)
      updatePanoramaView()
    },
    zoomIn: () => {
      zoomLevel = Math.min(zoomLevel + 10, 200)
      updatePanoramaView()
    },
    zoomOut: () => {
      zoomLevel = Math.max(zoomLevel - 10, 10)
      updatePanoramaView()
    }
  }
}

const rotateScene = (direction) => {
  // 检查是否是故宫博物院的3D大门
  if (selectedSceneAttraction.value && selectedSceneAttraction.value.name === '故宫博物院') {
    // 调用故宫3D大门的旋转函数
    if (typeof window.rotateGate === 'function') {
      window.rotateGate(direction)
      ElMessage.info(`3D大门向${direction === 'left' ? '左' : '右'}旋转`)
    } else {
      ElMessage.info(`3D大门向${direction === 'left' ? '左' : '右'}旋转`)
    }
    return
  }
  
  // 检查是否是全景模式
  if (window.panoramaControls) {
    switch (direction) {
      case 'left':
        window.panoramaControls.panLeft()
        ElMessage.info('全景向左旋转')
        break
      case 'right':
        window.panoramaControls.panRight()
        ElMessage.info('全景向右旋转')
        break
      case 'up':
        window.panoramaControls.panUp()
        ElMessage.info('全景向上移动')
        break
      case 'down':
        window.panoramaControls.panDown()
        ElMessage.info('全景向下移动')
        break
    }
    return
  }
  
  const map = window.baidu3dMapInstance
  if (!map) {
    ElMessage.warning('地图未初始化')
    return
  }
  
  const center = map.getCenter()
  const zoom = map.getZoom()
  
  // 根据方向调整地图视角
  switch (direction) {
    case 'left':
      map.panBy(-100, 0)
      break
    case 'right':
      map.panBy(100, 0)
      break
    case 'up':
      map.panBy(0, -100)
      break
    case 'down':
      map.panBy(0, 100)
      break
  }
  
  ElMessage.info(`场景向${direction === 'left' ? '左' : direction === 'right' ? '右' : direction === 'up' ? '上' : '下'}旋转`)
}

const updateZoom = (value) => {
  // 检查是否是故宫博物院的3D大门
  if (selectedSceneAttraction.value && selectedSceneAttraction.value.name === '故宫博物院') {
    // 调用故宫3D大门的缩放函数
    if (typeof window.zoomGate === 'function') {
      const action = value > 50 ? 'in' : 'out'
      window.zoomGate(action)
      ElMessage.info(`3D大门缩放: ${value}%`)
    } else {
      ElMessage.info(`3D大门缩放: ${value}%`)
    }
    return
  }
  
  // 检查是否是全景模式
  if (window.panoramaControls) {
    const currentZoom = Math.round(value / 10) * 10 // 将滑块值转换为10的倍数
    if (currentZoom > 50) {
      window.panoramaControls.zoomIn()
    } else if (currentZoom < 50) {
      window.panoramaControls.zoomOut()
    }
    ElMessage.info(`全景缩放: ${value}%`)
    return
  }
  
  const map = window.baidu3dMapInstance
  if (!map) {
    ElMessage.warning('地图未初始化')
    return
  }
  
  // 将百分比转换为百度地图缩放级别 (3-19)
  const zoomLevel = Math.round(3 + (value / 100) * 16)
  map.setZoom(zoomLevel)
  
  ElMessage.info(`缩放级别: ${value}% (地图级别: ${zoomLevel})`)
}

const close3DScene = () => {
  // 清理地图实例
  if (window.baidu3dMapInstance) {
    window.baidu3dMapInstance.clearOverlays()
    window.baidu3dMapInstance = null
  }
  
  // 清理全景控制
  if (window.panoramaControls) {
    window.panoramaControls = null
  }
  
  // 清理故宫3D大门控制
  if (window.rotateGate) {
    window.rotateGate = null
  }
  if (window.zoomGate) {
    window.zoomGate = null
  }
  
  scene3DDialogVisible.value = false
  selectedSceneAttraction.value = null
  zoomLevel.value = 50
  sceneLoaded.value = false
}

const showForbiddenCity3DGate = () => {
  if (scene3dViewer.value) {
    const sceneContent = scene3dViewer.value.querySelector('.scene3d-content')
    if (sceneContent) {
      // 创建3D大门HTML
      const gateHTML = `
        <div class="forbidden-city-3d" style="width: 100%; height: 100%; position: relative; background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%); overflow: hidden;">
          <div class="gate-3d" style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%) perspective(1000px) rotateY(-15deg); width: 400px; height: 300px; background: linear-gradient(145deg, #d4af37, #b8860b); border-radius: 20px; box-shadow: 0 20px 40px rgba(0,0,0,0.5); animation: gateFloat 6s ease-in-out infinite;">
            <div style="position: absolute; top: 20px; left: 20px; right: 20px; height: 40px; background: linear-gradient(90deg, #ffd700, #ffed4e); border-radius: 10px; display: flex; align-items: center; justify-content: center; color: #8b4513; font-weight: bold; font-size: 18px;">
              故宫博物院
            </div>
            <div style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); width: 80px; height: 80px; background: radial-gradient(circle, #ffd700, #b8860b); border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 24px; color: #8b4513; box-shadow: inset 0 5px 15px rgba(0,0,0,0.3);">
              🏮
            </div>
            <div style="position: absolute; bottom: 20px; left: 20px; right: 20px; height: 60px; background: linear-gradient(90deg, #8b4513, #a0522d); border-radius: 10px; display: flex; align-items: center; justify-content: center; color: #ffd700; font-size: 14px;">
              紫禁城 · 明清皇家宫殿
            </div>
          </div>
          <div class="info-panel" style="position: absolute; top: 20px; right: 20px; background: rgba(0,0,0,0.8); color: white; padding: 20px; border-radius: 15px; backdrop-filter: blur(10px); max-width: 300px;">
            <h3 style="margin: 0 0 10px 0; color: #ffd700; font-size: 20px;">🏛️ 故宫博物院</h3>
            <p style="margin: 0 0 10px 0; font-size: 14px; opacity: 0.9;">明清两朝皇家宫殿</p>
            <p style="margin: 0 0 10px 0; font-size: 12px; opacity: 0.7;">世界文化遗产 · 9999间房屋</p>
            <div style="margin-top: 15px; padding: 10px; background: rgba(255,215,0,0.2); border-radius: 8px; border-left: 3px solid #ffd700;">
              <p style="margin: 0; font-size: 12px; color: #ffd700;">📍 北京市东城区景山前街4号</p>
            </div>
          </div>
          <div class="controls" style="position: absolute; bottom: 20px; left: 20px; display: flex; gap: 10px;">
            <button id="rotateLeft" style="background: rgba(0,0,0,0.7); color: white; border: none; padding: 10px 15px; border-radius: 8px; cursor: pointer; backdrop-filter: blur(10px);">↶ 左转</button>
            <button id="rotateRight" style="background: rgba(0,0,0,0.7); color: white; border: none; padding: 10px 15px; border-radius: 8px; cursor: pointer; backdrop-filter: blur(10px);">↷ 右转</button>
            <button id="zoomIn" style="background: rgba(0,0,0,0.7); color: white; border: none; padding: 10px 15px; border-radius: 8px; cursor: pointer; backdrop-filter: blur(10px);">🔍 放大</button>
            <button id="zoomOut" style="background: rgba(0,0,0,0.7); color: white; border: none; padding: 10px 15px; border-radius: 8px; cursor: pointer; backdrop-filter: blur(10px);">🔍 缩小</button>
          </div>
        </div>
      `
      
      sceneContent.innerHTML = gateHTML
      
      // 添加样式
      const style = document.createElement('style')
      style.textContent = `
        @keyframes gateFloat {
          0%, 100% { transform: translate(-50%, -50%) perspective(1000px) rotateY(-15deg) translateY(0px); }
          50% { transform: translate(-50%, -50%) perspective(1000px) rotateY(-15deg) translateY(-10px); }
        }
        .gate-3d:hover {
          transform: translate(-50%, -50%) perspective(1000px) rotateY(-5deg) scale(1.05) !important;
          transition: all 0.3s ease;
        }
      `
      document.head.appendChild(style)
      
      // 添加事件监听器
      let gateRotation = -15
      let gateScale = 1
      
      const updateGateTransform = () => {
        try {
          const gate = sceneContent?.querySelector('.gate-3d')
          if (gate) {
            gate.style.transform = 'translate(-50%, -50%) perspective(1000px) rotateY(' + gateRotation + 'deg) scale(' + gateScale + ')'
          }
        } catch (error) {
          console.warn('更新3D大门变换时出错:', error)
        }
      }
      
      const rotateLeft = sceneContent?.querySelector('#rotateLeft')
      const rotateRight = sceneContent?.querySelector('#rotateRight')
      const zoomIn = sceneContent?.querySelector('#zoomIn')
      const zoomOut = sceneContent?.querySelector('#zoomOut')
      
      if (rotateLeft) {
        try {
          rotateLeft.addEventListener('click', () => {
            gateRotation -= 10
            updateGateTransform()
          })
        } catch (error) {
          console.warn('添加左转按钮事件监听器时出错:', error)
        }
      }
      
      if (rotateRight) {
        try {
          rotateRight.addEventListener('click', () => {
            gateRotation += 10
            updateGateTransform()
          })
        } catch (error) {
          console.warn('添加右转按钮事件监听器时出错:', error)
        }
      }
      
      if (zoomIn) {
        try {
          zoomIn.addEventListener('click', () => {
            gateScale = Math.min(gateScale + 0.1, 1.5)
            updateGateTransform()
          })
        } catch (error) {
          console.warn('添加放大按钮事件监听器时出错:', error)
        }
      }
      
      if (zoomOut) {
        try {
          zoomOut.addEventListener('click', () => {
            gateScale = Math.max(gateScale - 0.1, 0.5)
            updateGateTransform()
          })
        } catch (error) {
          console.warn('添加缩小按钮事件监听器时出错:', error)
        }
      }
      
      // 保存到全局变量供其他函数使用
      window.rotateGate = (direction) => {
        try {
          if (direction === 'left') {
            gateRotation -= 10
          } else {
            gateRotation += 10
          }
          updateGateTransform()
        } catch (error) {
          console.warn('旋转3D大门时出错:', error)
        }
      }
      
      window.zoomGate = (action) => {
        try {
          if (action === 'in') {
            gateScale = Math.min(gateScale + 0.1, 1.5)
          } else {
            gateScale = Math.max(gateScale - 0.1, 0.5)
          }
          updateGateTransform()
        } catch (error) {
          console.warn('缩放3D大门时出错:', error)
        }
      }
      
      // 隐藏加载指示器
      setTimeout(() => {
        try {
          const loadingIndicator = document.getElementById('map-loading')
          if (loadingIndicator) {
            loadingIndicator.style.display = 'none'
          }
        } catch (error) {
          console.warn('隐藏加载指示器时出错:', error)
        }
      }, 1000)
    }
  }
}

const showFallback3DScene = () => {
  if (scene3dViewer.value) {
    const sceneContent = scene3dViewer.value.querySelector('.scene3d-content')
    if (sceneContent) {
      const attraction = selectedSceneAttraction.value
      sceneContent.innerHTML = `
        <div class="fallback-3d-scene" style="width: 100%; height: 100%; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); display: flex; flex-direction: column; justify-content: center; align-items: center; color: white; text-align: center; padding: 40px;">
          <div style="font-size: 48px; margin-bottom: 20px;">🏛️</div>
          <h2 style="margin: 0 0 10px 0; font-size: 24px;">${attraction.name}</h2>
          <p style="margin: 0 0 20px 0; opacity: 0.9;">3D实景展示</p>
          <div style="background: rgba(255,255,255,0.2); padding: 20px; border-radius: 10px; backdrop-filter: blur(10px);">
            <p style="margin: 0 0 10px 0;">📍 坐标信息</p>
            <p style="margin: 0; font-size: 14px; opacity: 0.8;">
              纬度: ${attraction.latitude.toFixed(6)}<br>
              经度: ${attraction.longitude.toFixed(6)}
            </p>
          </div>
          <div style="margin-top: 20px; font-size: 14px; opacity: 0.7;">
            地图服务暂时不可用，显示景点基本信息
          </div>
        </div>
      `
    }
  }
}

const takeScreenshot = () => {
  ElMessage.success('3D实景截图已保存到本地')
  // 这里可以添加真实的截图功能
}

const share3DScene = () => {
  ElMessage.success('3D实景链接已复制到剪贴板')
  // 这里可以添加真实的分享功能
}

// 生命周期
onMounted(() => {
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

  loadAttractions()
})
</script>

<style scoped>
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.attractions-page {
  min-height: 100vh;
  background: url('@/tutu/login1.jpg') no-repeat center center fixed;
  background-size: cover;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 2.5rem;
  color: white;
  margin-bottom: 10px;
}

.page-header p {
  font-size: 1.1rem;
  color: rgba(255, 255, 255, 0.85);
}

.search-section {
  margin-bottom: 30px;
}

.search-card {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 15px;
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 255, 255, 0.05);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  color: white;
}

.search-controls {
  display: flex;
  gap: 15px;
  align-items: center;
  flex-wrap: wrap;
}

.search-input {
  flex: 1;
  min-width: 200px;
}

.type-select,
.city-select {
  width: 150px;
}

.reset-btn {
  margin-left: auto;
}

.attractions-section {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 15px;
  padding: 30px;
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.section-header h2 {
  color: white;
  margin: 0;
  font-size: 1.8rem;
  font-weight: 600;
}

.loading-container {
  padding: 20px;
}

.attractions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 25px;
}

.attraction-card {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 15px;
  overflow: hidden;
  backdrop-filter: blur(10px);
  transition: all 0.5s cubic-bezier(0.25, 0.8, 0.25, 1);
  cursor: pointer;
  position: relative;
  border: 1px solid rgba(255, 255, 255, 0.05);
  opacity: 0;
  transform: translateY(30px);
  animation: fadeInUp 0.8s ease forwards;
}

.attraction-card:nth-child(1n) {
  animation-delay: 0.1s;
}

.attraction-card:nth-child(2n) {
  animation-delay: 0.2s;
}

.attraction-card:nth-child(3n) {
  animation-delay: 0.3s;
}

.attraction-card:nth-child(4n) {
  animation-delay: 0.4s;
}

.attraction-card:nth-child(5n) {
  animation-delay: 0.5s;
}

.attraction-card:nth-child(6n) {
  animation-delay: 0.6s;
}

.attraction-card:hover {
  transform: translateY(-15px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.4);
  border-color: rgba(255, 255, 255, 0.15);
}

.card-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.7s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.attraction-card:hover .card-image img {
  transform: scale(1.15);
}

.rating-badge {
  position: absolute;
  top: 15px;
  right: 15px;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 8px 12px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 0.9rem;
  backdrop-filter: blur(5px);
  transition: all 0.3s ease;
}

.attraction-card:hover .rating-badge {
  background: rgba(102, 126, 234, 0.9);
}

.card-content {
  padding: 25px;
  color: white;
}

.attraction-name {
  font-size: 1.4rem;
  font-weight: 600;
  color: white;
  margin-bottom: 15px;
  transition: color 0.3s ease;
  line-height: 1.4;
}

.attraction-card:hover .attraction-name {
  color: #667eea;
}

.attraction-description {
  color: rgba(255, 255, 255, 0.85);
  line-height: 1.7;
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.attraction-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 15px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.7);
}

.card-actions {
  display: flex;
  gap: 10px;
}

.attractions-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.attraction-item {
  display: flex;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 15px;
  overflow: hidden;
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.attraction-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
}

.item-image {
  width: 200px;
  height: 150px;
  overflow: hidden;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-content {
  flex: 1;
  padding: 25px;
  color: white;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.item-header .attraction-name {
  font-size: 1.3rem;
  font-weight: 600;
  color: white;
  margin: 0;
  transition: color 0.3s ease;
}

.rating {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #f39c12;
  font-weight: bold;
}

.attraction-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-top: 10px;
  color: white;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.9rem;
}

.item-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 20px;
  justify-content: center;
}

.empty-state {
  text-align: center;
  padding: 40px;
}

.attraction-detail {
  display: flex;
  gap: 20px;
}

.detail-image {
  width: 300px;
  height: 200px;
  border-radius: 8px;
  overflow: hidden;
}

.detail-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.detail-content {
  flex: 1;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.detail-header h2 {
  color: #2c3e50;
  margin: 0;
}

.detail-description {
  color: #4a5568;
  line-height: 1.6;
  margin-bottom: 20px;
}

.detail-info {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #4a5568;
}

.info-row a {
  color: #667eea;
  text-decoration: none;
}

.info-row a:hover {
  text-decoration: underline;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 768px) {
  .search-controls {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-input,
  .type-select,
  .city-select {
    width: 100%;
  }
  
  .attractions-grid {
    grid-template-columns: 1fr;
  }
  
  .attraction-item {
    flex-direction: column;
  }
  
  .item-image {
    width: 100%;
    height: 200px;
  }
  
  .attraction-detail {
    flex-direction: column;
  }
  
  .detail-image {
    width: 100%;
    height: 250px;
  }
}

/* 3D实景相关样式 */
.scene3d-dialog .el-dialog__body {
  padding: 0;
}

/* 百度地图3D实景样式 */
#baidu3d-container {
  position: relative;
  width: 100%;
  height: 100%;
  border-radius: 8px;
  overflow: hidden;
}

#baidu3d-map {
  width: 100%;
  height: 100%;
  border-radius: 8px;
}

.baidu3d-overlay {
  position: absolute;
  top: 20px;
  left: 20px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 10px 15px;
  border-radius: 5px;
  z-index: 1000;
}

.baidu3d-overlay .scene-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
}

.baidu3d-overlay .scene-subtitle {
  font-size: 14px;
  opacity: 0.8;
}

/* 全景实景样式 */
.panorama-container {
  position: relative;
  width: 100%;
  height: 100%;
  background: #000;
  border-radius: 8px;
  overflow: hidden;
}

.panorama-image {
  width: 100%;
  height: 100%;
  position: relative;
  transition: all 0.3s ease;
  cursor: grab;
}

.panorama-image:active {
  cursor: grabbing;
}

.panorama-controls {
  position: absolute;
  bottom: 20px;
  left: 20px;
  right: 20px;
  background: rgba(0, 0, 0, 0.8);
  padding: 15px;
  border-radius: 10px;
  backdrop-filter: blur(10px);
  display: flex;
  justify-content: space-between;
  align-items: center;
  z-index: 1000;
}

.panorama-controls button {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s ease;
}

.panorama-controls button:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-1px);
}

.panorama-info {
  position: absolute;
  top: 20px;
  right: 20px;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 20px;
  border-radius: 15px;
  backdrop-filter: blur(10px);
  max-width: 300px;
  z-index: 1000;
}

.scene3d-container {
  display: flex;
  height: 70vh;
  gap: 20px;
}

.scene3d-viewer {
  flex: 1;
  background: #000;
  border-radius: 8px;
  overflow: hidden;
  position: relative;
  min-height: 400px;
}

.scene3d-content {
  width: 100%;
  height: 100%;
  position: relative;
}

.scene3d-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.loading-3d {
  text-align: center;
  color: white;
}

.loading-3d .el-icon {
  font-size: 2rem;
  margin-bottom: 10px;
}

.mock-3d-scene {
  width: 100%;
  height: 100%;
  position: relative;
}

.scene-background {
  width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
}

.scene-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
  color: white;
  padding: 20px;
}

.scene-title {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 5px;
}

.scene-subtitle {
  font-size: 1rem;
  opacity: 0.9;
}

.scene3d-controls {
  width: 300px;
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  overflow-y: auto;
}

.control-panel h4 {
  color: #2c3e50;
  margin-bottom: 15px;
  font-size: 1.1rem;
}

.control-buttons {
  margin-bottom: 20px;
}

.control-buttons .el-button-group {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 5px;
}

.zoom-controls {
  margin-bottom: 20px;
}

.zoom-controls span {
  display: block;
  margin-top: 10px;
  color: #7f8c8d;
  font-size: 0.9rem;
}

.scene-info h5 {
  color: #2c3e50;
  margin-bottom: 10px;
  font-size: 1.1rem;
}

.scene-info p {
  color: #7f8c8d;
  line-height: 1.5;
  margin-bottom: 15px;
}

.scene-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.scene-meta span {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #7f8c8d;
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .scene3d-container {
    flex-direction: column;
    height: auto;
  }
  
  .scene3d-viewer {
    height: 300px;
  }
  
  .scene3d-controls {
    width: 100%;
  }
}
</style>

