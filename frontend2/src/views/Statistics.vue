<template>
  <div class="statistics">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">数据统计</h1>
      <p class="page-description">系统数据分析和可视化展示</p>
    </div>

    <!-- 统计概览 -->
    <div class="stats-overview">
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ overviewData.totalUsers }}</div>
            <div class="stat-label">总用户数</div>
            <div class="stat-trend positive">
              <el-icon><TrendCharts /></el-icon>
              <span>+12.5%</span>
            </div>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ overviewData.totalPlans }}</div>
            <div class="stat-label">旅游计划</div>
            <div class="stat-trend positive">
              <el-icon><TrendCharts /></el-icon>
              <span>+8.3%</span>
            </div>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><Location /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ overviewData.totalAttractions }}</div>
            <div class="stat-label">景点数量</div>
            <div class="stat-trend positive">
              <el-icon><TrendCharts /></el-icon>
              <span>+5.2%</span>
            </div>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><Money /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">¥{{ overviewData.totalRevenue }}</div>
            <div class="stat-label">总收入</div>
            <div class="stat-trend positive">
              <el-icon><TrendCharts /></el-icon>
              <span>+15.7%</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <!-- 第一行图表 -->
      <div class="chart-row">
        <div class="content-card">
          <div class="card-header">
            <h3>用户增长趋势</h3>
            <el-radio-group v-model="userGrowthPeriod" @change="updateUserGrowthChart">
              <el-radio-button label="7d">近7天</el-radio-button>
              <el-radio-button label="30d">近30天</el-radio-button>
              <el-radio-button label="90d">近90天</el-radio-button>
            </el-radio-group>
          </div>
          <div class="card-body">
            <div ref="userGrowthChartRef" class="chart-container"></div>
          </div>
        </div>
        
        <div class="content-card">
          <div class="card-header">
            <h3>旅游计划状态分布</h3>
          </div>
          <div class="card-body">
            <div ref="planStatusChartRef" class="chart-container"></div>
          </div>
        </div>
      </div>

      <!-- 第二行图表 -->
      <div class="chart-row">
        <div class="content-card">
          <div class="card-header">
            <h3>热门目的地排行</h3>
          </div>
          <div class="card-body">
            <div ref="destinationRankingChartRef" class="chart-container"></div>
          </div>
        </div>
        
        <div class="content-card">
          <div class="card-header">
            <h3>月度收入统计</h3>
            <el-radio-group v-model="revenuePeriod" @change="updateRevenueChart">
              <el-radio-button label="6m">近6月</el-radio-button>
              <el-radio-button label="1y">近1年</el-radio-button>
            </el-radio-group>
          </div>
          <div class="card-body">
            <div ref="revenueChartRef" class="chart-container"></div>
          </div>
        </div>
      </div>

      <!-- 第三行图表 -->
      <div class="chart-row">
        <div class="content-card">
          <div class="card-header">
            <h3>用户活跃度分析</h3>
          </div>
          <div class="card-body">
            <div ref="userActivityChartRef" class="chart-container"></div>
          </div>
        </div>
        
        <div class="content-card">
          <div class="card-header">
            <h3>景点类型分布</h3>
          </div>
          <div class="card-body">
            <div ref="attractionTypeChartRef" class="chart-container"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="data-tables">
      <div class="content-card">
        <div class="card-header">
          <h3>热门景点排行</h3>
        </div>
        <div class="card-body">
          <el-table :data="topAttractions" style="width: 100%">
            <el-table-column prop="rank" label="排名" width="80" />
            <el-table-column prop="name" label="景点名称" />
            <el-table-column prop="city" label="城市" width="120" />
            <el-table-column prop="type" label="类型" width="120">
              <template #default="{ row }">
                <el-tag :type="getTypeColor(row.type)">
                  {{ getTypeText(row.type) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="rating" label="评分" width="100">
              <template #default="{ row }">
                <el-rate
                  v-model="row.rating"
                  disabled
                  show-score
                  text-color="#ff9900"
                  score-template="{value}"
                />
              </template>
            </el-table-column>
            <el-table-column prop="visitCount" label="访问次数" width="120" />
            <el-table-column prop="revenue" label="收入" width="100">
              <template #default="{ row }">
                ¥{{ row.revenue }}
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <div class="content-card">
        <div class="card-header">
          <h3>用户活跃度排行</h3>
        </div>
        <div class="card-body">
          <el-table :data="topUsers" style="width: 100%">
            <el-table-column prop="rank" label="排名" width="80" />
            <el-table-column label="用户" width="200">
              <template #default="{ row }">
                <div class="user-info">
                  <el-avatar :size="32" style="margin-right: 8px;">
                    <el-icon><User /></el-icon>
                  </el-avatar>
                  <div>
                    <div class="username">{{ row.username }}</div>
                    <div class="user-email">{{ row.email }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="planCount" label="计划数量" width="100" />
            <el-table-column prop="loginDays" label="登录天数" width="100" />
            <el-table-column prop="lastLogin" label="最后登录" width="160" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 'active' ? 'success' : 'danger'">
                  {{ row.status === 'active' ? '活跃' : '非活跃' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import * as echarts from 'echarts'

// 响应式数据
const userGrowthPeriod = ref('30d')
const revenuePeriod = ref('6m')

// 图表引用
const userGrowthChartRef = ref()
const planStatusChartRef = ref()
const destinationRankingChartRef = ref()
const revenueChartRef = ref()
const userActivityChartRef = ref()
const attractionTypeChartRef = ref()

// 概览数据
const overviewData = reactive({
  totalUsers: 12580,
  totalPlans: 3420,
  totalAttractions: 1250,
  totalRevenue: 1256000
})

// 热门景点数据
const topAttractions = ref([
  { rank: 1, name: '故宫博物院', city: '北京', type: 'MUSEUM', rating: 4.8, visitCount: 12580, revenue: 754800 },
  { rank: 2, name: '外滩', city: '上海', type: 'MONUMENT', rating: 4.7, visitCount: 11200, revenue: 0 },
  { rank: 3, name: '西湖', city: '杭州', type: 'PARK', rating: 4.8, visitCount: 10800, revenue: 0 },
  { rank: 4, name: '东方明珠', city: '上海', type: 'BUILDING', rating: 4.5, visitCount: 9600, revenue: 1536000 },
  { rank: 5, name: '天安门广场', city: '北京', type: 'MONUMENT', rating: 4.6, visitCount: 9200, revenue: 0 },
  { rank: 6, name: '迪士尼乐园', city: '上海', type: 'ENTERTAINMENT', rating: 4.6, visitCount: 8800, revenue: 3511200 },
  { rank: 7, name: '雷峰塔', city: '杭州', type: 'MONUMENT', rating: 4.5, visitCount: 7200, revenue: 288000 },
  { rank: 8, name: '灵隐寺', city: '杭州', type: 'RELIGIOUS', rating: 4.6, visitCount: 6800, revenue: 306000 }
])

// 用户活跃度数据
const topUsers = ref([
  { rank: 1, username: 'zhangsan', email: 'zhangsan@example.com', planCount: 15, loginDays: 45, lastLogin: '2024-03-15 14:30', status: 'active' },
  { rank: 2, username: 'lisi', email: 'lisi@example.com', planCount: 12, loginDays: 38, lastLogin: '2024-03-14 16:45', status: 'active' },
  { rank: 3, username: 'wangwu', email: 'wangwu@example.com', planCount: 10, loginDays: 32, lastLogin: '2024-03-13 11:30', status: 'active' },
  { rank: 4, username: 'zhaoliu', email: 'zhaoliu@example.com', planCount: 8, loginDays: 28, lastLogin: '2024-03-12 09:15', status: 'active' },
  { rank: 5, username: 'qianqi', email: 'qianqi@example.com', planCount: 6, loginDays: 25, lastLogin: '2024-03-10 15:20', status: 'active' }
])

// 初始化所有图表
const initCharts = () => {
  initUserGrowthChart()
  initPlanStatusChart()
  initDestinationRankingChart()
  initRevenueChart()
  initUserActivityChart()
  initAttractionTypeChart()
}

// 用户增长趋势图
const initUserGrowthChart = () => {
  const chart = echarts.init(userGrowthChartRef.value)
  const data = getUserGrowthData(userGrowthPeriod.value)
  
  chart.setOption({
    title: {
      text: '用户增长趋势',
      left: 'center',
      textStyle: { fontSize: 14 }
    },
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: data.dates
    },
    yAxis: { type: 'value' },
    series: [{
      data: data.values,
      type: 'line',
      smooth: true,
      areaStyle: { opacity: 0.3 },
      itemStyle: { color: '#1890ff' }
    }]
  })
}

// 旅游计划状态分布图
const initPlanStatusChart = () => {
  const chart = echarts.init(planStatusChartRef.value)
  
  chart.setOption({
    title: {
      text: '计划状态分布',
      left: 'center',
      textStyle: { fontSize: 14 }
    },
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: '60%',
      data: [
        { value: 45, name: '已完成' },
        { value: 30, name: '进行中' },
        { value: 20, name: '计划中' },
        { value: 5, name: '已取消' }
      ],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  })
}

// 热门目的地排行图
const initDestinationRankingChart = () => {
  const chart = echarts.init(destinationRankingChartRef.value)
  
  chart.setOption({
    title: {
      text: '热门目的地排行',
      left: 'center',
      textStyle: { fontSize: 14 }
    },
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'value'
    },
    yAxis: {
      type: 'category',
      data: ['北京', '上海', '杭州', '成都', '西安', '广州', '深圳', '苏州']
    },
    series: [{
      type: 'bar',
      data: [35, 25, 20, 15, 12, 10, 8, 6],
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#83bff6' },
          { offset: 0.5, color: '#188df0' },
          { offset: 1, color: '#188df0' }
        ])
      }
    }]
  })
}

