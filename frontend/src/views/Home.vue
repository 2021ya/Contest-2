<template>
  <div class="home">
    <!-- 主标题区域 -->
    <div class="hero-section">
      <div class="container">
        <div class="hero-content">
          <h1 class="hero-title">睿行视界</h1>
          <p class="hero-subtitle">让AI为您规划完美的旅行路线，探索世界的美好</p>
          <div class="hero-actions">
            <el-button type="primary" size="large" @click="$router.push('/planner')">
              <el-icon><MagicStick /></el-icon>
              开始AI规划
            </el-button>
            <el-button size="large" @click="$router.push('/china-map')">
              <el-icon><Location /></el-icon>
              中国地图
            </el-button>
          </div>
          <div class="scroll-indicator">
            <span>向下滚动开始探索</span>
            <div class="scroll-arrow">↓</div>
          </div>
        </div>
      </div>
    </div>

    <div class="features-section">
      <div class="container">
        <h2 class="section-title">核心功能</h2>
        <div class="features-grid">
          <div class="feature-card" @click="navigateToPlanner">
            <div class="feature-icon">
              <el-icon><MagicStick /></el-icon>
            </div>
            <h3>AI智能规划</h3>
            <p>基于您的偏好和需求，AI自动生成个性化的旅游路线</p>
            <div class="feature-action">
              <el-button type="primary" size="small">开始规划</el-button>
            </div>
          </div>
          
          <div class="feature-card" @click="navigateToChinaMap">
            <div class="feature-icon">
              <el-icon><MapLocation /></el-icon>
            </div>
            <h3>中国地图</h3>
            <p>交互式中国地图，点击任意省份查看景点详情</p>
            <div class="feature-action">
              <el-button type="primary" size="small">探索中国</el-button>
            </div>
          </div>
          
          <div class="feature-card" @click="navigateToAttractions">
            <div class="feature-icon">
              <el-icon><Star /></el-icon>
            </div>
            <h3>景点推荐</h3>
            <p>智能推荐热门景点，包含评分、开放时间等详细信息</p>
            <div class="feature-action">
              <el-button type="primary" size="small">浏览景点</el-button>
            </div>
          </div>
          
          <div class="feature-card" @click="navigateToPlans">
            <div class="feature-icon">
              <el-icon><Document /></el-icon>
            </div>
            <h3>行程管理</h3>
            <p>保存和管理您的旅游计划，随时查看和修改</p>
            <div class="feature-action">
              <el-button type="primary" size="small">管理计划</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>


    <!-- 滚动旅游景点图片区域 -->
    <div class="attractions-scroll-section">
      <div class="container">
        <h2 class="section-title">探索中国美景</h2>
        <div class="attractions-scroll-container">
          <div class="attractions-scroll-track">
            <div class="attraction-card">
              <img src="@/tutu/兵马俑.png" alt="兵马俑" />
              <div class="attraction-overlay">
                <h3>兵马俑</h3>
                <p>西安·陕西</p>
              </div>
            </div>
            <div class="attraction-card">
              <img src="@/tutu/外滩.png" alt="外滩" />
              <div class="attraction-overlay">
                <h3>外滩</h3>
                <p>上海</p>
              </div>
            </div>
            <div class="attraction-card">
              <img src="@/tutu/天安门广场.png" alt="天安门广场" />
              <div class="attraction-overlay">
                <h3>天安门广场</h3>
                <p>北京</p>
              </div>
            </div>
            <div class="attraction-card">
              <img src="@/tutu/生成故宫图片.jpg" alt="故宫" />
              <div class="attraction-overlay">
                <h3>故宫</h3>
                <p>北京</p>
              </div>
            </div>
            <div class="attraction-card">
              <img src="@/tutu/生成泰山图片(1).png" alt="泰山" />
              <div class="attraction-overlay">
                <h3>泰山</h3>
                <p>泰安·山东</p>
              </div>
            </div>
            <div class="attraction-card">
              <img src="@/tutu/西湖.png" alt="西湖" />
              <div class="attraction-overlay">
                <h3>西湖</h3>
                <p>杭州·浙江</p>
              </div>
            </div>
            <!-- 重复一遍以实现无缝滚动 -->
            <div class="attraction-card">
              <img src="@/tutu/兵马俑.png" alt="兵马俑" />
              <div class="attraction-overlay">
                <h3>兵马俑</h3>
                <p>西安·陕西</p>
              </div>
            </div>
            <div class="attraction-card">
              <img src="@/tutu/外滩.png" alt="外滩" />
              <div class="attraction-overlay">
                <h3>外滩</h3>
                <p>上海</p>
              </div>
            </div>
            <div class="attraction-card">
              <img src="@/tutu/天安门广场.png" alt="天安门广场" />
              <div class="attraction-overlay">
                <h3>天安门广场</h3>
                <p>北京</p>
              </div>
            </div>
            <div class="attraction-card">
              <img src="@/tutu/生成故宫图片.jpg" alt="故宫" />
              <div class="attraction-overlay">
                <h3>故宫</h3>
                <p>北京</p>
              </div>
            </div>
            <div class="attraction-card">
              <img src="@/tutu/生成泰山图片(1).png" alt="泰山" />
              <div class="attraction-overlay">
                <h3>泰山</h3>
                <p>泰安·山东</p>
              </div>
            </div>
            <div class="attraction-card">
              <img src="@/tutu/西湖.png" alt="西湖" />
              <div class="attraction-overlay">
                <h3>西湖</h3>
                <p>杭州·浙江</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 文旅新闻板块 -->
    <div class="travel-news-section" ref="travelNewsSection">
      <div class="container">
        <h2 class="section-title">文旅新闻</h2>
        <div class="news-grid">
          <div class="news-card">
            <div class="news-image-wrapper">
              <img src="@/tutu/777.png" alt="文旅新闻" class="news-image" />
              <div class="news-date">2024-06-15</div>
            </div>
            <div class="news-content">
              <h3 class="news-title">文化和旅游部关于印发《国内旅游提升计划》通知</h3>
              <p class="news-excerpt">文化和旅游部关于印发《国内旅游提升计划》通知结合实际认真落实...</p>
              <a href="#" class="news-link">阅读更多 <span class="arrow">→</span></a>
            </div>
          </div>
          
          <div class="news-card">
            <div class="news-image-wrapper">
              <img src="@/tutu/生成故宫图片.jpg" alt="文旅新闻" class="news-image" />
              <div class="news-date">2024-06-10</div>
            </div>
            <div class="news-content">
              <h3 class="news-title">故宫推出夏季特别展览</h3>
              <p class="news-excerpt">故宫博物院将于夏季推出"清代宫廷生活"特别展览，展出多件珍贵文物...</p>
              <a href="#" class="news-link">阅读更多 <span class="arrow">→</span></a>
            </div>
          </div>
          
          <div class="news-card">
            <div class="news-image-wrapper">
              <img src="@/tutu/丝绸之路.jpg" alt="文旅新闻" class="news-image" />
              <div class="news-date">2024-06-05</div>
            </div>
            <div class="news-content">
              <h3 class="news-title">丝绸之路旅游线路全新升级</h3>
              <p class="news-excerpt">全新升级的丝绸之路旅游线路将带您领略沿途的历史文化和自然风光...</p>
              <a href="#" class="news-link">阅读更多 <span class="arrow">→</span></a>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 触发区域 -->
    <div class="trigger-section" ref="triggerSection">
      <div class="trigger-content">
        <h2>准备开始您的旅程</h2>
        <p>向下滚动进入中国地图页面</p>
        <div class="loading-bar" :class="{ active: isTransitioning }">
          <div class="loading-progress"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { MagicStick, Location, MapLocation, Star, Document } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const triggerSection = ref(null)
