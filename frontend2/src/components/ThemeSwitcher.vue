<template>
  <div class="theme-switcher">
    <el-dropdown @command="handleThemeChange" trigger="click">
      <el-button type="primary" class="theme-button">
        <el-icon><Setting /></el-icon>
        主题色
        <el-icon class="el-icon--right"><ArrowDown /></el-icon>
      </el-button>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item 
            v-for="(theme, key) in themeConfig.presets" 
            :key="key"
            :command="key"
            :class="{ active: currentTheme === key }"
          >
            <div class="theme-option">
              <div class="theme-colors">
                <span 
                  class="color-dot" 
                  :style="{ backgroundColor: theme.primary }"
                ></span>
                <span 
                  class="color-dot" 
                  :style="{ backgroundColor: theme.secondary }"
                ></span>
                <span 
                  class="color-dot" 
                  :style="{ backgroundColor: theme.accent }"
                ></span>
              </div>
              <span class="theme-name">{{ theme.name }}</span>
            </div>
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Setting, ArrowDown } from '@element-plus/icons-vue'
import { themeConfig, getCurrentTheme, setTheme } from '@/config/theme.js'

const currentTheme = ref('cyberpunk')

const handleThemeChange = (themeKey) => {
  const theme = themeConfig.presets[themeKey]
  if (theme) {
    setTheme(theme)
    currentTheme.value = themeKey
  }
}

onMounted(() => {
  const saved = localStorage.getItem('admin_theme')
  if (saved) {
    const theme = JSON.parse(saved)
    // 找到对应的主题键
    for (const [key, preset] of Object.entries(themeConfig.presets)) {
      if (preset.primary === theme.primary && preset.secondary === theme.secondary) {
        currentTheme.value = key
        break
      }
    }
  }
})
</script>

<style scoped>
.theme-switcher {
  position: relative;
}

.theme-button {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  border: 1px solid var(--border-secondary) !important;
  color: #ffffff !important;
  border-radius: 8px !important;
  padding: 8px 16px !important;
  font-weight: 500 !important;
  transition: all 0.3s ease !important;
  box-shadow: 0 4px 12px var(--primary-color) !important;
}

.theme-button:hover {
  transform: translateY(-2px) !important;
  box-shadow: 0 8px 20px var(--primary-color) !important;
  background: linear-gradient(135deg, var(--secondary-color), var(--primary-color)) !important;
}

.theme-option {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.theme-colors {
  display: flex;
  gap: 4px;
}

.color-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 0 4px rgba(0, 0, 0, 0.3);
}

.theme-name {
  font-weight: 500;
  color: var(--text-primary);
}

.el-dropdown-menu__item.active {
  background: var(--primary-color) !important;
  color: #ffffff !important;
}

.el-dropdown-menu__item.active .theme-name {
  color: #ffffff !important;
}

.el-dropdown-menu__item:hover {
  background: var(--border-primary) !important;
}
</style>