// 月度收入统计图
const initRevenueChart = () => {
  const chart = echarts.init(revenueChartRef.value)
  const data = getRevenueData(revenuePeriod.value)
  
  chart.setOption({
    title: {
      text: '月度收入统计',
      left: 'center',
      textStyle: { fontSize: 14 }
    },
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: data.months
    },
    yAxis: { type: 'value' },
    series: [{
      data: data.values,
      type: 'bar',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#ff9a9e' },
          { offset: 0.5, color: '#fecfef' },
          { offset: 1, color: '#fecfef' }
        ])
      }
    }]
  })
}

// 用户活跃度分析图
const initUserActivityChart = () => {
  const chart = echarts.init(userActivityChartRef.value)
  
  chart.setOption({
    title: {
      text: '用户活跃度分析',
      left: 'center',
      textStyle: { fontSize: 14 }
    },
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00', '24:00']
    },
    yAxis: { type: 'value' },
    series: [{
      data: [120, 200, 150, 80, 70, 110, 90],
      type: 'line',
      smooth: true,
      areaStyle: { opacity: 0.3 },
      itemStyle: { color: '#52c41a' }
    }]
  })
}

// 景点类型分布图
const initAttractionTypeChart = () => {
  const chart = echarts.init(attractionTypeChartRef.value)
  
  chart.setOption({
    title: {
      text: '景点类型分布',
      left: 'center',
      textStyle: { fontSize: 14 }
    },
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: '60%',
      data: [
        { value: 25, name: '博物馆' },
        { value: 20, name: '公园' },
        { value: 18, name: '古迹' },
        { value: 15, name: '建筑' },
        { value: 12, name: '宗教场所' },
        { value: 10, name: '其他' }
      ]
    }]
  })
}

