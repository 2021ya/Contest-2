<template>
  <div class="attraction-management">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">景点管理</h1>
      <p class="page-description">管理系统景点信息和推荐内容</p>
    </div>

    <!-- 搜索表单 -->
    <div class="search-form">
      <el-form :model="searchForm" inline>
        <el-form-item label="景点名称">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入景点名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="城市">
          <el-input
            v-model="searchForm.city"
            placeholder="请输入城市"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="类型">
          <el-select
            v-model="searchForm.type"
            placeholder="请选择类型"
            clearable
            style="width: 150px"
          >
            <el-option label="全部" value="" />
            <el-option label="博物馆" value="MUSEUM" />
            <el-option label="公园" value="PARK" />
            <el-option label="古迹" value="MONUMENT" />
            <el-option label="建筑" value="BUILDING" />
            <el-option label="宗教场所" value="RELIGIOUS" />
            <el-option label="娱乐场所" value="ENTERTAINMENT" />
            <el-option label="购物" value="SHOPPING" />
            <el-option label="文化" value="CULTURAL" />
            <el-option label="历史" value="HISTORIC" />
            <el-option label="花园" value="GARDEN" />
          </el-select>
        </el-form-item>
        <el-form-item label="评分">
          <el-select
            v-model="searchForm.rating"
            placeholder="请选择评分"
            clearable
            style="width: 120px"
          >
            <el-option label="全部" value="" />
            <el-option label="4.5分以上" value="4.5" />
            <el-option label="4.0分以上" value="4.0" />
            <el-option label="3.5分以上" value="3.5" />
            <el-option label="3.0分以上" value="3.0" />
          </el-select>
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
        添加景点
      </el-button>
      <el-button type="danger" :disabled="!selectedAttractions.length" @click="handleBatchDelete">
        <el-icon><Delete /></el-icon>
        批量删除
      </el-button>
      <el-button type="success" @click="handleImport">
        <el-icon><Upload /></el-icon>
        批量导入
      </el-button>
      <el-button type="warning" @click="handleExport">
        <el-icon><Download /></el-icon>
        导出数据
      </el-button>
    </div>

    <!-- 景点表格 -->
    <div class="content-card">
      <div class="card-header">景点列表</div>
      <div class="card-body">
        <el-table
          :data="filteredAttractions"
          v-loading="loading"
          @selection-change="handleSelectionChange"
          style="width: 100%"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column label="景点图片" width="100">
            <template #default="{ row }">
              <el-image
                :src="row.imageUrl || '/default-attraction.jpg'"
                :preview-src-list="[row.imageUrl || '/default-attraction.jpg']"
                style="width: 60px; height: 40px; border-radius: 4px;"
                fit="cover"
              />
            </template>
          </el-table-column>
          <el-table-column prop="name" label="景点名称" width="200" show-overflow-tooltip />
          <el-table-column prop="city" label="城市" width="100" />
          <el-table-column label="类型" width="120">
            <template #default="{ row }">
              <el-tag :type="getTypeColor(row.type)">
                {{ getTypeText(row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="评分" width="100">
            <template #default="{ row }">
              <el-rate
                v-model="row.rating"
                disabled
                show-score
                text-color="#ff9900"
                score-template="{value}"
              />
            </template>
          </el-table-column>
          <el-table-column prop="visitDuration" label="游览时间(分钟)" width="120" />
          <el-table-column prop="ticketPrice" label="门票价格" width="100">
            <template #default="{ row }">
              <span v-if="row.ticketPrice > 0">¥{{ row.ticketPrice }}</span>
              <span v-else class="free-ticket">免费</span>
            </template>
          </el-table-column>
          <el-table-column prop="openingHours" label="开放时间" width="150" show-overflow-tooltip />
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
            :total="totalAttractions"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>

    <!-- 景点详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="景点详情"
      width="800px"
    >
      <div v-if="currentAttraction" class="attraction-detail">
        <div class="detail-header">
          <el-image
            :src="currentAttraction.imageUrl || '/default-attraction.jpg'"
            style="width: 200px; height: 150px; border-radius: 8px;"
            fit="cover"
          />
          <div class="detail-info">
            <h2>{{ currentAttraction.name }}</h2>
            <p class="description">{{ currentAttraction.description }}</p>
            <div class="detail-tags">
              <el-tag :type="getTypeColor(currentAttraction.type)">
                {{ getTypeText(currentAttraction.type) }}
              </el-tag>
              <el-tag type="info">{{ currentAttraction.city }}</el-tag>
              <el-tag v-if="currentAttraction.ticketPrice > 0" type="warning">
                ¥{{ currentAttraction.ticketPrice }}
              </el-tag>
              <el-tag v-else type="success">免费</el-tag>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h3>基本信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="景点名称">{{ currentAttraction.name }}</el-descriptions-item>
            <el-descriptions-item label="城市">{{ currentAttraction.city }}</el-descriptions-item>
            <el-descriptions-item label="地址">{{ currentAttraction.address }}</el-descriptions-item>
            <el-descriptions-item label="类型">{{ getTypeText(currentAttraction.type) }}</el-descriptions-item>
            <el-descriptions-item label="评分">
              <el-rate
                v-model="currentAttraction.rating"
                disabled
                show-score
                text-color="#ff9900"
                score-template="{value}分"
              />
            </el-descriptions-item>
            <el-descriptions-item label="游览时间">{{ currentAttraction.visitDuration }}分钟</el-descriptions-item>
            <el-descriptions-item label="门票价格">
              <span v-if="currentAttraction.ticketPrice > 0">¥{{ currentAttraction.ticketPrice }}</span>
              <span v-else>免费</span>
            </el-descriptions-item>
            <el-descriptions-item label="开放时间">{{ currentAttraction.openingHours }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ currentAttraction.phone || '暂无' }}</el-descriptions-item>
            <el-descriptions-item label="官方网站">
              <el-link v-if="currentAttraction.website" :href="currentAttraction.website" target="_blank">
                {{ currentAttraction.website }}
              </el-link>
              <span v-else>暂无</span>
            </el-descriptions-item>
          </el-descriptions>
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
const selectedAttractions = ref([])
const currentAttraction = ref(null)

// 搜索表单
const searchForm = reactive({
  name: '',
  city: '',
  type: '',
  rating: ''
})

// 模拟景点数据
const attractions = ref([
  {
    id: 1,
    name: '故宫博物院',
    description: '明清两朝的皇家宫殿，世界文化遗产',
    city: '北京',
    address: '北京市东城区景山前街4号',
    type: 'MUSEUM',
    rating: 4.8,
    visitDuration: 180,
    ticketPrice: 60,
    openingHours: '08:30-17:00',
    phone: '010-85007421',
    website: 'https://www.dpm.org.cn',
    imageUrl: 'https://example.com/forbidden-city.jpg'
  },
  {
    id: 2,
    name: '天安门广场',
    description: '世界最大的城市广场',
    city: '北京',
    address: '北京市东城区东长安街',
    type: 'MONUMENT',
    rating: 4.6,
    visitDuration: 60,
    ticketPrice: 0,
    openingHours: '全天开放',
    phone: '',
    website: '',
    imageUrl: 'https://example.com/tiananmen.jpg'
  },
  {
    id: 3,
    name: '外滩',
    description: '上海最著名的观光景点，万国建筑博览群',
    city: '上海',
    address: '上海市黄浦区中山东一路',
    type: 'MONUMENT',
    rating: 4.7,
    visitDuration: 90,
    ticketPrice: 0,
    openingHours: '全天开放',
    phone: '',
    website: '',
    imageUrl: 'https://example.com/bund.jpg'
  },
  {
    id: 4,
    name: '东方明珠',
    description: '上海地标性建筑，468米高塔',
    city: '上海',
    address: '上海市浦东新区世纪大道1号',
    type: 'BUILDING',
    rating: 4.5,
    visitDuration: 120,
    ticketPrice: 160,
    openingHours: '08:00-21:30',
    phone: '021-58791888',
    website: 'https://www.orientalpearltower.com',
    imageUrl: 'https://example.com/oriental-pearl.jpg'
  },
  {
    id: 5,
    name: '西湖',
    description: '世界文化遗产，中国最著名的湖泊',
    city: '杭州',
    address: '浙江省杭州市西湖区',
    type: 'PARK',
    rating: 4.8,
    visitDuration: 240,
    ticketPrice: 0,
    openingHours: '全天开放',
    phone: '',
    website: '',
    imageUrl: 'https://example.com/west-lake.jpg'
  },
  {
    id: 6,
    name: '雷峰塔',
    description: '白蛇传说的发源地',
    city: '杭州',
    address: '浙江省杭州市西湖区南山路15号',
    type: 'MONUMENT',
    rating: 4.5,
    visitDuration: 90,
    ticketPrice: 40,
    openingHours: '08:00-17:30',
    phone: '0571-87179603',
    website: '',
    imageUrl: 'https://example.com/leifeng-tower.jpg'
  },
  {
    id: 7,
    name: '迪士尼乐园',
    description: '中国大陆首座迪士尼主题乐园',
    city: '上海',
    address: '上海市浦东新区川沙新镇',
    type: 'ENTERTAINMENT',
    rating: 4.6,
    visitDuration: 480,
    ticketPrice: 399,
    openingHours: '09:00-21:00',
    phone: '400-180-0000',
    website: 'https://www.shanghaidisneyresort.com',
    imageUrl: 'https://example.com/disney.jpg'
  },
  {
    id: 8,
    name: '灵隐寺',
    description: '江南著名古刹',
    city: '杭州',
    address: '浙江省杭州市西湖区法云弄1号',
    type: 'RELIGIOUS',
    rating: 4.6,
    visitDuration: 120,
    ticketPrice: 45,
    openingHours: '07:00-18:00',
    phone: '0571-87968665',
    website: '',
    imageUrl: 'https://example.com/lingyin-temple.jpg'
  }
])

// 计算属性
const totalAttractions = computed(() => attractions.value.length)

// 过滤后的景点列表
const filteredAttractions = computed(() => {
  let result = attractions.value

  if (searchForm.name) {
    result = result.filter(attraction => 
      attraction.name.toLowerCase().includes(searchForm.name.toLowerCase())
    )
  }

  if (searchForm.city) {
    result = result.filter(attraction => 
      attraction.city.toLowerCase().includes(searchForm.city.toLowerCase())
    )
  }

  if (searchForm.type) {
    result = result.filter(attraction => attraction.type === searchForm.type)
  }

  if (searchForm.rating) {
    const minRating = parseFloat(searchForm.rating)
    result = result.filter(attraction => attraction.rating >= minRating)
  }

  return result
})

// 方法
const getTypeColor = (type) => {
  const colors = {
    'MUSEUM': 'primary',
    'PARK': 'success',
    'MONUMENT': 'warning',
    'BUILDING': 'info',
    'RELIGIOUS': 'danger',
    'ENTERTAINMENT': 'success',
    'SHOPPING': 'warning',
    'CULTURAL': 'primary',
    'HISTORIC': 'info',
    'GARDEN': 'success'
  }
  return colors[type] || 'info'
}

const getTypeText = (type) => {
  const texts = {
    'MUSEUM': '博物馆',
    'PARK': '公园',
    'MONUMENT': '古迹',
    'BUILDING': '建筑',
    'RELIGIOUS': '宗教场所',
    'ENTERTAINMENT': '娱乐场所',
    'SHOPPING': '购物',
    'CULTURAL': '文化',
    'HISTORIC': '历史',
    'GARDEN': '花园'
  }
  return texts[type] || type
}

const handleSearch = () => {
  currentPage.value = 1
}

const handleReset = () => {
  searchForm.name = ''
  searchForm.city = ''
  searchForm.type = ''
  searchForm.rating = ''
  currentPage.value = 1
}

const handleAdd = () => {
  ElMessage.info('添加景点功能开发中')
}

const handleView = (row) => {
  currentAttraction.value = row
  detailDialogVisible.value = true
}

const handleEdit = (row) => {
  ElMessage.info('编辑景点功能开发中')
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这个景点吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const index = attractions.value.findIndex(attraction => attraction.id === row.id)
    if (index > -1) {
      attractions.value.splice(index, 1)
      ElMessage.success('删除成功')
    }
  } catch {
    // 用户取消删除
  }
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedAttractions.value.length} 个景点吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    selectedAttractions.value.forEach(attraction => {
      const index = attractions.value.findIndex(a => a.id === attraction.id)
      if (index > -1) {
        attractions.value.splice(index, 1)
      }
    })
    
    selectedAttractions.value = []
    ElMessage.success('批量删除成功')
  } catch {
    // 用户取消删除
  }
}

