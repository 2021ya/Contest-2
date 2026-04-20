<template>
  <div id="app">
    <!-- 登录页面不显示导航栏 -->
    <div v-if="$route.name === 'Login'" class="login-layout">
      <transition name="page-transition">
        <router-view />
      </transition>
    </div>
    
    <!-- 其他页面显示完整布局 -->
    <el-container v-else>
      <el-header class="header">
        <div class="header-content">
          <div class="logo" @click="$router.push('/')">
            <img src="@/tutu/logo.png" alt="睿行视界" class="header-logo" />
            <span>睿行视界</span>
          </div>
          
          <el-menu
            :default-active="$route.path"
            mode="horizontal"
            class="nav-menu"
          >
            <el-menu-item :index="'/'" @click="handleNavClick('/', $event)">
              <el-icon><House /></el-icon>
              <span>首页</span>
            </el-menu-item>
            <el-menu-item :index="'/planner'" @click="handleNavClick('/planner', $event)">
              <el-icon><MagicStick /></el-icon>
              <span>AI规划</span>
            </el-menu-item>
            <el-menu-item :index="'/plans'" @click="handleNavClick('/plans', $event)">
              <el-icon><Document /></el-icon>
              <span>我的计划</span>
            </el-menu-item>
            <el-menu-item :index="'/map'" @click="handleNavClick('/map', $event)">
              <el-icon><Location /></el-icon>
              <span>地图</span>
            </el-menu-item>
            <el-menu-item :index="'/china-map'" @click="handleNavClick('/china-map', $event)">
              <el-icon><MapLocation /></el-icon>
              <span>中国地图</span>
            </el-menu-item>
            <el-menu-item :index="'/attractions'" @click="handleNavClick('/attractions', $event)">
              <el-icon><Star /></el-icon>
              <span>景点推荐</span>
            </el-menu-item>
          </el-menu>
          
          <!-- 用户菜单 -->
          <div class="user-menu">
            <el-dropdown v-if="authStore.isLoggedIn" @command="handleUserCommand">
              <div class="user-info">
                <el-avatar :src="authStore.userInfo?.avatar" :size="32">
                  <el-icon><User /></el-icon>
                </el-avatar>
                <span class="username">{{ authStore.userInfo?.nickname || authStore.userInfo?.username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    个人资料
                  </el-dropdown-item>
                  <el-dropdown-item command="settings">
                    <el-icon><Setting /></el-icon>
                    设置
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            
            <div v-else class="login-buttons">
              <el-button type="primary" @click="$router.push('/login')">
                <el-icon><User /></el-icon>
                登录
              </el-button>
            </div>
          </div>
        </div>
      </el-header>
      
      <el-main class="main-content">
        <transition name="page-transition">
          <router-view />
        </transition>
      </el-main>
      
      <el-footer class="footer">
        <div class="footer-content">
          <p>&copy; 2024 AI智能旅游地图规划系统. All rights reserved.</p>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { MapLocation, House, MagicStick, Document, Location, Star, User, ArrowDown, Setting, SwitchButton } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()

// 导航动画状态
const isNavigating = ref(false)
const currentMenuItem = ref(null)

// 处理导航菜单项点击，添加动画效果
const handleNavClick = (targetRoute, event) => {
  // 阻止默认事件行为
  if (event && event.preventDefault) {
    event.preventDefault()
  }
  
  // 如果已经在导航中或目标路径与当前路径相同，不执行新的导航
  if (isNavigating.value || route.path === targetRoute) return
  
  isNavigating.value = true
  const menuItem = event?.currentTarget || null
  currentMenuItem.value = menuItem
  
  // 添加动画类
  if (menuItem) {
    menuItem.classList.add('menu-item-animation')
  }
  
  // 等待动画完成后再跳转
  setTimeout(() => {
    // 移除动画类，为下次动画做准备
    if (currentMenuItem.value) {
      currentMenuItem.value.classList.remove('menu-item-animation')
    }
    
    // 确保router.push被正确执行
    console.log('Navigating to:', targetRoute)
    router.push(targetRoute)
    
    // 重置状态
    setTimeout(() => {
      isNavigating.value = false
      currentMenuItem.value = null
    }, 500)
  }, 300)
}

// 处理用户菜单命令
const handleUserCommand = (command) => {
  switch (command) {
    case 'profile':
      ElMessage.info('个人资料功能开发中...')
      break
    case 'settings':
      ElMessage.info('设置功能开发中...')
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 处理登出
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    authStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  } catch {
    // 用户取消
  }
}
</script>

<style scoped>
.header {
  background: linear-gradient(135deg, #4a90e2 0%, #7bb3f0 100%);
  color: white;
  padding: 0;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: bold;
  gap: 8px;
}

.header-logo {
  width: 32px;
  height: 32px;
  object-fit: contain;
}

.nav-menu {
  background: transparent;
  border: none;
}

.nav-menu .el-menu-item {
  color: white;
  border-bottom: 2px solid transparent;
}

.nav-menu .el-menu-item:hover,
.nav-menu .el-menu-item.is-active {
  background: rgba(255, 255, 255, 0.1);
  border-bottom-color: white;
}

/* 导航菜单项动画 */
.nav-menu .el-menu-item {
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

/* 菜单项点击动画 */
.nav-menu .el-menu-item.menu-item-animation {
  animation: menuItemClick 0.3s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;
}

@keyframes menuItemClick {
  0% {
    transform: scale(1);
    background: rgba(255, 255, 255, 0.1);
  }
  50% {
    transform: scale(1.05);
    background: rgba(255, 255, 255, 0.2);
  }
  100% {
    transform: scale(1);
    background: rgba(255, 255, 255, 0.1);
  }
}

/* 菜单项下划线动画 */
.nav-menu .el-menu-item::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 2px;
  background: white;
  transition: width 0.3s ease, left 0.3s ease;
}

.nav-menu .el-menu-item:hover::after,
.nav-menu .el-menu-item.is-active::after {
  width: 80%;
  left: 10%;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.1);
}

.username {
  font-size: 14px;
  font-weight: 500;
}

.login-buttons {
  display: flex;
  align-items: center;
  gap: 10px;
}

.login-layout {
  min-height: 100vh;
}

.main-content {
  min-height: calc(100vh - 120px);
  padding: 20px;
  background: #f5f7fa;
  background-image: url('@/tutu/login1.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-attachment: fixed;
}

.footer {
  background: #2c3e50;
  color: white;
  text-align: center;
  padding: 20px 0;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
}

#app {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
}

/* 页面过渡动画样式 */
.page-transition-enter-active {
  transition: opacity 1s ease-in;
}

.page-transition-leave-active {
  transition: opacity 1s ease-out;
}

.page-transition-enter-from {
  opacity: 0;
}

.page-transition-leave-to {
  opacity: 0;
}

/* 确保路由视图在动画期间正确显示 */
.page-transition-enter-active .main-content,
.page-transition-leave-active .main-content {
  overflow: hidden;
}

/* 优化动画性能 */
.router-view {
  will-change: transform, opacity;
}
</style>
