<template>
  <div class="news-management">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">新闻管理</h1>
      <p class="page-description">管理旅游相关的新闻资讯和公告</p>
    </div>

    <!-- 搜索表单 -->
    <div class="search-form">
      <el-form :model="searchForm" inline>
        <el-form-item label="新闻标题">
          <el-input
            v-model="searchForm.title"
            placeholder="请输入新闻标题"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select
            v-model="searchForm.category"
            placeholder="请选择分类"
            clearable
            style="width: 150px"
          >
            <el-option label="全部" value="" />
            <el-option label="旅游资讯" value="TRAVEL_NEWS" />
            <el-option label="景点公告" value="ATTRACTION_NOTICE" />
            <el-option label="活动通知" value="ACTIVITY_NOTICE" />
            <el-option label="政策解读" value="POLICY_NEWS" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 150px"
          >
            <el-option label="全部" value="" />
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已发布" value="PUBLISHED" />
            <el-option label="已下线" value="OFFLINE" />
          </el-select>
        </el-form-item>
        <el-form-item label="发布时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        发布新闻
      </el-button>
      <el-button type="danger" :disabled="!selectedNews.length" @click="handleBatchDelete">
        <el-icon><Delete /></el-icon>
        批量删除
      </el-button>
      <el-button type="success" @click="handleExport">
        <el-icon><Download /></el-icon>
        导出数据
      </el-button>
    </div>

    <!-- 新闻表格 -->
    <div class="content-card">
      <div class="card-header">新闻列表</div>
      <div class="card-body">
        <el-table
          :data="filteredNews"
          v-loading="loading"
          @selection-change="handleSelectionChange"
          style="width: 100%"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="新闻标题" width="250" show-overflow-tooltip />
          <el-table-column label="分类" width="120">
            <template #default="{ row }">
              <el-tag :type="getCategoryType(row.category)">
                {{ getCategoryText(row.category) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="author" label="作者" width="100" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="views" label="阅读量" width="100" />
          <el-table-column prop="likes" label="点赞数" width="100" />
          <el-table-column prop="publishTime" label="发布时间" width="160" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="handleView(row)">
                <el-icon><View /></el-icon>
                查看
              </el-button>
              <el-button size="small" @click="handleEdit(row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button size="small" type="danger" @click="handleDelete(row)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            :current-page="currentPage"
            :page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="totalNews"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>

    <!-- 新闻详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="新闻详情"
      width="800px"
    >
      <div v-if="currentNews" class="news-detail">
        <div class="detail-section">
          <h3>基本信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="新闻标题">{{ currentNews.title }}</el-descriptions-item>
            <el-descriptions-item label="分类">
              <el-tag :type="getCategoryType(currentNews.category)">
                {{ getCategoryText(currentNews.category) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="作者">{{ currentNews.author }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(currentNews.status)">
                {{ getStatusText(currentNews.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="阅读量">{{ currentNews.views }}</el-descriptions-item>
            <el-descriptions-item label="点赞数">{{ currentNews.likes }}</el-descriptions-item>
            <el-descriptions-item label="发布时间">{{ currentNews.publishTime }}</el-descriptions-item>
            <el-descriptions-item label="更新时间">{{ currentNews.updatedAt }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="detail-section" v-if="currentNews.summary">
          <h3>摘要</h3>
          <p>{{ currentNews.summary }}</p>
        </div>

        <div class="detail-section" v-if="currentNews.content">
          <h3>正文内容</h3>
          <div class="news-content" v-html="currentNews.content"></div>
        </div>

        <div class="detail-section" v-if="currentNews.tags && currentNews.tags.length">
          <h3>标签</h3>
          <div class="tags-container">
            <el-tag v-for="tag in currentNews.tags" :key="tag" style="margin-right: 8px; margin-bottom: 8px;">
              {{ tag }}
            </el-tag>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 响应式数据
const loading = ref(false)
const detailDialogVisible = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const selectedNews = ref([])
const currentNews = ref(null)

// 搜索表单
const searchForm = reactive({
  title: '',
  category: '',
  status: '',
  dateRange: null
})

// 模拟新闻数据
const news = ref([
  {
    id: 1,
    title: '春季旅游旺季来临，各大景区推出优惠政策',
    summary: '随着春季的到来，全国各大旅游景区纷纷推出优惠政策，为游客提供更好的旅游体验。',
    content: '<p>随着春季的到来，全国各大旅游景区纷纷推出优惠政策，为游客提供更好的旅游体验。据了解，本次优惠政策涵盖了门票、住宿、餐饮等多个方面...</p>',
    category: 'TRAVEL_NEWS',
    author: '张三',
    status: 'PUBLISHED',
    views: 1250,
    likes: 89,
    tags: ['春季旅游', '优惠政策', '景区'],
    publishTime: '2024-03-15 10:30:00',
    updatedAt: '2024-03-15 10:30:00'
  },
  {
    id: 2,
    title: '故宫博物院发布参观须知',
    summary: '故宫博物院发布最新的参观须知，提醒游客注意参观时间和安全事项。',
    content: '<p>故宫博物院发布最新的参观须知，提醒游客注意参观时间和安全事项。请游客提前预约门票，合理安排参观时间...</p>',
    category: 'ATTRACTION_NOTICE',
    author: '李四',
    status: 'PUBLISHED',
    views: 890,
    likes: 45,
    tags: ['故宫', '参观须知', '预约'],
    publishTime: '2024-03-12 14:20:00',
    updatedAt: '2024-03-12 14:20:00'
  },
  {
    id: 3,
    title: '2024年樱花节活动即将开始',
    summary: '一年一度的樱花节活动即将在各大公园拉开帷幕，为游客带来美丽的樱花观赏体验。',
    content: '<p>一年一度的樱花节活动即将在各大公园拉开帷幕，为游客带来美丽的樱花观赏体验。活动期间将举办各种文化表演和互动活动...</p>',
    category: 'ACTIVITY_NOTICE',
    author: '王五',
    status: 'DRAFT',
    views: 0,
    likes: 0,
    tags: ['樱花节', '活动', '春季'],
    publishTime: '',
    updatedAt: '2024-03-20 09:15:00'
  },
  {
    id: 4,
    title: '旅游行业新政策解读',
    summary: '国家旅游局发布新的旅游行业政策，对旅游服务质量提出更高要求。',
    content: '<p>国家旅游局发布新的旅游行业政策，对旅游服务质量提出更高要求。新政策主要涉及旅游安全、服务质量、环境保护等方面...</p>',
    category: 'POLICY_NEWS',
    author: '赵六',
    status: 'PUBLISHED',
    views: 2100,
    likes: 156,
    tags: ['政策解读', '旅游行业', '服务质量'],
    publishTime: '2024-03-08 16:45:00',
    updatedAt: '2024-03-08 16:45:00'
  },
  {
    id: 5,
    title: '西湖景区临时关闭通知',
    summary: '由于天气原因，西湖景区部分区域将临时关闭，请游客合理安排行程。',
    content: '<p>由于天气原因，西湖景区部分区域将临时关闭，请游客合理安排行程。具体开放时间将根据天气情况另行通知...</p>',
    category: 'ATTRACTION_NOTICE',
    author: '孙七',
    status: 'OFFLINE',
    views: 450,
    likes: 23,
    tags: ['西湖', '临时关闭', '天气'],
    publishTime: '2024-03-05 08:00:00',
    updatedAt: '2024-03-05 08:00:00'
  }
])

// 计算属性
const totalNews = computed(() => news.value.length)

// 过滤后的新闻列表
const filteredNews = computed(() => {
  let result = news.value

  if (searchForm.title) {
    result = result.filter(news => 
      news.title.toLowerCase().includes(searchForm.title.toLowerCase())
    )
  }

  if (searchForm.category) {
    result = result.filter(news => news.category === searchForm.category)
  }

  if (searchForm.status) {
    result = result.filter(news => news.status === searchForm.status)
  }

  if (searchForm.dateRange && searchForm.dateRange.length === 2) {
    const [start, end] = searchForm.dateRange
    result = result.filter(news => {
      if (!news.publishTime) return false
      const newsDate = new Date(news.publishTime)
      return newsDate >= start && newsDate <= end
    })
  }

  return result
})

// 方法
const getCategoryType = (category) => {
  const types = {
    'TRAVEL_NEWS': 'primary',
    'ATTRACTION_NOTICE': 'success',
    'ACTIVITY_NOTICE': 'warning',
    'POLICY_NEWS': 'info'
  }
  return types[category] || 'info'
}

const getCategoryText = (category) => {
  const texts = {
    'TRAVEL_NEWS': '旅游资讯',
    'ATTRACTION_NOTICE': '景点公告',
    'ACTIVITY_NOTICE': '活动通知',
    'POLICY_NEWS': '政策解读'
  }
  return texts[category] || category
}

const getStatusType = (status) => {
  const types = {
    'DRAFT': 'info',
    'PUBLISHED': 'success',
    'OFFLINE': 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    'DRAFT': '草稿',
    'PUBLISHED': '已发布',
    'OFFLINE': '已下线'
  }
  return texts[status] || status
}

const handleSearch = () => {
  currentPage.value = 1
}

const handleReset = () => {
  searchForm.title = ''
  searchForm.category = ''
  searchForm.status = ''
  searchForm.dateRange = null
  currentPage.value = 1
}

const handleAdd = () => {
  // 跳转到发布新闻页面
  window.location.href = '/news/publish'
}

const handleView = (row) => {
  currentNews.value = row
  detailDialogVisible.value = true
}

const handleEdit = (row) => {
  ElMessage.info('编辑新闻功能开发中')
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条新闻吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const index = news.value.findIndex(news => news.id === row.id)
    if (index > -1) {
      news.value.splice(index, 1)
      ElMessage.success('删除成功')
    }
  } catch {
    // 用户取消删除
  }
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedNews.value.length} 条新闻吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    selectedNews.value.forEach(news => {
      const index = news.value.findIndex(n => n.id === news.id)
      if (index > -1) {
        news.value.splice(index, 1)
      }
    })
    
    selectedNews.value = []
    ElMessage.success('批量删除成功')
  } catch {
    // 用户取消删除
  }
}

const handleExport = () => {
  ElMessage.info('导出功能开发中')
}

const handleSelectionChange = (selection) => {
  selectedNews.value = selection
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

const handleCurrentChange = (page) => {
  currentPage.value = page
}

onMounted(() => {
  // 初始化数据
})
</script>

<style scoped>
.news-management {
  padding: 0;
}

.search-form {
  background: linear-gradient(135deg, var(--bg-primary), var(--bg-secondary)) !important;
  border: 1px solid var(--border-primary) !important;
  padding: 20px !important;
  border-radius: 12px !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3) !important;
  margin-bottom: 20px !important;
  backdrop-filter: blur(10px) !important;
}

.action-buttons {
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.news-detail {
  max-height: 600px;
  overflow-y: auto;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section h3 {
  margin-bottom: 16px;
  color: #262626;
  font-size: 16px;
  font-weight: 600;
}

.news-content {
  line-height: 1.6;
  color: #333;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
}

/* 表格样式覆盖 - 使用深色主题背景 */
.news-management :deep(.el-table) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  border: 1px solid var(--border-primary) !important;
  border-radius: 12px !important;
  overflow: hidden !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5) !important;
}

.news-management :deep(.el-table__header) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  border-bottom: 2px solid var(--primary-color) !important;
}

.news-management :deep(.el-table__header th) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  color: #ffffff !important;
  font-weight: 600 !important;
  text-align: center !important;
  border: none !important;
  border-right: 1px solid rgba(255, 255, 255, 0.2) !important;
  padding: 16px 8px !important;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3) !important;
}

.news-management :deep(.el-table__header th:last-child) {
  border-right: none !important;
}

/* 确保表头单元格没有白色背景 */
.news-management :deep(.el-table__header-wrapper) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
}

.news-management :deep(.el-table__header-wrapper .el-table__header) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
}

.news-management :deep(.el-table__header-wrapper .el-table__header th) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
}

