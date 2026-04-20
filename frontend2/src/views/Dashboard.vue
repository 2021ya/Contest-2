<template>
  <div class="dashboard">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">仪表盘</h1>
      <p class="page-description">系统概览和关键指标</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-item">
        <div class="stat-number">{{ stats.totalUsers }}</div>
        <div class="stat-label">总用户数</div>
        <div class="stat-trend">
          <el-icon color="#52c41a"><TrendCharts /></el-icon>
          <span>+12.5%</span>
        </div>
      </div>
      
      <div class="stat-item">
        <div class="stat-number">{{ stats.totalPlans }}</div>
        <div class="stat-label">旅游计划</div>
        <div class="stat-trend">
          <el-icon color="#52c41a"><TrendCharts /></el-icon>
          <span>+8.3%</span>
        </div>
      </div>
      
      <div class="stat-item">
        <div class="stat-number">{{ stats.totalAttractions }}</div>
        <div class="stat-label">景点数量</div>
        <div class="stat-trend">
          <el-icon color="#52c41a"><TrendCharts /></el-icon>
          <span>+5.2%</span>
        </div>
      </div>
      
      <div class="stat-item">
        <div class="stat-number">{{ stats.activeUsers }}</div>
        <div class="stat-label">活跃用户</div>
        <div class="stat-trend">
          <el-icon color="#52c41a"><TrendCharts /></el-icon>
          <span>+15.7%</span>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <div class="chart-row">
        <!-- 用户增长趋势 -->
        <div class="content-card">
          <div class="card-header">用户增长趋势</div>
          <div class="card-body">
            <div ref="userChartRef" class="chart-container"></div>
          </div>
        </div>
        
        <!-- 热门目的地 -->
        <div class="content-card">
          <div class="card-header">热门目的地</div>
          <div class="card-body">
            <div ref="destinationChartRef" class="chart-container"></div>
          </div>
        </div>
      </div>
      
      <div class="chart-row">
        <!-- 旅游计划状态分布 -->
        <div class="content-card">
          <div class="card-header">旅游计划状态分布</div>
          <div class="card-body">
            <div ref="planStatusChartRef" class="chart-container"></div>
          </div>
        </div>
        
        <!-- 月度收入统计 -->
        <div class="content-card">
          <div class="card-header">月度收入统计</div>
          <div class="card-body">
            <div ref="revenueChartRef" class="chart-container"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 最近活动 -->
    <div class="content-card">
      <div class="card-header">最近活动</div>
      <div class="card-body">
        <el-timeline>
          <el-timeline-item
            v-for="activity in recentActivities"
            :key="activity.id"
            :timestamp="activity.time"
            :type="activity.type"
          >
            <div class="activity-content">
              <h4>{{ activity.title }}</h4>
              <p>{{ activity.description }}</p>
            </div>
          </el-timeline-item>
        </el-timeline>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'

const userChartRef = ref()
const destinationChartRef = ref()
const planStatusChartRef = ref()
const revenueChartRef = ref()

// 统计数据
const stats = ref({
  totalUsers: 12580,
  totalPlans: 3420,
  totalAttractions: 1250,
  activeUsers: 8920
})

// 最近活动
const recentActivities = ref([
  {
    id: 1,
    title: '新用户注册',
    description: '用户 "张三" 注册了账号',
    time: '2024-03-15 14:30',
    type: 'primary'
  },
  {
    id: 2,
    title: '旅游计划创建',
    description: '用户 "李四" 创建了北京三日游计划',
    time: '2024-03-15 13:45',
    type: 'success'
  },
  {
    id: 3,
    title: '景点信息更新',
    description: '管理员更新了故宫博物院的开放时间',
    time: '2024-03-15 12:20',
    type: 'warning'
  },
  {
    id: 4,
    title: '系统维护',
    description: '系统进行了例行维护，提升了性能',
    time: '2024-03-15 10:00',
    type: 'info'
  }
])

