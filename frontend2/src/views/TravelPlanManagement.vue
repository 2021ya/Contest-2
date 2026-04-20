<template>
  <div class="travel-plan-management">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">旅游计划管理</h1>
      <p class="page-description">管理用户创建的旅游计划和AI生成的推荐</p>
    </div>

    <!-- 搜索表单 -->
    <div class="search-form">
      <el-form :model="searchForm" inline>
        <el-form-item label="计划标题">
          <el-input
            v-model="searchForm.title"
            placeholder="请输入计划标题"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="目的地">
          <el-input
            v-model="searchForm.destination"
            placeholder="请输入目的地"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 150px"
          >
            <el-option label="全部" value="" />
            <el-option label="计划中" value="PLANNING" />
            <el-option label="已确认" value="CONFIRMED" />
            <el-option label="进行中" value="IN_PROGRESS" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </el-form-item>
        <el-form-item label="创建时间">
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
        创建计划
      </el-button>
      <el-button type="danger" :disabled="!selectedPlans.length" @click="handleBatchDelete">
        <el-icon><Delete /></el-icon>
        批量删除
      </el-button>
      <el-button type="success" @click="handleExport">
        <el-icon><Download /></el-icon>
        导出数据
      </el-button>
    </div>

    <!-- 计划表格 -->
    <div class="content-card">
      <div class="card-header">旅游计划列表</div>
      <div class="card-body">
        <el-table
          :data="filteredPlans"
          v-loading="loading"
          @selection-change="handleSelectionChange"
          style="width: 100%"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="计划标题" width="200" show-overflow-tooltip />
          <el-table-column label="目的地" width="120">
            <template #default="{ row }">
              <el-tag>{{ getDestination(row) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="user.username" label="创建者" width="120" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="budget" label="预算" width="100">
            <template #default="{ row }">
              ¥{{ row.budget }}
            </template>
          </el-table-column>
          <el-table-column prop="days" label="天数" width="80" />
          <el-table-column label="AI生成" width="100">
            <template #default="{ row }">
              <el-tag :type="row.aiGenerated ? 'success' : 'info'">
                {{ row.aiGenerated ? '是' : '否' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" width="160" />
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
            :total="totalPlans"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>

    <!-- 计划详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="计划详情"
      width="800px"
    >
      <div v-if="currentPlan" class="plan-detail">
        <div class="detail-section">
          <h3>基本信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="计划标题">{{ currentPlan.title }}</el-descriptions-item>
            <el-descriptions-item label="目的地">{{ getDestination(currentPlan) }}</el-descriptions-item>
            <el-descriptions-item label="预算">¥{{ currentPlan.budget }}</el-descriptions-item>
            <el-descriptions-item label="天数">{{ currentPlan.days }}天</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(currentPlan.status)">
                {{ getStatusText(currentPlan.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="AI生成">
              <el-tag :type="currentPlan.aiGenerated ? 'success' : 'info'">
                {{ currentPlan.aiGenerated ? '是' : '否' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ currentPlan.createdAt }}</el-descriptions-item>
            <el-descriptions-item label="更新时间">{{ currentPlan.updatedAt }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="detail-section" v-if="currentPlan.destinations && currentPlan.destinations.length">
          <h3>目的地信息</h3>
          <el-table :data="currentPlan.destinations" style="width: 100%">
            <el-table-column prop="name" label="目的地名称" />
            <el-table-column prop="city" label="城市" />
            <el-table-column prop="rating" label="评分" />
            <el-table-column prop="visitDuration" label="建议游览时间(分钟)" />
            <el-table-column prop="estimatedCost" label="预估费用" />
          </el-table>
        </div>

        <div class="detail-section" v-if="currentPlan.attractions && currentPlan.attractions.length">
          <h3>景点信息</h3>
          <el-table :data="getAllAttractions(currentPlan)" style="width: 100%">
            <el-table-column prop="name" label="景点名称" />
            <el-table-column prop="type" label="类型" />
            <el-table-column prop="rating" label="评分" />
            <el-table-column prop="visitDuration" label="游览时间(分钟)" />
            <el-table-column prop="ticketPrice" label="门票价格" />
          </el-table>
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
const selectedPlans = ref([])
const currentPlan = ref(null)

// 搜索表单
const searchForm = reactive({
  title: '',
  destination: '',
  status: '',
  dateRange: null
})

// 模拟旅游计划数据
const plans = ref([
  {
    id: 1,
    title: '北京三日游',
    description: '探索北京的历史文化和现代风貌',
    startDate: '2024-03-15T00:00:00',
    endDate: '2024-03-17T23:59:59',
    budget: 3000,
    days: 3,
    status: 'CONFIRMED',
    aiGenerated: true,
    user: { id: 1, username: 'zhangsan' },
    destinations: [
      {
        id: 1,
        name: '北京',
        city: '北京',
        rating: 4.8,
        visitDuration: 480,
        estimatedCost: 1000
      }
    ],
    attractions: [
      {
        id: 1,
        name: '故宫博物院',
        type: 'MUSEUM',
        rating: 4.8,
        visitDuration: 180,
        ticketPrice: 60
      },
      {
        id: 2,
        name: '天安门广场',
        type: 'MONUMENT',
        rating: 4.6,
        visitDuration: 60,
        ticketPrice: 0
      }
    ],
    createdAt: '2024-03-10T10:00:00',
    updatedAt: '2024-03-10T10:00:00'
  },
  {
    id: 2,
    title: '上海周末游',
    description: '体验上海的现代都市魅力',
    startDate: '2024-03-22T00:00:00',
    endDate: '2024-03-24T23:59:59',
    budget: 2500,
    days: 2,
    status: 'PLANNING',
    aiGenerated: true,
    user: { id: 2, username: 'lisi' },
    destinations: [
      {
        id: 2,
        name: '上海',
        city: '上海',
        rating: 4.7,
        visitDuration: 360,
        estimatedCost: 800
      }
    ],
    attractions: [
      {
        id: 3,
        name: '外滩',
        type: 'MONUMENT',
        rating: 4.7,
        visitDuration: 90,
        ticketPrice: 0
      }
    ],
    createdAt: '2024-03-12T14:30:00',
    updatedAt: '2024-03-12T14:30:00'
  },
  {
    id: 3,
    title: '杭州西湖游',
    description: '感受江南水乡的美丽风光',
    startDate: '2024-04-01T00:00:00',
    endDate: '2024-04-03T23:59:59',
    budget: 2000,
    days: 3,
    status: 'IN_PROGRESS',
    aiGenerated: false,
    user: { id: 3, username: 'wangwu' },
    destinations: [
      {
        id: 3,
        name: '杭州',
        city: '杭州',
        rating: 4.9,
        visitDuration: 480,
        estimatedCost: 600
      }
    ],
    attractions: [
      {
        id: 4,
        name: '西湖',
        type: 'PARK',
        rating: 4.8,
        visitDuration: 240,
        ticketPrice: 0
      }
    ],
    createdAt: '2024-03-20T09:15:00',
    updatedAt: '2024-03-20T09:15:00'
  }
])

// 计算属性
const totalPlans = computed(() => plans.value.length)

// 过滤后的计划列表
const filteredPlans = computed(() => {
  let result = plans.value

  if (searchForm.title) {
    result = result.filter(plan => 
      plan.title.toLowerCase().includes(searchForm.title.toLowerCase())
    )
  }

  if (searchForm.destination) {
    result = result.filter(plan => 
      getDestination(plan).toLowerCase().includes(searchForm.destination.toLowerCase())
    )
  }

  if (searchForm.status) {
    result = result.filter(plan => plan.status === searchForm.status)
  }

  if (searchForm.dateRange && searchForm.dateRange.length === 2) {
    const [start, end] = searchForm.dateRange
    result = result.filter(plan => {
      const planDate = new Date(plan.createdAt)
      return planDate >= start && planDate <= end
    })
  }

  return result
})

// 方法
const getDestination = (plan) => {
  if (plan.destinations && plan.destinations.length > 0) {
    return plan.destinations[0].name
  }
  return '未知'
}

const getStatusType = (status) => {
  const types = {
    'PLANNING': 'warning',
    'CONFIRMED': 'success',
    'IN_PROGRESS': 'primary',
    'COMPLETED': 'success',
    'CANCELLED': 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    'PLANNING': '计划中',
    'CONFIRMED': '已确认',
    'IN_PROGRESS': '进行中',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return texts[status] || status
}

const getAllAttractions = (plan) => {
  const attractions = []
  if (plan.destinations) {
    plan.destinations.forEach(dest => {
      if (dest.attractions) {
        attractions.push(...dest.attractions)
      }
    })
  }
  if (plan.attractions) {
    attractions.push(...plan.attractions)
  }
  return attractions
}

const handleSearch = () => {
  currentPage.value = 1
}

const handleReset = () => {
  searchForm.title = ''
  searchForm.destination = ''
  searchForm.status = ''
  searchForm.dateRange = null
  currentPage.value = 1
}

const handleAdd = () => {
  ElMessage.info('创建计划功能开发中')
}

const handleView = (row) => {
  currentPlan.value = row
  detailDialogVisible.value = true
}

const handleEdit = (row) => {
  ElMessage.info('编辑计划功能开发中')
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这个旅游计划吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const index = plans.value.findIndex(plan => plan.id === row.id)
    if (index > -1) {
      plans.value.splice(index, 1)
      ElMessage.success('删除成功')
    }
  } catch {
    // 用户取消删除
  }
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedPlans.value.length} 个计划吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    selectedPlans.value.forEach(plan => {
      const index = plans.value.findIndex(p => p.id === plan.id)
      if (index > -1) {
        plans.value.splice(index, 1)
      }
    })
    
    selectedPlans.value = []
    ElMessage.success('批量删除成功')
  } catch {
    // 用户取消删除
  }
}

const handleExport = () => {
  ElMessage.info('导出功能开发中')
}

const handleSelectionChange = (selection) => {
  selectedPlans.value = selection
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
.travel-plan-management {
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

.plan-detail {
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

/* 表格样式覆盖 - 使用深色主题背景 */
.travel-plan-management :deep(.el-table) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  border: 1px solid var(--border-primary) !important;
  border-radius: 12px !important;
  overflow: hidden !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5) !important;
}

.travel-plan-management :deep(.el-table__header) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  border-bottom: 2px solid var(--primary-color) !important;
}

.travel-plan-management :deep(.el-table__header th) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  color: #ffffff !important;
  font-weight: 600 !important;
  text-align: center !important;
  border: none !important;
  border-right: 1px solid rgba(255, 255, 255, 0.2) !important;
  padding: 16px 8px !important;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3) !important;
}

.travel-plan-management :deep(.el-table__header th:last-child) {
  border-right: none !important;
}

/* 确保表头单元格没有白色背景 */
.travel-plan-management :deep(.el-table__header-wrapper) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
}

.travel-plan-management :deep(.el-table__header-wrapper .el-table__header) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
}

.travel-plan-management :deep(.el-table__header-wrapper .el-table__header th) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
}

/* 强制覆盖所有可能的表头白色背景 */
.travel-plan-management :deep(.el-table thead) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
}

