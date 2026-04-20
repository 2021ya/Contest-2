<template>
  <div class="theme-demo">
    <div class="page-header">
      <h1 class="page-title">主题色演示</h1>
      <p class="page-description">展示所有可用的主题色配置</p>
    </div>

    <div class="theme-grid">
      <div 
        v-for="(theme, key) in themeConfig.presets" 
        :key="key"
        class="theme-card"
        :class="{ active: currentTheme === key }"
        @click="applyTheme(key)"
      >
        <div class="theme-preview">
          <div class="theme-colors">
            <div 
              class="color-block primary" 
              :style="{ backgroundColor: theme.primary }"
            ></div>
            <div 
              class="color-block secondary" 
              :style="{ backgroundColor: theme.secondary }"
            ></div>
            <div 
              class="color-block accent" 
              :style="{ backgroundColor: theme.accent }"
            ></div>
          </div>
          <div class="theme-info">
            <h3>{{ theme.name }}</h3>
            <div class="color-codes">
              <div class="color-code">
                <span class="label">主色:</span>
                <span class="value">{{ theme.primary }}</span>
              </div>
              <div class="color-code">
                <span class="label">次色:</span>
                <span class="value">{{ theme.secondary }}</span>
              </div>
              <div class="color-code">
                <span class="label">强调:</span>
                <span class="value">{{ theme.accent }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="theme-actions">
          <el-button 
            type="primary" 
            size="small"
            @click.stop="applyTheme(key)"
          >
            应用主题
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { themeConfig, getCurrentTheme, setTheme } from '@/config/theme.js'

const currentTheme = ref('cyberpunk')

const applyTheme = (themeKey) => {
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
.theme-demo {
  padding: 30px;
}

.theme-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
  margin-top: 30px;
}

.theme-card {
  background: linear-gradient(135deg, rgba(26, 26, 46, 0.8) 0%, rgba(22, 33, 62, 0.9) 100%);
  border-radius: 16px;
  padding: 24px;
  border: 1px solid var(--border-primary);
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.theme-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px var(--primary-color);
  border-color: var(--border-secondary);
}

.theme-card.active {
  border-color: var(--primary-color);
  box-shadow: 0 0 20px var(--primary-color);
}

.theme-preview {
  margin-bottom: 20px;
}

.theme-colors {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.color-block {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  border: 2px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  transition: transform 0.3s ease;
}

.color-block:hover {
  transform: scale(1.1);
}

.theme-info h3 {
  color: var(--text-primary);
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 12px;
}

.color-codes {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.color-code {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
}

.label {
  color: var(--text-secondary);
  font-weight: 500;
}

.value {
  color: var(--text-primary);
  font-family: 'Courier New', monospace;
  background: rgba(0, 0, 0, 0.3);
  padding: 2px 6px;
  border-radius: 4px;
  border: 1px solid var(--border-primary);
}

.theme-actions {
  display: flex;
  justify-content: center;
}

.theme-actions .el-button {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  border: 1px solid var(--border-secondary) !important;
  color: #ffffff !important;
  border-radius: 8px !important;
  font-weight: 500 !important;
  transition: all 0.3s ease !important;
}

.theme-actions .el-button:hover {
  transform: translateY(-2px) !important;
  box-shadow: 0 8px 20px var(--primary-color) !important;
}
</style>