// 获取用户增长数据
const getUserGrowthData = (period) => {
  if (period === '7d') {
    return {
      dates: ['3-9', '3-10', '3-11', '3-12', '3-13', '3-14', '3-15'],
      values: [120, 200, 150, 80, 70, 110, 90]
    }
  } else if (period === '30d') {
    return {
      dates: ['2-15', '2-22', '3-1', '3-8', '3-15'],
      values: [820, 932, 901, 934, 1290]
    }
  } else {
    return {
      dates: ['12-15', '1-15', '2-15', '3-15'],
      values: [620, 720, 820, 920]
    }
  }
}

// 获取收入数据
const getRevenueData = (period) => {
  if (period === '6m') {
    return {
      months: ['10月', '11月', '12月', '1月', '2月', '3月'],
      values: [82000, 93200, 90100, 93400, 129000, 133000]
    }
  } else {
    return {
      months: ['4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月', '1月', '2月', '3月'],
      values: [62000, 72000, 82000, 93200, 90100, 93400, 129000, 133000, 145000, 158000, 162000, 175000]
    }
  }
}

// 更新用户增长图表
const updateUserGrowthChart = () => {
  initUserGrowthChart()
}

// 更新收入图表
const updateRevenueChart = () => {
  initRevenueChart()
}

// 获取类型颜色
const getTypeColor = (type) => {
  const colors = {
    'MUSEUM': 'primary',
    'PARK': 'success',
    'MONUMENT': 'warning',
    'BUILDING': 'info',
    'RELIGIOUS': 'danger',
    'ENTERTAINMENT': 'success'
  }
  return colors[type] || 'info'
}

// 获取类型文本
const getTypeText = (type) => {
  const texts = {
    'MUSEUM': '博物馆',
    'PARK': '公园',
    'MONUMENT': '古迹',
    'BUILDING': '建筑',
    'RELIGIOUS': '宗教场所',
    'ENTERTAINMENT': '娱乐场所'
  }
  return texts[type] || type
}

