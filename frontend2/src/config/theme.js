// 主题色配置
export const themeConfig = {
  // 默认主题色
  primary: '#00ffcc',
  secondary: '#00ccaa',
  accent: '#ff6b35',
  
  // 背景色
  background: {
    primary: '#0a0e27',
    secondary: '#1a1a2e',
    tertiary: '#16213e'
  },
  
  // 边框和阴影
  border: {
    primary: 'rgba(0, 255, 204, 0.2)',
    secondary: 'rgba(0, 255, 204, 0.3)',
    accent: 'rgba(0, 255, 204, 0.6)'
  },
  
  // 文字颜色
  text: {
    primary: '#ffffff',
    secondary: '#a0a0a0',
    accent: '#00ffcc'
  },
  
  // 预设主题
  presets: {
    cyberpunk: {
      primary: '#00ffcc',
      secondary: '#00ccaa',
      accent: '#ff6b35',
      name: '赛博朋克'
    },
    neon: {
      primary: '#ff00ff',
      secondary: '#00ffff',
      accent: '#ffff00',
      name: '霓虹'
    },
    ocean: {
      primary: '#00bfff',
      secondary: '#0080ff',
      accent: '#00ffff',
      name: '海洋'
    },
    fire: {
      primary: '#ff6b35',
      secondary: '#ff8c42',
      accent: '#ffa500',
      name: '火焰'
    },
    forest: {
      primary: '#00ff88',
      secondary: '#00cc66',
      accent: '#00ffaa',
      name: '森林'
    }
  }
}

// 获取当前主题色
export const getCurrentTheme = () => {
  const saved = localStorage.getItem('admin_theme')
  return saved ? JSON.parse(saved) : themeConfig.presets.cyberpunk
}

// 设置主题色
export const setTheme = (theme) => {
  localStorage.setItem('admin_theme', JSON.stringify(theme))
  applyTheme(theme)
}

// 应用主题色到CSS变量
export const applyTheme = (theme) => {
  const root = document.documentElement
  
  // 设置CSS变量
  root.style.setProperty('--primary-color', theme.primary)
  root.style.setProperty('--secondary-color', theme.secondary)
  root.style.setProperty('--accent-color', theme.accent)
  
  // 设置背景色
  root.style.setProperty('--bg-primary', theme.background?.primary || '#0a0e27')
  root.style.setProperty('--bg-secondary', theme.background?.secondary || '#1a1a2e')
  root.style.setProperty('--bg-tertiary', theme.background?.tertiary || '#16213e')
  
  // 设置边框色
  root.style.setProperty('--border-primary', theme.border?.primary || 'rgba(0, 255, 255, 0.2)')
  root.style.setProperty('--border-secondary', theme.border?.secondary || 'rgba(0, 255, 255, 0.3)')
  root.style.setProperty('--border-accent', theme.border?.accent || 'rgba(0, 255, 255, 0.6)')
  
  // 设置文字色
  root.style.setProperty('--text-primary', theme.text?.primary || '#ffffff')
  root.style.setProperty('--text-secondary', theme.text?.secondary || '#a0a0a0')
  root.style.setProperty('--text-accent', theme.text?.accent || '#00ffff')
}

// 初始化主题
export const initTheme = () => {
  const currentTheme = getCurrentTheme()
  applyTheme(currentTheme)
  return currentTheme
}