const isTransitioning = ref(false)
const animatingCard = ref(null)
const travelNewsSection = ref(null)
const hasAnimated = ref(false)

// 滚动监听函数
const handleScroll = () => {
  if (!triggerSection.value) return
  
  const rect = triggerSection.value.getBoundingClientRect()
  const windowHeight = window.innerHeight
  
  // 当触发区域进入视口时开始过渡
  if (rect.top <= windowHeight * 0.5) {
    if (!isTransitioning.value) {
      isTransitioning.value = true
      
      // 延迟跳转，让用户看到过渡效果
      setTimeout(() => {
        router.push('/china-map')
      }, 1500)
    }
  }
  
  // 检测旅游新闻板块是否进入视口
  if (travelNewsSection.value && !hasAnimated.value) {
    const newsRect = travelNewsSection.value.getBoundingClientRect()
    if (newsRect.top <= windowHeight * 0.75) {
      hasAnimated.value = true
      triggerNewsAnimation()
    }
  }
}

// 触发新闻卡片动画
const triggerNewsAnimation = () => {
  const newsCards = travelNewsSection.value.querySelectorAll('.news-card')
  
  // 计算动画总时长为3秒，三个卡片依次触发
  const totalDuration = 3000
  const delayBetweenCards = totalDuration / 3
  
  newsCards.forEach((card, index) => {
    setTimeout(() => {
      card.style.animationPlayState = 'running'
    }, index * delayBetweenCards)
  })
}