// 初始化图表
const initCharts = () => {
  // 用户增长趋势图
  const userChart = echarts.init(userChartRef.value)
  userChart.setOption({
    backgroundColor: 'transparent',
    title: {
      text: '近30天用户增长',
      left: 'center',
      textStyle: { 
        fontSize: 14,
        color: '#ffffff',
        textShadowColor: 'rgba(0, 255, 255, 0.3)',
        textShadowBlur: 10
      }
    },
    tooltip: { 
      trigger: 'axis',
      backgroundColor: 'rgba(26, 26, 46, 0.9)',
      borderColor: 'rgba(0, 255, 255, 0.3)',
      textStyle: { color: '#ffffff' }
    },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月'],
      axisLine: { lineStyle: { color: 'rgba(0, 255, 255, 0.3)' } },
      axisLabel: { color: '#a0a0a0' }
    },
    yAxis: { 
      type: 'value',
      axisLine: { lineStyle: { color: 'rgba(0, 255, 255, 0.3)' } },
      axisLabel: { color: '#a0a0a0' },
      splitLine: { lineStyle: { color: 'rgba(0, 255, 255, 0.1)' } }
    },
    series: [{
      data: [120, 200, 150, 80, 70, 110],
      type: 'line',
      smooth: true,
      areaStyle: { 
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(0, 255, 255, 0.3)' },
          { offset: 1, color: 'rgba(0, 255, 255, 0.05)' }
        ])
      },
      lineStyle: { 
        color: '#00ffff',
        width: 3,
        shadowColor: 'rgba(0, 255, 255, 0.5)',
        shadowBlur: 10
      },
      itemStyle: {
        color: '#00ffff',
        shadowColor: 'rgba(0, 255, 255, 0.5)',
        shadowBlur: 10
      }
    }]
  })

  // 热门目的地饼图
  const destinationChart = echarts.init(destinationChartRef.value)
  destinationChart.setOption({
    backgroundColor: 'transparent',
    title: {
      text: '热门目的地分布',
      left: 'center',
      textStyle: { 
        fontSize: 14,
        color: '#ffffff',
        textShadowColor: 'rgba(0, 255, 255, 0.3)',
        textShadowBlur: 10
      }
    },
    tooltip: { 
      trigger: 'item',
      backgroundColor: 'rgba(26, 26, 46, 0.9)',
      borderColor: 'rgba(0, 255, 255, 0.3)',
      textStyle: { color: '#ffffff' }
    },
    series: [{
      type: 'pie',
      radius: '60%',
      data: [
        { value: 35, name: '北京' },
        { value: 25, name: '上海' },
        { value: 20, name: '杭州' },
        { value: 15, name: '成都' },
        { value: 5, name: '其他' }
      ],
      itemStyle: {
        borderRadius: 8,
        borderColor: 'rgba(0, 0, 0, 0.2)',
        borderWidth: 2
      },
      label: {
        color: '#ffffff',
        fontSize: 12
      },
      labelLine: {
        lineStyle: { color: 'rgba(0, 255, 255, 0.3)' }
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 20,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 255, 255, 0.5)'
        }
      }
    }]
  })

  // 旅游计划状态分布
  const planStatusChart = echarts.init(planStatusChartRef.value)
  planStatusChart.setOption({
    backgroundColor: 'transparent',
    title: {
      text: '计划状态分布',
      left: 'center',
      textStyle: { 
        fontSize: 14,
        color: '#ffffff',
        textShadowColor: 'rgba(0, 255, 255, 0.3)',
        textShadowBlur: 10
      }
    },
    tooltip: { 
      trigger: 'item',
      backgroundColor: 'rgba(26, 26, 46, 0.9)',
      borderColor: 'rgba(0, 255, 255, 0.3)',
      textStyle: { color: '#ffffff' }
    },
    series: [{
      type: 'pie',
      radius: '60%',
      data: [
        { value: 45, name: '已完成' },
        { value: 30, name: '进行中' },
        { value: 20, name: '计划中' },
        { value: 5, name: '已取消' }
      ],
      itemStyle: {
        borderRadius: 8,
        borderColor: 'rgba(0, 0, 0, 0.2)',
        borderWidth: 2
      },
      label: {
        color: '#ffffff',
        fontSize: 12
      },
      labelLine: {
        lineStyle: { color: 'rgba(0, 255, 255, 0.3)' }
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 20,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 255, 255, 0.5)'
        }
      }
    }]
  })

  // 月度收入统计
  const revenueChart = echarts.init(revenueChartRef.value)
  revenueChart.setOption({
    backgroundColor: 'transparent',
    title: {
      text: '月度收入趋势',
      left: 'center',
      textStyle: { 
        fontSize: 14,
        color: '#ffffff',
        textShadowColor: 'rgba(0, 255, 255, 0.3)',
        textShadowBlur: 10
      }
    },
    tooltip: { 
      trigger: 'axis',
      backgroundColor: 'rgba(26, 26, 46, 0.9)',
      borderColor: 'rgba(0, 255, 255, 0.3)',
      textStyle: { color: '#ffffff' }
    },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月'],
      axisLine: { lineStyle: { color: 'rgba(0, 255, 255, 0.3)' } },
      axisLabel: { color: '#a0a0a0' }
    },
    yAxis: { 
      type: 'value',
      axisLine: { lineStyle: { color: 'rgba(0, 255, 255, 0.3)' } },
      axisLabel: { color: '#a0a0a0' },
      splitLine: { lineStyle: { color: 'rgba(0, 255, 255, 0.1)' } }
    },
    series: [{
      data: [820, 932, 901, 934, 1290, 1330],
      type: 'bar',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#00ffff' },
          { offset: 0.5, color: '#00bfff' },
          { offset: 1, color: '#0080ff' }
        ]),
        borderRadius: [4, 4, 0, 0],
        shadowColor: 'rgba(0, 255, 255, 0.3)',
        shadowBlur: 10
      },
      emphasis: {
        itemStyle: {
          shadowColor: 'rgba(0, 255, 255, 0.5)',
          shadowBlur: 20
        }
      }
    }]
  })
}