.travel-plan-management :deep(.el-table thead tr) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
}

.travel-plan-management :deep(.el-table thead tr th) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  color: #ffffff !important;
}

/* 覆盖Element Plus默认样式 */
.travel-plan-management :deep(.el-table__header-wrapper .el-table__header thead tr th) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  color: #ffffff !important;
}

/* 确保表头容器也是深色 */
.travel-plan-management :deep(.el-table__header-wrapper) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
}

.travel-plan-management :deep(.el-table__header-wrapper .el-table__header) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
}

.travel-plan-management :deep(.el-table__body) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
}

.travel-plan-management :deep(.el-table__body td) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  color: #e0e0e0 !important;
  border: none !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1) !important;
  padding: 12px 8px !important;
  text-align: center !important;
}

/* 表格行悬停效果 */
.travel-plan-management :deep(.el-table__body tr:hover td) {
  background: linear-gradient(135deg, #2a2a4e, #1e2a4e) !important;
  color: var(--primary-color) !important;
  box-shadow: inset 0 0 20px rgba(var(--primary-rgb), 0.3) !important;
  border-bottom: 1px solid var(--primary-color) !important;
}

/* 表格选中行样式 */
.travel-plan-management :deep(.el-table__row.current-row td) {
  background: linear-gradient(135deg, #2a2a4e, #1e2a4e) !important;
  color: var(--primary-color) !important;
  box-shadow: inset 0 0 15px rgba(var(--primary-rgb), 0.4) !important;
  border-bottom: 1px solid var(--primary-color) !important;
}

/* 分页样式 - 使用深色主题背景 */
.travel-plan-management :deep(.pagination-container) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  border: 1px solid var(--border-primary) !important;
  border-radius: 12px !important;
  padding: 20px !important;
  margin-top: 24px !important;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.4) !important;
}