// 带动画的导航方法
const navigateWithAnimation = (route, event) => {
  // 如果已经有卡片在动画中，不执行新的动画
  if (animatingCard.value) return
  
  // 获取被点击的卡片元素
  const card = event.currentTarget
  animatingCard.value = card
  
  // 添加动画类
  card.classList.add('card-animation')
  
  // 等待动画完成后再跳转
  setTimeout(() => {
    router.push(route)
    animatingCard.value = null
  }, 600)
}

// 导航方法
const navigateToPlanner = (event) => {
  navigateWithAnimation('/planner', event)
}

const navigateToMap = () => {
  router.push('/map')
}

const navigateToChinaMap = (event) => {
  navigateWithAnimation('/china-map', event)
}

const navigateToAttractions = (event) => {
  // 跳转到景点推荐页面
  navigateWithAnimation('/attractions', event)
}

const navigateToPlans = (event) => {
  navigateWithAnimation('/plans', event)
}

// 生命周期钩子
onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.home {
  min-height: 300vh; /* 确保有足够的滚动空间 */
}

.hero-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-image: url('@/tutu/login1.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  color: white;
  padding: 80px 0;
  text-align: center;
  border-top-left-radius: 20px;
  border-top-right-radius: 20px;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: bold;
  margin-bottom: 20px;
  line-height: 1.2;
  background: linear-gradient(90deg, rgba(0,0,0,0), #000 51.33%, rgba(0,0,0,0));
  background-size: 200% 100%;
  /* 文字裁剪效果 */
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  /* 动画效果 */
  animation: titleFlow 6s ease-in-out infinite;
}

.hero-subtitle {
  font-size: 1.3rem;
  margin-bottom: 40px;
  opacity: 0.9;
  background: linear-gradient(90deg, rgba(0,0,0,0), #000 51.33%, rgba(0,0,0,0));
  background-size: 200% 100%;
  /* 文字裁剪效果 */
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  /* 动画效果 */
  animation: titleFlow 6s ease-in-out infinite;
}

.hero-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
  flex-wrap: wrap;
  margin-bottom: 3rem;
}

.scroll-indicator {
  position: absolute;
  bottom: 2rem;
  left: 50%;
  transform: translateX(-50%);
  text-align: center;
  animation: bounce 2s infinite;
}

.scroll-indicator span {
  display: block;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
  opacity: 0.9;
}

.scroll-arrow {
  font-size: 2rem;
}

.features-section {
  padding: 80px 0;
  background: white;
}

.section-title {
  text-align: center;
  font-size: 2.5rem;
  margin-bottom: 60px;
  color: #2c3e50;
}

/* 招商银行tool_tab风格的features-grid */
.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 0;
  border: 1px solid #e6e6e6;
  border-radius: 8px;
  overflow: hidden;
  background-color: #ffffff;
}

/* 招商银行tool_tab风格的卡片 */
.feature-card {
  text-align: center;
  padding: 30px 15px;
  background: #f8f9fa;
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  border-right: 1px solid #e6e6e6;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 160px;
}

/* 移除最后一列的右边框 */
.features-grid > .feature-card:nth-child(4n) {
  border-right: none;
}

/* 移除最后一行的下边框 */
.features-grid > .feature-card:nth-last-child(-n+4) {
  border-bottom: none;
}