onMounted(() => {
  setTimeout(() => {
    initCharts()
  }, 100)
})
</script>

<style scoped>
.statistics {
  padding: 0;
}

.stats-overview {
  margin-bottom: 30px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.stat-card {
  background: linear-gradient(135deg, var(--bg-primary), var(--bg-secondary)) !important;
  border: 1px solid var(--border-primary) !important;
  border-radius: 16px !important;
  padding: 24px !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3) !important;
  display: flex !important;
  align-items: center !important;
  gap: 16px !important;
  backdrop-filter: blur(10px) !important;
  position: relative !important;
  overflow: hidden !important;
  transition: all 0.3s ease !important;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--primary-color), var(--secondary-color), var(--accent-color));
  animation: shimmer 3s ease-in-out infinite;
}

.stat-card:hover {
  transform: translateY(-4px) !important;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.4) !important;
  border-color: var(--primary-color) !important;
}

@keyframes shimmer {
  0%, 100% { opacity: 0.5; }
  50% { opacity: 1; }
}

.stat-icon {
  width: 60px !important;
  height: 60px !important;
  border-radius: 12px !important;
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  color: #ffffff !important;
  font-size: 24px !important;
  box-shadow: 0 4px 16px rgba(var(--primary-rgb), 0.3) !important;
  transition: all 0.3s ease !important;
}

.stat-card:hover .stat-icon {
  transform: scale(1.1) !important;
  box-shadow: 0 6px 20px rgba(var(--primary-rgb), 0.5) !important;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 32px !important;
  font-weight: bold !important;
  color: var(--primary-color) !important;
  margin-bottom: 4px !important;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3) !important;
}

.stat-label {
  color: var(--text-secondary) !important;
  font-size: 14px !important;
  margin-bottom: 8px !important;
  font-weight: 500 !important;
}

.stat-trend {
  display: flex !important;
  align-items: center !important;
  gap: 4px !important;
  font-size: 12px !important;
  font-weight: 500 !important;
  padding: 4px 8px !important;
  border-radius: 12px !important;
  background: rgba(var(--success-rgb), 0.1) !important;
  border: 1px solid rgba(var(--success-rgb), 0.3) !important;
}

.stat-trend.positive {
  color: var(--success-color) !important;
  background: rgba(var(--success-rgb), 0.1) !important;
  border: 1px solid rgba(var(--success-rgb), 0.3) !important;
}

.charts-section {
  margin-bottom: 30px;
}

.chart-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.chart-container {
  height: 300px;
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-header h3 {
  margin: 0;
  color: #262626;
  font-size: 16px;
  font-weight: 600;
}

.data-tables {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.user-info {
  display: flex;
  align-items: center;
}

.username {
  font-weight: 500;
  color: #262626;
  margin-bottom: 2px;
}

.user-email {
  font-size: 12px;
  color: #8c8c8c;
}

/* 表格样式覆盖 - 使用深色主题背景 */
.statistics :deep(.el-table) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  border: 1px solid var(--border-primary) !important;
  border-radius: 12px !important;
  overflow: hidden !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5) !important;
}

.statistics :deep(.el-table__header) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
}

.statistics :deep(.el-table__header th) {
  background: transparent !important;
  color: #ffffff !important;
  font-weight: 600 !important;
  text-align: center !important;
  border: none !important;
  padding: 16px 8px !important;
}

.statistics :deep(.el-table__body) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
}

.statistics :deep(.el-table__body td) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  color: #e0e0e0 !important;
  border: none !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1) !important;
  padding: 12px 8px !important;
  text-align: center !important;
}

/* 表格行悬停效果 */
.statistics :deep(.el-table__body tr:hover td) {
  background: linear-gradient(135deg, #2a2a4e, #1e2a4e) !important;
  color: var(--primary-color) !important;
  box-shadow: inset 0 0 20px rgba(var(--primary-rgb), 0.3) !important;
  border-bottom: 1px solid var(--primary-color) !important;
}

/* 表格选中行样式 */
.statistics :deep(.el-table__row.current-row td) {
  background: linear-gradient(135deg, #2a2a4e, #1e2a4e) !important;
  color: var(--primary-color) !important;
  box-shadow: inset 0 0 15px rgba(var(--primary-rgb), 0.4) !important;
  border-bottom: 1px solid var(--primary-color) !important;
}

@media (max-width: 768px) {
  .chart-row {
    grid-template-columns: 1fr;
  }
  
  .data-tables {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>