.travel-plan-management :deep(.el-pagination) {
  justify-content: center !important;
}

/* 搜索表单深色主题样式 */
.travel-plan-management :deep(.search-form .el-form-item__label) {
  color: #e0e0e0 !important;
  font-weight: 500 !important;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5) !important;
}

.travel-plan-management :deep(.search-form .el-input__wrapper) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  border: 1px solid var(--border-primary) !important;
  border-radius: 8px !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3) !important;
  transition: all 0.3s ease !important;
}

.travel-plan-management :deep(.search-form .el-input__wrapper:hover) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 4px 12px rgba(var(--primary-rgb), 0.2) !important;
}

.travel-plan-management :deep(.search-form .el-input__wrapper.is-focus) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 0 0 2px rgba(var(--primary-rgb), 0.2) !important;
}

.travel-plan-management :deep(.search-form .el-input__inner) {
  color: #e0e0e0 !important;
  background: transparent !important;
}

.travel-plan-management :deep(.search-form .el-input__inner::placeholder) {
  color: #888888 !important;
}

.travel-plan-management :deep(.search-form .el-select .el-input__wrapper) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  border: 1px solid var(--border-primary) !important;
  border-radius: 8px !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3) !important;
  transition: all 0.3s ease !important;
}

.travel-plan-management :deep(.search-form .el-select .el-input__wrapper:hover) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 4px 12px rgba(var(--primary-rgb), 0.2) !important;
}

