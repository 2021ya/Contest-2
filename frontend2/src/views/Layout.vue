<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <div class="admin-sidebar">
      <div class="sidebar-header">
        <div class="logo-container">
          <img src="@/logo.png" alt="睿行视界" class="logo" />
          <div class="logo-glow"></div>
        </div>
        <h2>睿行视界</h2>
        <p class="subtitle">智能旅游管理</p>
      </div>
      <el-menu
        :default-active="$route.path"
        class="sidebar-menu"
        background-color="#1890ff"
        text-color="#fff"
        active-text-color="#1890ff"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/travel-plans">
          <el-icon><Document /></el-icon>
          <span>旅游计划</span>
        </el-menu-item>
        <el-menu-item index="/attractions">
          <el-icon><Location /></el-icon>
          <span>景点管理</span>
        </el-menu-item>
        <el-menu-item index="/news">
          <el-icon><Document /></el-icon>
          <span>新闻管理</span>
        </el-menu-item>
        <el-menu-item index="/statistics">
          <el-icon><TrendCharts /></el-icon>
          <span>数据统计</span>
        </el-menu-item>
        <el-menu-item @click="openVisualization">
          <el-icon><DataAnalysis /></el-icon>
          <span>睿行视界可视化</span>
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 主内容区 -->
    <div class="admin-main">
      <!-- 顶部导航 -->
      <div class="admin-header">
        <div class="header-left">
          <h3>{{ pageTitle }}</h3>
        </div>
        <div class="header-right">
          <ThemeSwitcher />
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" style="margin-right: 8px;">
                <el-icon><User /></el-icon>
              </el-avatar>
              管理员
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                <el-dropdown-item command="settings">设置</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 页面内容 -->
      <div class="admin-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { DataAnalysis } from '@element-plus/icons-vue'
import ThemeSwitcher from '@/components/ThemeSwitcher.vue'

const route = useRoute()
const router = useRouter()

// 计算页面标题
const pageTitle = computed(() => {
  return route.meta?.title || '仪表盘'
})

// 处理用户操作
const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      ElMessage.info('个人资料功能开发中')
      break
    case 'settings':
      ElMessage.info('设置功能开发中')
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('admin_token')
  ElMessage.success('已退出登录')
  router.push('/login')
}

// 打开可视化页面
const openVisualization = () => {
  // 在新标签页中打开外部链接
  window.open('http://localhost:5173/Visualization.html', 'Visualization.html')
}
</script>

<style>
/* 强制显示侧边栏 */
.admin-layout {
  height: 100vh !important;
  display: flex !important;
}

.admin-sidebar {
  width: 280px !important;
  background: linear-gradient(180deg, #1a1a2e 0%, #0f0f23 100%) !important;
  color: #ffffff !important;
  overflow-y: auto !important;
  position: relative !important;
  box-shadow: 4px 0 20px #00ffff !important;
  border-right: 1px solid rgba(0, 255, 255, 0.2) !important;
  display: block !important;
  flex-shrink: 0 !important;
}

.admin-main {
  flex: 1 !important;
  display: flex !important;
  flex-direction: column !important;
  background-image: url('http://localhost:3002/theme-demo');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.sidebar-header {
  padding: 30px 20px;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0.05) 100%);
  backdrop-filter: blur(10px);
  position: relative;
  overflow: hidden;
}

.sidebar-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, transparent 30%, rgba(255, 255, 255, 0.1) 50%, transparent 70%);
  transform: translateX(-100%);
  transition: transform 0.6s ease;
}

.sidebar-header:hover::before {
  transform: translateX(100%);
}

.logo-container {
  position: relative;
  display: inline-block;
  margin-bottom: 15px;
}

.logo {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
  position: relative;
  z-index: 2;
}

.logo:hover {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 12px 35px rgba(0, 0, 0, 0.3);
}

.logo-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 60px;
  height: 60px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, transparent 70%);
  border-radius: 50%;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.logo-container:hover .logo-glow {
  opacity: 1;
}

.sidebar-header h2 {
  color: #fff;
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 5px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  letter-spacing: 1px;
}

.subtitle {
  color: rgba(255, 255, 255, 0.8);
  font-size: 12px;
  font-weight: 400;
  margin: 0;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.sidebar-menu {
  border: none;
  background: transparent !important;
  padding: 10px 0;
}

.sidebar-menu .el-menu-item {
  height: 55px;
  line-height: 55px;
  margin: 5px 15px;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  background: transparent !important;
  border: 1px solid transparent;
}

.sidebar-menu .el-menu-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0.05) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 12px;
}

.sidebar-menu .el-menu-item:hover {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.15) 0%, rgba(255, 255, 255, 0.08) 100%) !important;
  border-color: rgba(255, 255, 255, 0.2);
  transform: translateX(5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.sidebar-menu .el-menu-item:hover::before {
  opacity: 1;
}

.sidebar-menu .el-menu-item.is-active {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.2) 0%, rgba(255, 255, 255, 0.1) 100%) !important;
  border-color: rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
  transform: translateX(8px);
}

.sidebar-menu .el-menu-item.is-active::after {
  content: '';
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 20px;
  background: linear-gradient(to bottom, #fff, rgba(255, 255, 255, 0.5));
  border-radius: 2px;
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
}

.sidebar-menu .el-menu-item span {
  font-weight: 500;
  font-size: 14px;
  letter-spacing: 0.5px;
  transition: all 0.3s ease;
}

.sidebar-menu .el-menu-item:hover span {
  font-weight: 600;
}

.sidebar-menu .el-menu-item .el-icon {
  font-size: 18px;
  margin-right: 12px;
  transition: all 0.3s ease;
}

.sidebar-menu .el-menu-item:hover .el-icon {
  transform: scale(1.1);
}

.header-left h3 {
  color: #262626;
  font-size: 18px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f5f5;
}

</style>