/* 强制覆盖所有可能的表头白色背景 */
.news-management :deep(.el-table thead) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
}

.news-management :deep(.el-table thead tr) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
}

.news-management :deep(.el-table thead tr th) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  color: #ffffff !important;
}

/* 覆盖Element Plus默认样式 */
.news-management :deep(.el-table__header-wrapper .el-table__header thead tr th) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  color: #ffffff !important;
}

/* 确保表头容器也是深色 */
.news-management :deep(.el-table__header-wrapper) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
}

.news-management :deep(.el-table__header-wrapper .el-table__header) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
}

.news-management :deep(.el-table__body) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
}

.news-management :deep(.el-table__body td) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  color: #e0e0e0 !important;
  border: none !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1) !important;
  padding: 12px 8px !important;
  text-align: center !important;
}

/* 表格行悬停效果 */
.news-management :deep(.el-table__body tr:hover td) {
  background: linear-gradient(135deg, #2a2a4e, #1e2a4e) !important;
  color: var(--primary-color) !important;
  box-shadow: inset 0 0 20px rgba(var(--primary-rgb), 0.3) !important;
  border-bottom: 1px solid var(--primary-color) !important;
}

/* 表格选中行样式 */
.news-management :deep(.el-table__row.current-row td) {
  background: linear-gradient(135deg, #2a2a4e, #1e2a4e) !important;
  color: var(--primary-color) !important;
  box-shadow: inset 0 0 15px rgba(var(--primary-rgb), 0.4) !important;
  border-bottom: 1px solid var(--primary-color) !important;
}

/* 分页样式 - 使用深色主题背景 */
.news-management :deep(.pagination-container) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  border: 1px solid var(--border-primary) !important;
  border-radius: 12px !important;
  padding: 20px !important;
  margin-top: 24px !important;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.4) !important;
}