const handleImport = () => {
  ElMessage.info('批量导入功能开发中')
}

const handleExport = () => {
  ElMessage.info('导出功能开发中')
}

const handleSelectionChange = (selection) => {
  selectedAttractions.value = selection
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
.attraction-management {
  padding: 0;
}

.search-form {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.action-buttons {
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.free-ticket {
  color: #52c41a;
  font-weight: 500;
}

.attraction-detail {
  max-height: 600px;
  overflow-y: auto;
}

.detail-header {
  display: flex;
  gap: 20px;
  margin-bottom: 24px;
}

.detail-info h2 {
  margin: 0 0 8px 0;
  color: #262626;
  font-size: 20px;
  font-weight: 600;
}

.description {
  margin: 0 0 12px 0;
  color: #8c8c8c;
  font-size: 14px;
  line-height: 1.5;
}

.detail-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
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
.attraction-management :deep(.el-table) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  border: 1px solid var(--border-primary) !important;
  border-radius: 12px !important;
  overflow: hidden !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5) !important;
}

.attraction-management :deep(.el-table__header) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  border-bottom: 2px solid var(--primary-color) !important;
}

.attraction-management :deep(.el-table__header th) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  color: #ffffff !important;
  font-weight: 600 !important;
  text-align: center !important;
  border: none !important;
  border-right: 1px solid rgba(255, 255, 255, 0.2) !important;
  padding: 16px 8px !important;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3) !important;
}