.travel-plan-management :deep(.search-form .el-select .el-input__wrapper.is-focus) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 0 0 2px rgba(var(--primary-rgb), 0.2) !important;
}

.travel-plan-management :deep(.search-form .el-date-editor) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  border: 1px solid var(--border-primary) !important;
  border-radius: 8px !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3) !important;
  transition: all 0.3s ease !important;
}

.travel-plan-management :deep(.search-form .el-date-editor:hover) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 4px 12px rgba(var(--primary-rgb), 0.2) !important;
}

.travel-plan-management :deep(.search-form .el-date-editor.is-active) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 0 0 2px rgba(var(--primary-rgb), 0.2) !important;
}

.travel-plan-management :deep(.search-form .el-date-editor .el-input__inner) {
  color: #e0e0e0 !important;
  background: transparent !important;
}

.travel-plan-management :deep(.search-form .el-date-editor .el-input__inner::placeholder) {
  color: #888888 !important;
}

.travel-plan-management :deep(.search-form .el-button) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  border: 1px solid var(--primary-color) !important;
  color: #ffffff !important;
  border-radius: 8px !important;
  box-shadow: 0 4px 12px rgba(var(--primary-rgb), 0.3) !important;
  transition: all 0.3s ease !important;
}

.travel-plan-management :deep(.search-form .el-button:hover) {
  background: linear-gradient(135deg, var(--secondary-color), var(--primary-color)) !important;
  box-shadow: 0 6px 16px rgba(var(--primary-rgb), 0.4) !important;
  transform: translateY(-2px) !important;
}

.travel-plan-management :deep(.search-form .el-button:active) {
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