.news-management :deep(.el-pagination) {
  justify-content: center !important;
}

/* 搜索表单深色主题样式 */
.news-management :deep(.search-form .el-form-item__label) {
  color: #e0e0e0 !important;
  font-weight: 500 !important;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5) !important;
}

.news-management :deep(.search-form .el-input__wrapper) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  border: 1px solid var(--border-primary) !important;
  border-radius: 8px !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3) !important;
  transition: all 0.3s ease !important;
}

.news-management :deep(.search-form .el-input__wrapper:hover) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 4px 12px rgba(var(--primary-rgb), 0.2) !important;
}

.news-management :deep(.search-form .el-input__wrapper.is-focus) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 0 0 2px rgba(var(--primary-rgb), 0.2) !important;
}

.news-management :deep(.search-form .el-input__inner) {
  color: #e0e0e0 !important;
  background: transparent !important;
}

.news-management :deep(.search-form .el-input__inner::placeholder) {
  color: #888888 !important;
}

.news-management :deep(.search-form .el-select .el-input__wrapper) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  border: 1px solid var(--border-primary) !important;
  border-radius: 8px !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3) !important;
  transition: all 0.3s ease !important;
}

.news-management :deep(.search-form .el-select .el-input__wrapper:hover) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 4px 12px rgba(var(--primary-rgb), 0.2) !important;
}