.attraction-management :deep(.el-table__header th:last-child) {
  border-right: none !important;
}

/* 确保表头单元格没有白色背景 */
.attraction-management :deep(.el-table__header-wrapper) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
}

.attraction-management :deep(.el-table__header-wrapper .el-table__header) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
}

.attraction-management :deep(.el-table__header-wrapper .el-table__header th) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
}

/* 强制覆盖所有可能的表头白色背景 */
.attraction-management :deep(.el-table thead) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
}

.attraction-management :deep(.el-table thead tr) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
}

.attraction-management :deep(.el-table thead tr th) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  color: #ffffff !important;
}

/* 覆盖Element Plus默认样式 */
.attraction-management :deep(.el-table__header-wrapper .el-table__header thead tr th) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  color: #ffffff !important;
}

/* 确保表头容器也是深色 */
.attraction-management :deep(.el-table__header-wrapper) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
}

.attraction-management :deep(.el-table__header-wrapper .el-table__header) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
}

.attraction-management :deep(.el-table__body) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
}

.attraction-management :deep(.el-table__body td) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  color: #e0e0e0 !important;
  border: none !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1) !important;
  padding: 12px 8px !important;
  text-align: center !important;
}

/* 表格行悬停效果 */
.attraction-management :deep(.el-table__body tr:hover td) {
  background: linear-gradient(135deg, #2a2a4e, #1e2a4e) !important;
  color: var(--primary-color) !important;
  box-shadow: inset 0 0 20px rgba(var(--primary-rgb), 0.3) !important;
  border-bottom: 1px solid var(--primary-color) !important;
}

/* 表格选中行样式 */
.attraction-management :deep(.el-table__row.current-row td) {
  background: linear-gradient(135deg, #2a2a4e, #1e2a4e) !important;
  color: var(--primary-color) !important;
  box-shadow: inset 0 0 15px rgba(var(--primary-rgb), 0.4) !important;
  border-bottom: 1px solid var(--primary-color) !important;
}

/* 分页样式 - 使用深色主题背景 */
.attraction-management :deep(.pagination-container) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  border: 1px solid var(--border-primary) !important;
  border-radius: 12px !important;
  padding: 20px !important;
  margin-top: 24px !important;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.4) !important;
}

.attraction-management :deep(.el-pagination) {
  justify-content: center !important;
}

@media (max-width: 768px) {
  .search-form .el-form {
    display: block;
  }
  
  .search-form .el-form-item {
    display: block;
    margin-bottom: 16px;
  }
  
  .detail-header {
    flex-direction: column;
  }
}
</style>