/* 鼠标悬停效果 - 切换为图片背景 */
.feature-card:hover {
  transform: translateY(0);
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  color: white;
  z-index: 10;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

/* 为每个卡片设置不同的悬停图片 */
  .feature-card:nth-child(1):hover {
    background-image: url('@/tutu/1.png');
  }

  .feature-card:nth-child(2):hover {
    background-image: url('@/tutu/2.png');
  }

  .feature-card:nth-child(3):hover {
    background-image: url('@/tutu/3.png');
  }

  .feature-card:nth-child(4):hover {
    background-image: url('@/tutu/4.png');
  }

  /* 悬浮时图标消失 */
  .feature-card:hover .feature-icon {
    opacity: 0;
    transition: opacity 0.3s ease;
  }

.feature-card:active {
  transform: translateY(-2px);
}

/* 高级卡片动画效果 */
.feature-card.card-animation {
  animation: cardJumpOut 0.6s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;
}

@keyframes cardJumpOut {
  0% {
    transform: translateY(-5px) scale(1);
    opacity: 1;
    z-index: 10;
  }
  50% {
    transform: translateY(-20px) scale(1.05);
    opacity: 0.9;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
  }
  100% {
    transform: translateY(-50px) scale(1.1);
    opacity: 0;
  }
}

/* 页面加载动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 为所有页面添加淡入动画 */
.page-transition-enter-active,
.page-transition-leave-active {
  transition: opacity 0.5s ease;
}

.page-transition-enter-from,
.page-transition-leave-to {
  opacity: 0;
}

.feature-icon {
    font-size: 3rem;
    color: #667eea;
    margin-bottom: 20px;
  }

.feature-card h3 {
  font-size: 1.5rem;
  margin-bottom: 15px;
  color: #2c3e50;
}

.feature-card p {
  color: #333333;
  line-height: 1.6;
  margin-bottom: 20px;
}

.feature-action {
  margin-top: 20px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.feature-card:hover .feature-action {
  opacity: 1;
}


/* 滚动旅游景点图片区域样式 */
.attractions-scroll-section {
  background: rgba(0, 0, 0, 0.8);
  padding: 80px 0;
  position: relative;
  overflow: hidden;
}

.attractions-scroll-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('@/tutu/login1.jpg');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  opacity: 0.3;
  z-index: 0;
}

.attractions-scroll-section .container {
  position: relative;
  z-index: 1;
}

.attractions-scroll-section .section-title {
  text-align: center;
  font-size: 2.5rem;
  margin-bottom: 60px;
  color: white;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  background: linear-gradient(90deg, rgba(255,255,255,0), #fff 51.33%, rgba(255,255,255,0));
  background-size: 200% 100%;
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: titleFlow 6s ease-in-out infinite;
}

.attractions-scroll-container {
  overflow: hidden;
  position: relative;
  margin: 0 -150px; /* 进一步增加两侧边距 */
}

.attractions-scroll-track {
  display: flex;
  gap: 50px; /* 增加图片间隔 */
  animation: scrollAttractions 30s linear infinite;
  width: calc(200% + 50px); /* 两倍宽度 + gap */
  padding: 0 50px; /* 添加内边距让图片更靠两侧 */
}

.attraction-card {
  position: relative;
  width: 300px;
  height: 400px;
  border-radius: 20px;
  overflow: hidden;
  flex-shrink: 0;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.attraction-card:hover {
  transform: translateY(-10px) scale(1.05);
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.5);
}

.attraction-card img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.attraction-card:hover img {
  transform: scale(1.1);
}

.attraction-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
  color: white;
  padding: 30px 20px 20px;
  transform: translateY(100%);
  transition: transform 0.3s ease;
}

.attraction-card:hover .attraction-overlay {
  transform: translateY(0);
}

.attraction-overlay h3 {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 8px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
}

.attraction-overlay p {
  font-size: 1rem;
  opacity: 0.9;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
}

@keyframes scrollAttractions {
  0% {
    transform: translateX(0);
  }
  100% {
    transform: translateX(calc(-50% - 15px)); /* 移动一半距离 */
  }
}

.trigger-section {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: white;
  position: relative;
  background-image: url('@/tutu/login1.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-attachment: fixed;
}

.trigger-content h2 {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.trigger-content p {
  font-size: 1.3rem;
  margin-bottom: 2rem;
  opacity: 0.9;
}

.loading-bar {
  width: 300px;
  height: 6px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 3px;
  overflow: hidden;
  margin: 0 auto;
}

.loading-progress {
  height: 100%;
  background: linear-gradient(90deg, #ff6b6b, #4ecdc4);
  width: 0%;
  transition: width 1.5s ease;
}

.loading-bar.active .loading-progress {
  width: 100%;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateX(-50%) translateY(0);
  }
  40% {
    transform: translateX(-50%) translateY(-10px);
  }
  60% {
    transform: translateX(-50%) translateY(-5px);
  }
}

@keyframes titleFlow {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}

/* 文旅新闻板块样式 */
.travel-news-section {
  background: rgba(0, 0, 0, 0.8);
  padding: 100px 0 40px 0;
  position: relative;
  overflow: hidden;
}

.travel-news-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('@/tutu/login1.jpg');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  opacity: 0.3;
  z-index: 0;
}

.travel-news-section .container {
  position: relative;
  z-index: 1;
  max-width: 1440px;
  margin: 0 auto;
  padding: 0 20px;
}

.travel-news-section .section-title {
  text-align: center;
  font-size: 3rem;
  margin-bottom: 80px;
  color: white;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  background: linear-gradient(90deg, rgba(255,255,255,0), #fff 51.33%, rgba(255,255,255,0));
  background-size: 200% 100%;
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: titleFlow 6s ease-in-out infinite;
  font-weight: 700;
  letter-spacing: 1px;
}

.news-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  grid-template-rows: 1fr 1fr;
  gap: 30px;
  margin-bottom: 60px;
  height: 600px;
}

.news-card:nth-child(1) {
  grid-column: 1;
  grid-row: 1 / span 2;
}

.news-card:nth-child(2) {
  grid-column: 2;
  grid-row: 1;
}

.news-card:nth-child(3) {
  grid-column: 2;
  grid-row: 2;
}

.news-card {
    background: rgba(255, 255, 255, 0.1);
    border-radius: 15px;
    overflow: hidden;
    backdrop-filter: blur(10px);
    transition: all 0.5s cubic-bezier(0.25, 0.8, 0.25, 1);
    opacity: 0;
    position: relative;
    border: 1px solid rgba(255, 255, 255, 0.05);
    height: 100%;
    display: flex;
    flex-direction: column;
  }

  .news-card:nth-child(1) {
    animation: slideInFromLeft 1s ease forwards;
    animation-delay: 0.2s;
    animation-play-state: paused;
  }

  .news-card:nth-child(2) {
    animation: slideInFromRight 1s ease forwards;
    animation-delay: 0.4s;
    animation-play-state: paused;
  }

  .news-card:nth-child(3) {
    animation: slideInFromRight 1s ease forwards;
    animation-delay: 0.6s;
    animation-play-state: paused;
  }

.news-card:hover {
  transform: translateY(-15px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.4);
  border-color: rgba(255, 255, 255, 0.15);
}

.news-image-wrapper {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
  z-index: 0;
}

.news-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.7s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.news-card:hover .news-image {
  transform: scale(1.15);
}

.news-date {
  position: absolute;
  top: 20px;
  right: 20px;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 8px 12px;
  border-radius: 20px;
  font-size: 0.85rem;
  backdrop-filter: blur(5px);
  transition: all 0.3s ease;
  z-index: 2;
}

.news-card:hover .news-date {
  background: rgba(102, 126, 234, 0.9);
}

.news-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 25px;
  color: white;
  background: linear-gradient(to top, rgba(0,0,0,0.8) 0%, rgba(0,0,0,0.4) 60%, rgba(0,0,0,0) 100%);
  z-index: 1;
  transition: all 0.3s ease;
}

.news-title {
  font-size: 1.4rem;
  margin-bottom: 20px;
  transition: color 0.3s ease;
  line-height: 1.4;
  font-weight: 600;
}

.news-card:hover .news-title {
  color: #667eea;
}

.news-excerpt {
  font-size: 1rem;
  line-height: 1.7;
  margin-bottom: 25px;
  opacity: 0.85;
}

.news-link {
  display: inline-flex;
  align-items: center;
  color: #667eea;
  font-weight: bold;
  text-decoration: none;
  transition: all 0.3s ease;
  font-size: 1rem;
}

.news-link:hover {
  color: #764ba2;
}

.news-link:hover .arrow {
  transform: translateX(8px);
}

.arrow {
  margin-left: 8px;
  transition: transform 0.3s ease;
  display: inline-block;
}

.news-action {
  text-align: center;
  margin-top: 40px;
}

/* 新闻卡片淡入动画 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(50px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 左侧元素从左侧滑入动画 */
@keyframes slideInFromLeft {
  from {
    opacity: 0;
    transform: translateX(-100px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 右侧元素从右侧滑入动画 */
@keyframes slideInFromRight {
  from {
    opacity: 0;
    transform: translateX(100px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* w1440响应式布局优化 */
@media (max-width: 1440px) {
  .travel-news-section .container {
    padding: 0 40px;
  }
  
  .news-grid {
    gap: 25px;
    height: 550px;
  }
  
  .travel-news-section .section-title {
    font-size: 2.8rem;
    margin-bottom: 70px;
  }
}

@media (max-width: 1200px) {
  .news-grid {
    grid-template-columns: 1fr 1fr;
    grid-template-rows: auto auto;
    height: 800px;
  }
  
  .news-card:nth-child(1) {
    grid-column: 1;
    grid-row: 1 / span 2;
    animation: slideInFromLeft 1s ease forwards;
    animation-delay: 0.2s;
    animation-play-state: paused;
  }
  
  .news-card:nth-child(2) {
    grid-column: 2;
    grid-row: 1;
    animation: slideInFromRight 1s ease forwards;
    animation-delay: 0.4s;
    animation-play-state: paused;
  }
  
  .news-card:nth-child(3) {
    grid-column: 2;
    grid-row: 2;
    animation: slideInFromRight 1s ease forwards;
    animation-delay: 0.6s;
    animation-play-state: paused;
  }
  
  .news-card:nth-child(1) .news-title {
    font-size: 1.8rem;
  }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 2.5rem;
  }
  
  .hero-subtitle {
    font-size: 1.1rem;
  }
  
  .hero-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  
  .trigger-content h2 {
    font-size: 2rem;
  }
  
  .trigger-content p {
    font-size: 1.1rem;
  }
  
  .loading-bar {
    width: 250px;
  }
  
  /* 滚动景点图片区域移动端样式 */
  .attractions-scroll-section {
    padding: 60px 0;
  }
  
  .attractions-scroll-section .section-title {
    font-size: 2rem;
    margin-bottom: 40px;
  }
  
  .attraction-card {
    width: 250px;
    height: 320px;
  }
  
  .attractions-scroll-container {
    margin: 0 -80px; /* 移动端增加两侧边距 */
  }
  
  .attractions-scroll-track {
    gap: 30px; /* 移动端图片间隔 */
    animation-duration: 25s; /* 移动端稍微快一点 */
    padding: 0 30px; /* 移动端内边距 */
  }
  
  .attraction-overlay h3 {
    font-size: 1.2rem;
  }
  
  .attraction-overlay p {
    font-size: 0.9rem;
  }
  
  /* 文旅新闻板块移动端样式 */
  .travel-news-section {
    padding: 60px 0 30px 0;
  }
  
  .travel-news-section .section-title {
    font-size: 2rem;
    margin-bottom: 40px;
  }
  
  .news-grid {
    grid-template-columns: 1fr;
    grid-template-rows: auto auto auto;
    gap: 20px;
    height: auto;
  }
  
  .news-card {
    height: 400px;
  }
  
  .news-card:nth-child(1),
  .news-card:nth-child(2),
  .news-card:nth-child(3) {
    grid-column: 1;
    /* 在移动端，所有卡片都从上方滑入 */
    animation: fadeInUp 1s ease forwards !important;
    animation-play-state: paused;
  }
  
  .news-card:nth-child(1) {
    grid-row: 1;
    animation-delay: 0.2s;
  }
  
  .news-card:nth-child(2) {
    grid-row: 2;
    animation-delay: 0.4s;
  }
  
  .news-card:nth-child(3) {
    grid-row: 3;
    animation-delay: 0.6s;
  }
  
  .news-card:nth-child(1) .news-title,
  .news-title {
    font-size: 1.3rem;
  }
  
  .news-excerpt {
    font-size: 0.9rem;
    margin-bottom: 20px;
  }
  
  .news-content {
    padding: 20px;
  }
  
  .travel-news-section .container {
    padding: 0 20px;
  }
}
</style>



























