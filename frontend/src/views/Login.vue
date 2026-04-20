<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="floating-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
        <div class="shape shape-4"></div>
      </div>
    </div>

    <!-- 登录卡片 -->
    <div class="login-card">
      <div class="login-header">
        <div class="logo">
          <img src="@/tutu/logo.png" alt="睿行视界" class="logo-image" />
          <h1>睿行视界</h1>
        </div>
        <p class="welcome-text">欢迎回来，开始您的智能旅行规划</p>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            :prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item>
          <div class="login-options">
            <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            <el-link type="primary" :underline="false">忘记密码？</el-link>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-button"
            :loading="loading"
            @click="handleLogin"
          >
            <el-icon v-if="!loading"><Right /></el-icon>
            {{ loading ? '登录中...' : '立即登录' }}
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <div class="divider">
          <span>或者</span>
        </div>
        
        <div class="social-login">
          <el-button class="social-button" @click="handleSocialLogin('wechat')">
            <el-icon><ChatDotRound /></el-icon>
            微信登录
          </el-button>
          <el-button class="social-button" @click="handleSocialLogin('qq')">
            <el-icon><Message /></el-icon>
            QQ登录
          </el-button>
        </div>

        <div class="register-link">
          <span>还没有账号？</span>
          <el-link type="primary" :underline="false" @click="handleRegister">
            立即注册
          </el-link>
        </div>
      </div>
    </div>

    <!-- 快速登录提示 -->
    <div class="quick-login-tips">
      <el-alert
        title="快速体验"
        type="info"
        :closable="false"
        show-icon
      >
        <template #default>
          <p>您可以使用以下测试账号快速体验：</p>
          <div class="test-accounts">
            <div class="test-account" @click="quickLogin('admin', 'admin123')">
              <strong>管理员：</strong> admin / admin123
            </div>
            <div class="test-account" @click="quickLogin('alice', 'admin123')">
              <strong>普通用户：</strong> alice / admin123
            </div>
          </div>
        </template>
      </el-alert>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  User, 
  Lock, 
  Right, 
  MagicStick, 
  ChatDotRound, 
  Message 
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

// 表单引用
const loginFormRef = ref()

// 表单数据
const loginForm = reactive({
  username: '',
  password: ''
})

// 其他状态
const loading = ref(false)
const rememberMe = ref(false)

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    const valid = await loginFormRef.value.validate()
    if (!valid) return
    
    loading.value = true
    
    // 调用认证服务
    const result = await authStore.login(loginForm.username, loginForm.password)
    
    if (result.success) {
      ElMessage.success('登录成功！')
      
      // 保存登录状态
      if (rememberMe.value) {
        localStorage.setItem('rememberMe', 'true')
        localStorage.setItem('username', loginForm.username)
      }
      
      // 跳转到首页
      router.push('/')
    } else {
      ElMessage.error(result.message || '登录失败，请检查用户名和密码')
    }
  } catch (error) {
    console.error('登录错误:', error)
    ElMessage.error('登录过程中发生错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 快速登录
const quickLogin = (username, password) => {
  loginForm.username = username
  loginForm.password = password
  handleLogin()
}

// 社交登录
const handleSocialLogin = (platform) => {
  ElMessage.info(`${platform} 登录功能开发中...`)
}

// 注册
const handleRegister = () => {
  ElMessageBox.alert('注册功能开发中，敬请期待！', '提示', {
    confirmButtonText: '确定',
    type: 'info'
  })
}

// 页面加载时检查是否记住用户名
const checkRememberedUser = () => {
  const remembered = localStorage.getItem('rememberMe')
  const savedUsername = localStorage.getItem('username')
  
  if (remembered === 'true' && savedUsername) {
    loginForm.username = savedUsername
    rememberMe.value = true
  }
}

// 组件挂载时检查记住的用户
checkRememberedUser()
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-image: url('@/tutu/reg.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-attachment: fixed;
  position: relative;
  overflow: hidden;
  padding: 20px;
}

.login-container::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.3);
  z-index: 0;
}

.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.floating-shapes {
  position: relative;
  width: 100%;
  height: 100%;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 80px;
  height: 80px;
  top: 20%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 120px;
  height: 120px;
  top: 60%;
  right: 15%;
  animation-delay: 2s;
}

.shape-3 {
  width: 60px;
  height: 60px;
  top: 80%;
  left: 20%;
  animation-delay: 4s;
}

.shape-4 {
  width: 100px;
  height: 100px;
  top: 30%;
  right: 30%;
  animation-delay: 1s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

.login-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 40px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  z-index: 2;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-bottom: 10px;
}

.logo-image {
  width: 40px;
  height: 40px;
  object-fit: contain;
}

.logo h1 {
  font-size: 1.5rem;
  color: #2c3e50;
  margin: 0;
  font-weight: 600;
}

.welcome-text {
  color: #7f8c8d;
  margin: 0;
  font-size: 0.9rem;
}

.login-form {
  margin-bottom: 20px;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.login-button {
  width: 100%;
  height: 50px;
  font-size: 1rem;
  font-weight: 600;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
}

.login-footer {
  text-align: center;
}

.divider {
  position: relative;
  margin: 20px 0;
  color: #7f8c8d;
  font-size: 0.9rem;
}

.divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: #e0e0e0;
}

.divider span {
  background: rgba(255, 255, 255, 0.95);
  padding: 0 15px;
  position: relative;
}

.social-login {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.social-button {
  flex: 1;
  height: 45px;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  background: white;
  color: #666;
  transition: all 0.3s ease;
}

.social-button:hover {
  border-color: #667eea;
  color: #667eea;
  transform: translateY(-1px);
}

.register-link {
  color: #7f8c8d;
  font-size: 0.9rem;
}

.quick-login-tips {
  position: absolute;
  bottom: 20px;
  left: 20px;
  right: 20px;
  max-width: 400px;
  margin: 0 auto;
  z-index: 2;
}

.test-accounts {
  margin-top: 10px;
}

.test-account {
  padding: 8px 12px;
  margin: 5px 0;
  background: rgba(102, 126, 234, 0.1);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.85rem;
}

.test-account:hover {
  background: rgba(102, 126, 234, 0.2);
  transform: translateX(5px);
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-card {
    padding: 30px 20px;
    margin: 10px;
  }
  
  .logo h1 {
    font-size: 1.3rem;
  }
  
  .social-login {
    flex-direction: column;
  }
  
  .quick-login-tips {
    position: relative;
    bottom: auto;
    left: auto;
    right: auto;
    margin-top: 20px;
  }
}

/* 加载动画 */
.login-button.is-loading {
  position: relative;
  overflow: hidden;
}

.login-button.is-loading::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  animation: loading 1.5s infinite;
}

@keyframes loading {
  0% {
    left: -100%;
  }
  100% {
    left: 100%;
  }
}
</style>