onMounted(() => {
  setTimeout(() => {
    initCharts()
  }, 100)
})
</script>

<style scoped>
.dashboard {
  padding: 0;
  animation: fadeInUp 0.6s ease-out;
}

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

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-item {
  background: linear-gradient(135deg, rgba(26, 26, 46, 0.8) 0%, rgba(22, 33, 62, 0.9) 100%);
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 255, 255, 0.1), inset 0 1px 0 rgba(255, 255, 255, 0.1);
  text-align: center;
  position: relative;
  animation: slideInUp 0.6s ease-out;
  animation-fill-mode: both;
  border: 1px solid rgba(0, 255, 255, 0.3);
  backdrop-filter: blur(10px);
}

.stat-item:nth-child(1) { animation-delay: 0.1s; }
.stat-item:nth-child(2) { animation-delay: 0.2s; }
.stat-item:nth-child(3) { animation-delay: 0.3s; }
.stat-item:nth-child(4) { animation-delay: 0.4s; }

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.stat-number {
  font-size: 36px;
  font-weight: bold;
  background: linear-gradient(135deg, #00ffff 0%, #00bfff 50%, #ff6b35 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 8px;
  text-shadow: 0 0 20px rgba(0, 255, 255, 0.3);
  animation: numberPulse 2s ease-in-out infinite alternate;
}

.stat-label {
  color: #a0a0a0;
  font-size: 14px;
  margin-bottom: 12px;
  text-shadow: 0 0 5px rgba(160, 160, 160, 0.3);
}

.stat-trend {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  color: #00ff88;
  background: linear-gradient(135deg, rgba(0, 255, 136, 0.2) 0%, rgba(0, 255, 136, 0.1) 100%);
  padding: 6px 12px;
  border-radius: 20px;
  border: 1px solid rgba(0, 255, 136, 0.4);
  position: relative;
  z-index: 2;
  box-shadow: 0 0 10px rgba(0, 255, 136, 0.2);
  animation: trendGlow 2s ease-in-out infinite alternate;
}

@keyframes trendGlow {
  0% { box-shadow: 0 0 10px rgba(0, 255, 136, 0.2); }
  100% { box-shadow: 0 0 20px rgba(0, 255, 136, 0.4); }
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

.activity-content h4 {
  margin: 0 0 4px 0;
  font-size: 14px;
  color: #262626;
}

.activity-content p {
  margin: 0;
  font-size: 12px;
  color: #8c8c8c;
}

@media (max-width: 768px) {
  .chart-row {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>