.news-management :deep(.search-form .el-select .el-input__wrapper.is-focus) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 0 0 2px rgba(var(--primary-rgb), 0.2) !important;
}

.news-management :deep(.search-form .el-date-editor) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  border: 1px solid var(--border-primary) !important;
  border-radius: 8px !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3) !important;
  transition: all 0.3s ease !important;
}

.news-management :deep(.search-form .el-date-editor:hover) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 4px 12px rgba(var(--primary-rgb), 0.2) !important;
}

.news-management :deep(.search-form .el-date-editor.is-active) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 0 0 2px rgba(var(--primary-rgb), 0.2) !important;
}

.news-management :deep(.search-form .el-date-editor .el-input__inner) {
  color: #e0e0e0 !important;
  background: transparent !important;
}

.news-management :deep(.search-form .el-date-editor .el-input__inner::placeholder) {
  color: #888888 !important;
}

.news-management :deep(.search-form .el-button) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  border: 1px solid var(--primary-color) !important;
  color: #ffffff !important;
  border-radius: 8px !important;
  box-shadow: 0 4px 12px rgba(var(--primary-rgb), 0.3) !important;
  transition: all 0.3s ease !important;
}

.news-management :deep(.search-form .el-button:hover) {
  background: linear-gradient(135deg, var(--secondary-color), var(--primary-color)) !important;
  box-shadow: 0 6px 16px rgba(var(--primary-rgb), 0.4) !important;
  transform: translateY(-2px) !important;
}

.news-management :deep(.search-form .el-button:active) {
  transform: translateY(0) !important;
  box-shadow: 0 2px 8px rgba(var(--primary-rgb), 0.3) !important;
}

@media (max-width: 768px) {
  .search-form .el-form {
    display: block;
  }
  
  .search-form .el-form-item {
    display: block;
    margin-bottom: 16px;
  }
}
</style>
